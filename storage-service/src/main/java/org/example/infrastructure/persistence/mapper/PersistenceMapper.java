package org.example.infrastructure.persistence.mapper;

import org.example.domain.model.car.Car;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.Engine;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;
import org.example.domain.model.order.CustomOrder;
import org.example.domain.model.order.InStockOrder;
import org.example.domain.model.order.Order;
import org.example.domain.model.order.TestDriveOrder;
import org.example.domain.model.part.Part;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.user.SystemAdmin;
import org.example.domain.model.user.User;
import org.example.domain.model.user.WarehouseAdmin;
import org.example.domain.model.enums.OrderType;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.example.infrastructure.persistence.entity.car.EngineEmbeddable;
import org.example.infrastructure.persistence.entity.car.configuration.CarConfigurationComponentEntity;
import org.example.infrastructure.persistence.entity.car.configuration.CarConfigurationEntity;
import org.example.infrastructure.persistence.entity.car.configuration.ComponentEntity;
import org.example.infrastructure.persistence.entity.order.CustomOrderEntity;
import org.example.infrastructure.persistence.entity.order.InStockOrderEntity;
import org.example.infrastructure.persistence.entity.order.OrderEntity;
import org.example.infrastructure.persistence.entity.order.TestDriveOrderEntity;
import org.example.infrastructure.persistence.entity.part.PartEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PersistenceMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    Car toDomain(CarEntity entity);

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    CarModel toDomain(CarModelEntity entity);

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    Part toDomain(PartEntity entity);

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "isBase", source = "base")
    Component toDomain(ComponentEntity entity);

    Engine toDomain(EngineEmbeddable embeddable);

    Set<Engine> toEngineDomainSet(Set<EngineEmbeddable> embeddables);

    default User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return switch (entity.getRole()) {
            case USER -> toCustomerDomain(entity);
            case MANAGER -> toManagerDomain(entity);
            case WAREHOUSE_ADMIN -> toWarehouseAdminDomain(entity);
            case ADMIN -> toSystemAdminDomain(entity);
        };
    }

    default Order<?> toDomain(OrderEntity entity) {
        if (entity instanceof InStockOrderEntity inStockOrderEntity) {
            return toDomain(inStockOrderEntity);
        }
        if (entity instanceof CustomOrderEntity customOrderEntity) {
            return toDomain(customOrderEntity);
        }
        throw new IllegalArgumentException("Unsupported order entity type: " + entity.getClass().getName());
    }

    default CarConfiguration toDomain(CarConfigurationEntity entity) {
        Map<ComponentType, Component> components = entity.getComponents().stream()
                .collect(Collectors.toMap(
                        CarConfigurationComponentEntity::getComponentType,
                        componentEntity -> toDomain(componentEntity.getComponent()),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));

        return CarConfiguration.builder()
                .id(entity.getId().toString())
                .model(toDomain(entity.getModel()))
                .components(components)
                .totalPrice(entity.getTotalPrice())
                .isValid(entity.isValid())
                .validationMessage(entity.getValidationMessage())
                .build();
    }

    default InStockOrder toDomain(InStockOrderEntity entity) {
        return InStockOrder.builder()
                .id(entity.getId().toString())
                .orderNumber(entity.getOrderNumber())
                .customer(toCustomerDomain(entity.getCustomer()))
                .manager(toManagerDomain(entity.getManager()))
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .status(entity.getStatus())
                .type(OrderType.IN_STOCK)
                .car(toDomain(entity.getCar()))
                .build();
    }

    default CustomOrder toDomain(CustomOrderEntity entity) {
        return CustomOrder.builder()
                .id(entity.getId().toString())
                .orderNumber(entity.getOrderNumber())
                .customer(toCustomerDomain(entity.getCustomer()))
                .manager(toManagerDomain(entity.getManager()))
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .status(entity.getStatus())
                .type(OrderType.CUSTOM)
                .carModel(toDomain(entity.getCarModel()))
                .configuration(toDomain(entity.getConfiguration()))
                .expectedDeliveryDate(entity.getExpectedDeliveryDate())
                .deliveryDate(entity.getDeliveryDate())
                .build();
    }

    default TestDriveOrder toDomain(TestDriveOrderEntity entity) {
        return TestDriveOrder.builder()
                .id(entity.getId().toString())
                .customer(toCustomerDomain(entity.getCustomer()))
                .manager(toManagerDomain(entity.getManager()))
                .car(toDomain(entity.getCar()))
                .requestedTime(entity.getRequestedTime())
                .status(entity.getStatus())
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .build();
    }

    default CarEntity toEntity(Car domain) {
        CarEntity entity = new CarEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setBrand(domain.getBrand());
        entity.setModel(domain.getModel());
        entity.setYear(domain.getYear());
        entity.setBodyType(domain.getBodyType());
        entity.setFuelType(domain.getFuelType());
        entity.setTransmissionType(domain.getTransmissionType());
        entity.setDriveType(domain.getDriveType());
        entity.setEngine(toEmbeddable(domain.getEngine()));
        entity.setColor(domain.getColor());
        entity.setPrice(domain.getPrice());
        entity.setStatus(domain.getStatus());
        entity.setVin(domain.getVin());
        return entity;
    }

    default PartEntity toEntity(Part domain) {
        PartEntity entity = new PartEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setName(domain.getName());
        entity.setPartNumber(domain.getPartNumber());
        entity.setCategory(domain.getCategory());
        entity.setManufacturer(domain.getManufacturer());
        entity.setPrice(domain.getPrice());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setQuantity(domain.getQuantity());
        entity.setLocation(domain.getLocation());
        return entity;
    }

    default ComponentEntity toEntity(Component domain) {
        ComponentEntity entity = new ComponentEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setBase(domain.isBase());
        entity.setCompatibleModels(domain.getCompatibleModels().stream()
                .map(this::toEntity)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new)));
        return entity;
    }

    default CarModelEntity toEntity(CarModel domain) {
        CarModelEntity entity = new CarModelEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setBrand(domain.getBrand());
        entity.setModel(domain.getModel());
        entity.setBodyType(domain.getBodyType());
        entity.setBasePrice(domain.getBasePrice());
        entity.setAvailableYears(new java.util.LinkedHashSet<>(domain.getAvailableYears()));
        entity.setAvailableEngines(domain.getAvailableEngines().stream()
                .map(this::toEmbeddable)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new)));
        entity.setAvailableTransmissions(new java.util.LinkedHashSet<>(domain.getAvailableTransmissions()));
        entity.setAvailableDrives(new java.util.LinkedHashSet<>(domain.getAvailableDrives()));
        return entity;
    }

    default UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setActive(domain.isActive());

        if (domain instanceof Manager manager) {
            entity.setRole(org.example.domain.model.enums.UserRole.MANAGER);
            entity.setEmployeeId(manager.getEmployeeId());
        } else if (domain instanceof WarehouseAdmin warehouseAdmin) {
            entity.setRole(org.example.domain.model.enums.UserRole.WAREHOUSE_ADMIN);
            entity.setWarehouseId(warehouseAdmin.getWarehouseId());
        } else if (domain instanceof Customer) {
            entity.setRole(org.example.domain.model.enums.UserRole.USER);
        } else if (domain instanceof SystemAdmin) {
            entity.setRole(org.example.domain.model.enums.UserRole.ADMIN);
        }

        return entity;
    }

    default TestDriveOrderEntity toEntity(TestDriveOrder domain, UserEntity customer, UserEntity manager, CarEntity car) {
        TestDriveOrderEntity entity = new TestDriveOrderEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setCustomer(customer);
        entity.setManager(manager);
        entity.setCar(car);
        entity.setRequestedTime(domain.getRequestedTime());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    default InStockOrderEntity toEntity(InStockOrder domain, UserEntity customer, UserEntity manager, CarEntity car) {
        InStockOrderEntity entity = new InStockOrderEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setOrderNumber(domain.getOrderNumber());
        entity.setCustomer(customer);
        entity.setManager(manager);
        entity.setStatus(domain.getStatus());
        entity.setType(OrderType.IN_STOCK);
        entity.setCar(car);
        return entity;
    }

    default CustomOrderEntity toEntity(CustomOrder domain,
                                       UserEntity customer,
                                       UserEntity manager,
                                       CarModelEntity carModel,
                                       CarConfigurationEntity configuration) {
        CustomOrderEntity entity = new CustomOrderEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setOrderNumber(domain.getOrderNumber());
        entity.setCustomer(customer);
        entity.setManager(manager);
        entity.setStatus(domain.getStatus());
        entity.setType(OrderType.CUSTOM);
        entity.setCarModel(carModel);
        entity.setConfiguration(configuration);
        entity.setExpectedDeliveryDate(domain.getExpectedDeliveryDate());
        entity.setDeliveryDate(domain.getDeliveryDate());
        return entity;
    }

    default CarConfigurationEntity toEntity(CarConfiguration domain, CarModelEntity modelEntity) {
        CarConfigurationEntity entity = new CarConfigurationEntity();
        if (domain.getId() != null) {
            entity.setId(java.util.UUID.fromString(domain.getId()));
        }
        entity.setModel(modelEntity);
        entity.setTotalPrice(domain.getTotalPrice());
        entity.setValid(domain.isValid());
        entity.setValidationMessage(domain.getValidationMessage());
        return entity;
    }

    default CarConfigurationComponentEntity toEntity(CarConfigurationEntity configuration,
                                                     ComponentType componentType,
                                                     ComponentEntity component) {
        CarConfigurationComponentEntity entity = new CarConfigurationComponentEntity();
        entity.setConfiguration(configuration);
        entity.setComponentType(componentType);
        entity.setComponent(component);
        return entity;
    }

    default EngineEmbeddable toEmbeddable(Engine domain) {
        if (domain == null) {
            return null;
        }
        EngineEmbeddable embeddable = new EngineEmbeddable();
        embeddable.setPower(domain.getPower());
        embeddable.setVolume(domain.getVolume());
        embeddable.setFuelType(domain.getFuelType());
        embeddable.setCylinders(domain.getCylinders());
        embeddable.setModel(domain.getModel());
        return embeddable;
    }

    private org.example.domain.model.user.Customer toCustomerDomain(UserEntity entity) {
        return Customer.builder()
                .id(entity.getId().toString())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .active(entity.isActive())
                .build();
    }

    private Manager toManagerDomain(UserEntity entity) {
        return Manager.builder()
                .id(entity.getId().toString())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .active(entity.isActive())
                .employeeId(entity.getEmployeeId())
                .build();
    }

    private WarehouseAdmin toWarehouseAdminDomain(UserEntity entity) {
        return WarehouseAdmin.builder()
                .id(entity.getId().toString())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .active(entity.isActive())
                .warehouseId(entity.getWarehouseId())
                .build();
    }

    private SystemAdmin toSystemAdminDomain(UserEntity entity) {
        return SystemAdmin.builder()
                .id(entity.getId().toString())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .createdAt(toLocalDateTime(entity.getCreatedAt()))
                .updatedAt(toLocalDateTime(entity.getUpdatedAt()))
                .active(entity.isActive())
                .build();
    }

    private java.time.LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : java.time.LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
    }
}
