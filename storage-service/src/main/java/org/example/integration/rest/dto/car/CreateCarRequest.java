package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.FuelType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;

public record CreateCarRequest(
        String brand,
        String model,
        int year,
        BodyType bodyType,
        FuelType fuelType,
        TransmissionType transmissionType,
        DriveType driveType,
        double power,
        double volume,
        String color,
        BigDecimal price,
        String vin
) {
}
