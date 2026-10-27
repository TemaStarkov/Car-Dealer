package org.example.infrastructure.persistence.entity.car.configuration;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.example.infrastructure.persistence.entity.BaseEntity;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car_configurations")
public class CarConfigurationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModelEntity model;

    @OneToMany(mappedBy = "configuration", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarConfigurationComponentEntity> components = new HashSet<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private boolean valid;

    @Column(length = 512)
    private String validationMessage;

    public CarModelEntity getModel() {
        return model;
    }

    public void setModel(CarModelEntity model) {
        this.model = model;
    }

    public Set<CarConfigurationComponentEntity> getComponents() {
        return components;
    }

    public void setComponents(Set<CarConfigurationComponentEntity> components) {
        this.components = components;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
}
