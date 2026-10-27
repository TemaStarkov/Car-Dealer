package org.example.domain.model.car;

import lombok.Builder;
import lombok.Value;
import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class CarModel {
    String id;
    String brand;
    String model;
    BodyType bodyType;
    BigDecimal basePrice;
    Set<Integer> availableYears;
    Set<Engine> availableEngines;
    Set<TransmissionType> availableTransmissions;
    Set<DriveType> availableDrives;

    private CarModel(String id, String brand, String model, BodyType bodyType,
                     BigDecimal basePrice, Set<Integer> availableYears,
                     Set<Engine> availableEngines,
                     Set<TransmissionType> availableTransmissions,
                     Set<DriveType> availableDrives) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.brand = brand;
        this.model = model;
        this.bodyType = bodyType;
        this.basePrice = basePrice;
        this.availableYears = availableYears != null ? availableYears : Set.of();
        this.availableEngines = availableEngines != null ? availableEngines : Set.of();
        this.availableTransmissions = availableTransmissions != null ? availableTransmissions : Set.of();
        this.availableDrives = availableDrives != null ? availableDrives : Set.of();

        validate();
    }

    private void validate() {

        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Brand is required");
        }

        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model is required");
        }

        if (bodyType == null) {
            throw new IllegalArgumentException("Body type is required");
        }

        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }

        if (availableEngines.isEmpty()) {
            throw new IllegalArgumentException("At least one engine must be available");
        }
    }

    public String getFullName() {
        return brand + " " + model;
    }

    public boolean yearAvailable(int year) {

        return availableYears.contains(year);
    }

    public boolean engineAvailable(Engine engine) {

        return availableEngines.contains(engine);
    }

    public boolean transmissionAvailable(TransmissionType transmission) {

        return availableTransmissions.contains(transmission);
    }

    public boolean driveAvailable(DriveType drive) {

        return availableDrives.contains(drive);
    }
}