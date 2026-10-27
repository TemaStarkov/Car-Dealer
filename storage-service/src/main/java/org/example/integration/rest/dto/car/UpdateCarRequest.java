package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.CarStatus;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.FuelType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;

public record UpdateCarRequest(
        String brand,
        String model,
        Integer year,
        BodyType bodyType,
        FuelType fuelType,
        TransmissionType transmissionType,
        DriveType driveType,
        Double power,
        Double volume,
        String color,
        BigDecimal price,
        CarStatus status
) {
}
