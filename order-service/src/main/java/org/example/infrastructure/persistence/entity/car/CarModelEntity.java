package org.example.infrastructure.persistence.entity.car;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.TransmissionType;
import org.example.infrastructure.persistence.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car_models")
public class CarModelEntity extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String brand;

    @Column(nullable = false, length = 128)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BodyType bodyType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal basePrice;

    @ElementCollection
    @CollectionTable(name = "car_model_years", joinColumns = @JoinColumn(name = "car_model_id"))
    @Column(name = "year_value", nullable = false)
    private Set<Integer> availableYears = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "car_model_engines", joinColumns = @JoinColumn(name = "car_model_id"))
    private Set<EngineEmbeddable> availableEngines = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "car_model_transmissions", joinColumns = @JoinColumn(name = "car_model_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false, length = 32)
    private Set<TransmissionType> availableTransmissions = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "car_model_drives", joinColumns = @JoinColumn(name = "car_model_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "drive_type", nullable = false, length = 32)
    private Set<DriveType> availableDrives = new HashSet<>();

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Set<Integer> getAvailableYears() {
        return availableYears;
    }

    public void setAvailableYears(Set<Integer> availableYears) {
        this.availableYears = availableYears;
    }

    public Set<EngineEmbeddable> getAvailableEngines() {
        return availableEngines;
    }

    public void setAvailableEngines(Set<EngineEmbeddable> availableEngines) {
        this.availableEngines = availableEngines;
    }

    public Set<TransmissionType> getAvailableTransmissions() {
        return availableTransmissions;
    }

    public void setAvailableTransmissions(Set<TransmissionType> availableTransmissions) {
        this.availableTransmissions = availableTransmissions;
    }

    public Set<DriveType> getAvailableDrives() {
        return availableDrives;
    }

    public void setAvailableDrives(Set<DriveType> availableDrives) {
        this.availableDrives = availableDrives;
    }
}
