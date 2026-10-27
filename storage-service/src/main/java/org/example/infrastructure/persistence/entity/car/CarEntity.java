package org.example.infrastructure.persistence.entity.car;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.CarStatus;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.FuelType;
import org.example.domain.model.enums.TransmissionType;
import org.example.infrastructure.persistence.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
public class CarEntity extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String brand;

    @Column(nullable = false, length = 128)
    private String model;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DriveType driveType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "power", column = @Column(name = "engine_power", nullable = false)),
            @AttributeOverride(name = "volume", column = @Column(name = "engine_volume", nullable = false)),
            @AttributeOverride(name = "fuelType", column = @Column(name = "engine_fuel_type", nullable = false, length = 32)),
            @AttributeOverride(name = "cylinders", column = @Column(name = "engine_cylinders", nullable = false)),
            @AttributeOverride(name = "model", column = @Column(name = "engine_model", length = 128))
    })
    private EngineEmbeddable engine;

    @Column(nullable = false, length = 64)
    private String color;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CarStatus status;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public DriveType getDriveType() {
        return driveType;
    }

    public void setDriveType(DriveType driveType) {
        this.driveType = driveType;
    }

    public EngineEmbeddable getEngine() {
        return engine;
    }

    public void setEngine(EngineEmbeddable engine) {
        this.engine = engine;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
