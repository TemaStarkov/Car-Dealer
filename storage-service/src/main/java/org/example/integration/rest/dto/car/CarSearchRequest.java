package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.FuelType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;

public record CarSearchRequest(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String brand,
        String model,
        BodyType bodyType,
        FuelType fuelType,
        Double minPower,
        Double maxPower,
        Double minVolume,
        Double maxVolume,
        TransmissionType transmissionType,
        DriveType driveType,
        String color
) {
}
