package org.example.infrastructure.persistence.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.example.domain.model.enums.TestDriveStatus;
import org.example.infrastructure.persistence.entity.BaseEntity;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drive_orders")
public class TestDriveOrderEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private UserEntity manager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    @Column(nullable = false)
    private LocalDateTime requestedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TestDriveStatus status;

    public UserEntity getCustomer() {
        return customer;
    }

    public void setCustomer(UserEntity customer) {
        this.customer = customer;
    }

    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }

    public CarEntity getCar() {
        return car;
    }

    public void setCar(CarEntity car) {
        this.car = car;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public TestDriveStatus getStatus() {
        return status;
    }

    public void setStatus(TestDriveStatus status) {
        this.status = status;
    }
}
