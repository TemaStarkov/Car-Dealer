package org.example.infrastructure.persistence.entity.car;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.domain.model.enums.FuelType;

@Embeddable
public class EngineEmbeddable {

    @Column(nullable = false)
    private double power;

    @Column(nullable = false)
    private double volume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FuelType fuelType;

    @Column(nullable = false)
    private int cylinders;

    @Column(length = 128)
    private String model;

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public int getCylinders() {
        return cylinders;
    }

    public void setCylinders(int cylinders) {
        this.cylinders = cylinders;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
