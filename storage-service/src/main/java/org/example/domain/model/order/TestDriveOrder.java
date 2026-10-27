package org.example.domain.model.order;

import lombok.Builder;
import lombok.Getter;
import org.example.domain.model.car.Car;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.TestDriveStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class TestDriveOrder {
    @Builder.Default
    private final String id = UUID.randomUUID().toString();

    private final Customer customer;
    private final Manager manager;
    private final Car car;
    private LocalDateTime requestedTime;
    private TestDriveStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private TestDriveOrder(String id, Customer customer, Manager manager, Car car,
                           LocalDateTime requestedTime, TestDriveStatus status,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.customer = customer;
        this.manager = manager;
        this.car = car;
        this.requestedTime = requestedTime;
        this.status = status != null ? status : TestDriveStatus.REQUESTED;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validate();
    }

    private void validate() {

        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (manager == null) {
            throw new IllegalArgumentException("Manager is required");
        }
        if (car == null) {
            throw new IllegalArgumentException("Car is required");
        }
        if (requestedTime == null) {
            throw new IllegalArgumentException("Requested time is required");
        }
        if (requestedTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Requested time cannot be in the past");
        }
        if (!car.isForTestDrive()) {
            throw new IllegalArgumentException("Car is not available for test drive");
        }
    }

    public TestDriveOrder confirm() {

        if (status != TestDriveStatus.REQUESTED) {
            throw new IllegalStateException("Can only confirm REQUESTED test drive");
        }
        this.status = TestDriveStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDriveOrder cancel() {

        if (status == TestDriveStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed test drive");
        }
        this.status = TestDriveStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDriveOrder complete() {

        if (status != TestDriveStatus.CONFIRMED) {
            throw new IllegalStateException("Can only complete CONFIRMED test drive");
        }
        this.status = TestDriveStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDriveOrder markNoShow() {

        if (status != TestDriveStatus.CONFIRMED) {
            throw new IllegalStateException("Can only mark no-show for CONFIRMED test drive");
        }
        this.status = TestDriveStatus.NO_SHOW;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDriveOrder reschedule(LocalDateTime newTime) {

        if (status == TestDriveStatus.COMPLETED || status == TestDriveStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule completed or cancelled test drive");
        }
        if (newTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("New time cannot be in the past");
        }

        this.requestedTime = newTime;
        this.status = TestDriveStatus.REQUESTED;
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public boolean canStartNow() {

        return status == TestDriveStatus.CONFIRMED &&
                requestedTime.isBefore(LocalDateTime.now().plusMinutes(15)) &&
                requestedTime.isAfter(LocalDateTime.now().minusMinutes(15));
    }

    public String getOrderInfo() {

        return String.format(
                "Тест-драйв #%s\n" +
                        "Клиент: %s %s\n" +
                        "Менеджер: %s %s\n" +
                        "Автомобиль: %s %s (%d)\n" +
                        "Время: %s\n" +
                        "Статус: %s",
                id.substring(0, 8),
                customer.getFirstName(), customer.getLastName(),
                manager.getFirstName(), manager.getLastName(),
                car.getBrand(), car.getModel(), car.getYear(),
                requestedTime,
                status
        );
    }

    @Override
    public String toString() {

        return String.format("TestDriveOrder: %s %s - %s %s (%s)",
                customer.getLastName(), customer.getFirstName(),
                car.getBrand(), car.getModel(),
                status);
    }
}