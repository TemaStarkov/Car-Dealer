package org.example.domain.model.car;

import lombok.Builder;
import lombok.Getter;
import org.example.domain.model.enums.*;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Builder
public class SearchFilters {
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final String brand;
    private final String model;
    private final BodyType bodyType;
    private final FuelType fuelType;
    private final Double minPower;
    private final Double maxPower;
    private final Double minVolume;
    private final Double maxVolume;
    private final TransmissionType transmissionType;
    private final DriveType driveType;
    private final String color;

    public boolean matches(Car car) {
        return matchesPrice(car) &&
                matchesBrand(car) &&
                matchesModel(car) &&
                matchesBodyType(car) &&
                matchesFuelType(car) &&
                matchesPower(car) &&
                matchesVolume(car) &&
                matchesTransmission(car) &&
                matchesDrive(car) &&
                matchesColor(car);
    }

    private boolean matchesPrice(Car car) {
        if (minPrice != null && car.getPrice().compareTo(minPrice) < 0) return false;
        if (maxPrice != null && car.getPrice().compareTo(maxPrice) > 0) return false;
        return true;
    }

    private boolean matchesBrand(Car car) {
        return brand == null || brand.equalsIgnoreCase(car.getBrand());
    }

    private boolean matchesModel(Car car) {
        return model == null || model.equalsIgnoreCase(car.getModel());
    }

    private boolean matchesBodyType(Car car) {
        return bodyType == null || bodyType == car.getBodyType();
    }

    private boolean matchesFuelType(Car car) {
        return fuelType == null || fuelType == car.getFuelType();
    }

    private boolean matchesPower(Car car) {

        double power = car.getEngine().getPower();
        if (minPower != null && power < minPower) return false;
        if (maxPower != null && power > maxPower) return false;
        return true;
    }

    private boolean matchesVolume(Car car) {

        double volume = car.getEngine().getVolume();
        if (minVolume != null && volume < minVolume) return false;
        if (maxVolume != null && volume > maxVolume) return false;
        return true;
    }

    private boolean matchesTransmission(Car car) {

        return transmissionType == null || transmissionType == car.getTransmissionType();
    }

    private boolean matchesDrive(Car car) {
        return driveType == null || driveType == car.getDriveType();
    }

    private boolean matchesColor(Car car) {
        return color == null || color.equalsIgnoreCase(car.getColor());
    }

    public Optional<String> getModel() { return Optional.ofNullable(model); }

}