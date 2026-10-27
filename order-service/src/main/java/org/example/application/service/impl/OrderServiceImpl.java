package org.example.application.service.impl;

import org.example.application.service.OrderService;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;
import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;
import org.example.domain.model.order.CustomOrder;
import org.example.domain.model.order.InStockOrder;
import org.example.domain.model.order.Order;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.car.configuration.ConfigurationFactory;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.example.infrastructure.persistence.entity.car.configuration.CarConfigurationEntity;
import org.example.infrastructure.persistence.entity.order.CustomOrderEntity;
import org.example.infrastructure.persistence.entity.order.InStockOrderEntity;
import org.example.infrastructure.persistence.entity.order.OrderEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.CarModelEntityRepository;
import org.example.infrastructure.persistence.repository.ComponentEntityRepository;
import org.example.infrastructure.persistence.repository.OrderEntityRepository;
import org.example.infrastructure.persistence.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.infrastructure.security.CurrentUserService;
import org.example.domain.model.enums.UserRole;
import org.example.application.service.OutboxService;
import org.example.integration.kafka.event.OrderSentForApprovalEvent;
import org.example.infrastructure.persistence.entity.order.TestDriveOrderEntity;

import java.time.Instant;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orderRepository;
    private final UserEntityRepository userRepository;
    private final CarEntityRepository carRepository;
    private final CarModelEntityRepository carModelRepository;
    private final ComponentEntityRepository componentRepository;
    private final PersistenceMapper persistenceMapper;
    private final ConfigurationFactory configurationFactory;
    private final CurrentUserService currentUserService;
    private final OutboxService outboxService;

    public OrderServiceImpl(OrderEntityRepository orderRepository,
                            UserEntityRepository userRepository,
                            CarEntityRepository carRepository,
                            CarModelEntityRepository carModelRepository,
                            ComponentEntityRepository componentRepository,
                            PersistenceMapper persistenceMapper,
                            ConfigurationFactory configurationFactory,
                            CurrentUserService currentUserService,
                            OutboxService outboxService) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.componentRepository = componentRepository;
        this.persistenceMapper = persistenceMapper;
        this.configurationFactory = configurationFactory;
        this.currentUserService = currentUserService;
        this.outboxService = outboxService;
    }

    @Override
    public Order createInStockOrder(String customerId, String carId) {

        UserEntity customerEntity = getCurrentUserEntity();
        Customer customer = (Customer) persistenceMapper.toDomain(customerEntity);

        CarEntity carEntity = carRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(carId))
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
        var car = persistenceMapper.toDomain(carEntity);

        if (!car.isAvailable()) {
            throw new IllegalStateException("Car is not available for sale");
        }

        UserEntity managerEntity = userRepository.findAllByRoleAndActiveTrueAndRemovedFalse(org.example.domain.model.enums.UserRole.MANAGER)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No managers available"));
        Manager manager = (Manager) persistenceMapper.toDomain(managerEntity);

        String orderNumber = generateOrderNumber();

        InStockOrder order = InStockOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .manager(manager)
                .car(car)
                .build();

        OrderEntity savedOrder = orderRepository.save(
                persistenceMapper.toEntity(order, customerEntity, managerEntity, carEntity)
        );

        InStockOrder createdOrder = order.toBuilder()
                .id(savedOrder.getId().toString())
                .createdAt(savedOrder.getCreatedAt() == null ? order.getCreatedAt() :
                        java.time.LocalDateTime.ofInstant(savedOrder.getCreatedAt(), java.time.ZoneOffset.UTC))
                .updatedAt(savedOrder.getUpdatedAt() == null ? order.getUpdatedAt() :
                        java.time.LocalDateTime.ofInstant(savedOrder.getUpdatedAt(), java.time.ZoneOffset.UTC))
                .type(OrderType.IN_STOCK)
                .build();

        car.markAsSold();
        carEntity.setStatus(car.getStatus());
        carRepository.save(carEntity);

        return createdOrder;
    }

    @Override
    public Order createCustomOrder(String customerId, String modelId,
                                   Map<String, String> componentChoices,
                                   LocalDateTime expectedDeliveryDate) {

        UserEntity customerEntity = getCurrentUserEntity();
        Customer customer = (Customer) persistenceMapper.toDomain(customerEntity);

        CarModelEntity carModelEntity = carModelRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(modelId))
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelId));
        CarModel model = persistenceMapper.toDomain(carModelEntity);

        UserEntity managerEntity = userRepository.findAllByRoleAndActiveTrueAndRemovedFalse(org.example.domain.model.enums.UserRole.MANAGER)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No managers available"));
        Manager manager = (Manager) persistenceMapper.toDomain(managerEntity);

        Map<ComponentType, Component> components = new HashMap<>();
        Map<ComponentType, org.example.infrastructure.persistence.entity.car.configuration.ComponentEntity> componentEntities = new HashMap<>();

        for (Map.Entry<String, String> entry : componentChoices.entrySet()) {

            ComponentType type = ComponentType.valueOf(entry.getKey());
            var componentEntity = componentRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(entry.getValue()))
                    .orElseThrow(() -> new EntityNotFoundException("Component not found: " + entry.getValue()));
            Component component = persistenceMapper.toDomain(componentEntity);
            components.put(type, component);
            componentEntities.put(type, componentEntity);
        }

        ComponentType[] requiredTypes = {

                ComponentType.WHEELS,
                ComponentType.TRANSMISSION,
                ComponentType.STEERING_WHEEL,
                ComponentType.INTERIOR
        };

        for (ComponentType type : requiredTypes) {
            if (!components.containsKey(type)) {
                componentRepository.findFirstByTypeAndBaseTrueAndRemovedFalse(type)
                        .ifPresent(base -> {
                            components.put(type, persistenceMapper.toDomain(base));
                            componentEntities.put(type, base);
                        });
            }
        }

        CarConfiguration configuration = configurationFactory.createValid(model, components);

        String orderNumber = generateOrderNumber();

        CustomOrder order = CustomOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .manager(manager)
                .carModel(model)
                .configuration(configuration)
                .expectedDeliveryDate(expectedDeliveryDate)
                .build();

        CarConfigurationEntity configurationEntity = persistenceMapper.toEntity(configuration, carModelEntity);
        configurationEntity.setComponents(componentEntities.entrySet().stream()
                .map(entry -> persistenceMapper.toEntity(configurationEntity, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet()));

        OrderEntity savedOrder = orderRepository.save(
                persistenceMapper.toEntity(order, customerEntity, managerEntity, carModelEntity, configurationEntity)
        );

        return persistenceMapper.toDomain(savedOrder);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {

        OrderEntity orderEntity = orderRepository.findByIdAndRemovedFalse(UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        Order<?> order = persistenceMapper.toDomain(orderEntity);

        switch (newStatus) {
            case APPROVED_BY_MANAGER:
            case APPROVED_BY_WAREHOUSE:
                order.approve();
                orderEntity.setStatus(order.getStatus());
                break;

            case AWAITING_PAYMENT:
                order.awaitPayment();
                orderEntity.setStatus(order.getStatus());
                break;

            case PAID:
                orderEntity.setStatus(OrderStatus.PAID);
                break;

            case CAR_READY_FOR_PICKUP:
                order.readyForPickup();
                orderEntity.setStatus(order.getStatus());
                break;

            case COMPLETED:
                order.complete();
                orderEntity.setStatus(order.getStatus());
                break;

            case CANCELLED:
                if (!order.canBeCancelled()) {
                    throw new IllegalStateException("Order cannot be cancelled at current status");
                }
                order.cancel();
                orderEntity.setStatus(order.getStatus());
                break;

            default:
                throw new IllegalArgumentException("Unsupported status update: " + newStatus);
        }

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        if (newStatus == OrderStatus.PAID) {
            publishOrderSentForApprovalEvent(savedOrder);
        }

        return persistenceMapper.toDomain(savedOrder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getOrdersForCurrentUser() {
        if (currentUserService.hasAnyRole("MANAGER", "ADMIN")) {
            return getAllOrders();
        }

        UserEntity currentUser = getCurrentUserEntity();

        return orderRepository.findAllByCustomerAndRemovedFalse(currentUser).stream()
                .map(persistenceMapper::toDomain)
                .map(order -> (Order) order)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getOrdersByCustomer(String customerId) {

        UserEntity customer = userRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(customerId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));

        return orderRepository.findAllByCustomerAndRemovedFalse(customer).stream()
                .map(persistenceMapper::toDomain)
                .map(order -> (Order) order)
                .collect(Collectors.toList());
    }

    private void publishOrderSentForApprovalEvent(OrderEntity order) {
        OrderSentForApprovalEvent event = new OrderSentForApprovalEvent(
                UUID.randomUUID(),
                order.getId(),
                extractOrderType(order),
                extractCarId(order),
                extractCarModelId(order),
                extractComponentIds(order),
                UUID.randomUUID(),
                Instant.now()
        );

        outboxService.saveEvent(
                order.getId(),
                "OrderSentForApproval",
                event
        );
    }

    private String extractOrderType(OrderEntity order) {
        if (order instanceof InStockOrderEntity) {
            return "IN_STOCK";
        }

        if (order instanceof CustomOrderEntity) {
            return "CUSTOM";
        }

        return "UNKNOWN";
    }

    private UUID extractCarId(OrderEntity order) {
        if (order instanceof InStockOrderEntity inStockOrder) {
            if (inStockOrder.getCar() != null) {
                return inStockOrder.getCar().getId();
            }
        }

        return null;
    }

    private UUID extractCarModelId(OrderEntity order) {
        return null;
    }

    private List<UUID> extractComponentIds(OrderEntity order) {
        if (order instanceof CustomOrderEntity customOrder) {
            if (customOrder.getConfiguration() != null
                    && customOrder.getConfiguration().getComponents() != null) {
                return customOrder.getConfiguration().getComponents()
                        .stream()
                        .map(component -> component.getId())
                        .toList();
            }
        }

        return List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getOrdersByManager(String managerId) {

        UserEntity manager = userRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(managerId))
                .orElseThrow(() -> new EntityNotFoundException("Manager not found: " + managerId));

        return orderRepository.findAllByManagerAndRemovedFalse(manager).stream()
                .map(persistenceMapper::toDomain)
                .map(order -> (Order) order)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getAllOrders() {

        return orderRepository.findAllByRemovedFalse().stream()
                .map(persistenceMapper::toDomain)
                .map(order -> (Order) order)
                .collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(String id) {

        return orderRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(id))
                .map(persistenceMapper::toDomain)
                .map(order -> (Order) order)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    @Override
    public Order cancelOrder(String id) {

        OrderEntity orderEntity = orderRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        Order<?> order = persistenceMapper.toDomain(orderEntity);

        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled at current status");
        }

        order.cancel();
        orderEntity.setStatus(order.getStatus());
        return persistenceMapper.toDomain(orderRepository.save(orderEntity));
    }

    private String generateOrderNumber() {

        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private UserEntity getCurrentUserEntity() {
        UUID keycloakId = currentUserService.getCurrentUserId();

        return userRepository.findByKeycloakIdAndRemovedFalse(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found in database: " + keycloakId));
    }
}
