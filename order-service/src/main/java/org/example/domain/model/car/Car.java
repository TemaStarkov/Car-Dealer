package org.example.domain.model.car;

import lombok.*;
import org.example.domain.model.enums.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class Car {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String brand;
    private String model;
    private int year;
    private BodyType bodyType;
    private FuelType fuelType;
    private TransmissionType transmissionType;
    private DriveType driveType;
    private Engine engine;
    private String color;
    private BigDecimal price;

    @Builder.Default
    private CarStatus status = CarStatus.AVAILABLE;

    private String vin;

    public Car(String id, String brand, String model, int year, BodyType bodyType,
               FuelType fuelType, TransmissionType transmissionType, DriveType driveType,
               Engine engine, String color, BigDecimal price, CarStatus status, String vin) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.bodyType = bodyType;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.driveType = driveType;
        this.engine = engine;
        this.color = color;
        this.price = price;
        this.status = status != null ? status : CarStatus.AVAILABLE;
        this.vin = vin;

        validate();
    }

    private void validate() {

        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be null or empty");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
        if (vin == null || vin.length() != 17) {
            throw new IllegalArgumentException("VIN must be 17 characters");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
        if (engine == null) {
            throw new IllegalArgumentException("Engine cannot be null");
        }
    }

    public boolean isAvailable() {
        return status == CarStatus.AVAILABLE;
    }

    public boolean isForTestDrive() {
        return status == CarStatus.AVAILABLE || status == CarStatus.TEST_DRIVE;
    }

    public Car markAsSold() {

        if (!isAvailable()) {
            throw new IllegalStateException("Car is not available for sale");
        }
        this.status = CarStatus.SOLD;
        return this;
    }

    public Car markForTestDrive() {

        this.status = CarStatus.TEST_DRIVE;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        Car car = (Car) o;
        return vin != null ? vin.equals(car.vin) : id.equals(car.id);
    }

    @Override
    public int hashCode() {

        return vin != null ? vin.hashCode() : id.hashCode();
    }
}