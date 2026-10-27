package org.example.application.service.impl;

import org.example.application.service.TestDriveService;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.model.order.TestDriveOrder;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.user.User;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.example.infrastructure.persistence.entity.order.TestDriveOrderEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.TestDriveOrderEntityRepository;
import org.example.infrastructure.persistence.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TestDriveServiceImpl implements TestDriveService {

    private final TestDriveOrderEntityRepository testDriveRepository;
    private final UserEntityRepository userRepository;
    private final CarEntityRepository carRepository;
    private final PersistenceMapper persistenceMapper;

    public TestDriveServiceImpl(TestDriveOrderEntityRepository testDriveRepository,
                                UserEntityRepository userRepository,
                                CarEntityRepository carRepository,
                                PersistenceMapper persistenceMapper) {

        this.testDriveRepository = testDriveRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public TestDriveOrder requestTestDrive(String customerId, String carId, LocalDateTime requestedTime) {

        UserEntity customerEntity = userRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(customerId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));
        Customer customer = (Customer) persistenceMapper.toDomain(customerEntity);

        CarEntity carEntity = carRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(carId))
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
        var car = persistenceMapper.toDomain(carEntity);

        if (!car.isForTestDrive()) {
            throw new IllegalStateException("Car is not available for test drive");
        }

        UserEntity managerEntity = userRepository.findAllByRoleAndActiveTrueAndRemovedFalse(org.example.domain.model.enums.UserRole.MANAGER)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No managers available"));
        Manager manager = (Manager) persistenceMapper.toDomain(managerEntity);

        TestDriveOrder order = TestDriveOrder.builder()
                .customer(customer)
                .manager(manager)
                .car(car)
                .requestedTime(requestedTime)
                .build();

        return persistenceMapper.toDomain(
                testDriveRepository.save(
                        persistenceMapper.toEntity(order, customerEntity, managerEntity, carEntity)
                )
        );
    }

    @Override
    public TestDriveOrder confirmTestDrive(String orderId) {

        TestDriveOrderEntity entity = testDriveRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Test drive order not found: " + orderId));
        TestDriveOrder order = persistenceMapper.toDomain(entity);

        order.confirm();
        return saveTestDriveOrder(order, entity);
    }

    @Override
    public TestDriveOrder completeTestDrive(String orderId) {

        TestDriveOrderEntity entity = testDriveRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Test drive order not found: " + orderId));
        TestDriveOrder order = persistenceMapper.toDomain(entity);

        order.complete();
        return saveTestDriveOrder(order, entity);
    }

    @Override
    public TestDriveOrder cancelTestDrive(String orderId) {

        TestDriveOrderEntity entity = testDriveRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Test drive order not found: " + orderId));
        TestDriveOrder order = persistenceMapper.toDomain(entity);

        order.cancel();
        return saveTestDriveOrder(order, entity);
    }

    @Override
    public TestDriveOrder markNoShow(String orderId) {

        TestDriveOrderEntity entity = testDriveRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Test drive order not found: " + orderId));
        TestDriveOrder order = persistenceMapper.toDomain(entity);

        order.markNoShow();
        return saveTestDriveOrder(order, entity);
    }

    @Override
    public List<TestDriveOrder> getTestDrivesByDate(LocalDate date) {

        return testDriveRepository.findAllByRequestedTimeBetweenAndRemovedFalse(
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                ).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<TestDriveOrder> getTestDrivesByCustomer(String customerId) {

        User user = persistenceMapper.toDomain(
                userRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(customerId))
                        .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId))
        );
        Customer customer = (Customer) user;

        UserEntity customerEntity = persistenceMapper.toEntity(customer);
        return testDriveRepository.findAllByCustomerAndRemovedFalse(customerEntity).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    public TestDriveOrder rescheduleTestDrive(String orderId, LocalDateTime newTime) {

        TestDriveOrderEntity entity = testDriveRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Test drive order not found: " + orderId));
        TestDriveOrder order = persistenceMapper.toDomain(entity);

        order.reschedule(newTime);
        return saveTestDriveOrder(order, entity);
    }

    private TestDriveOrder saveTestDriveOrder(TestDriveOrder domainOrder, TestDriveOrderEntity sourceEntity) {
        TestDriveOrderEntity entity = persistenceMapper.toEntity(
                domainOrder,
                sourceEntity.getCustomer(),
                sourceEntity.getManager(),
                sourceEntity.getCar()
        );
        entity.setCreatedAt(sourceEntity.getCreatedAt());
        entity.setUpdatedAt(sourceEntity.getUpdatedAt());
        entity.setRemoved(sourceEntity.isRemoved());
        return persistenceMapper.toDomain(testDriveRepository.save(entity));
    }
}
