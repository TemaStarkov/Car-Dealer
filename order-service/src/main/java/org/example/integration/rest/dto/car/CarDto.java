package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.CarStatus;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.FuelType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;

public record CarDto(
        String id,
        String brand,
        String model,
        int year,
        BodyType bodyType,
        FuelType fuelType,
        TransmissionType transmissionType,
        DriveType driveType,
        EngineDto engine,
        String color,
        BigDecimal price,
        CarStatus status,
        String vin
) {
}
