package org.example.infrastructure.persistence.entity.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.example.infrastructure.persistence.entity.car.configuration.CarConfigurationEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_orders")
public class CustomOrderEntity extends OrderEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModelEntity carModel;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "configuration_id", nullable = false)
    private CarConfigurationEntity configuration;

    @jakarta.persistence.Column(nullable = false)
    private LocalDateTime expectedDeliveryDate;

    private LocalDateTime deliveryDate;

    public CarModelEntity getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModelEntity carModel) {
        this.carModel = carModel;
    }

    public CarConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CarConfigurationEntity configuration) {
        this.configuration = configuration;
    }

    public LocalDateTime getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
