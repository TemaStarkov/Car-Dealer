package org.example.domain.model.testdrive;

import lombok.Builder;
import lombok.Getter;
import org.example.domain.model.car.Car;
import org.example.domain.model.user.Customer;
import org.example.domain.model.enums.TestDriveStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class TestDrive{
    @Builder.Default
    private final String id = UUID.randomUUID().toString();

    private final Customer customer;
    private final Car car;
    private final LocalDateTime requestedTime;
    private TestDriveStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private TestDrive(String id, Customer customer, Car car,
                             LocalDateTime requestedTime, TestDriveStatus status,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.customer = customer;
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

    public TestDrive confirm() {

        this.status = TestDriveStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDrive cancel() {

        this.status = TestDriveStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDrive complete() {

        this.status = TestDriveStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public TestDrive markNoShow() {

        this.status = TestDriveStatus.NO_SHOW;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    @Override
    public String toString() {

        return String.format("TestDrive: %s %s - %s (%s)",
                car.getBrand(), car.getModel(),
                requestedTime.toLocalDate(), status);
    }
}