package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.FuelType;

public record EngineDto(
        double power,
        double volume,
        FuelType fuelType,
        int cylinders,
        String model
) {
}
