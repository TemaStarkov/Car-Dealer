package org.example.integration.rest.dto.car;

import org.example.domain.model.enums.BodyType;
import org.example.domain.model.enums.DriveType;
import org.example.domain.model.enums.TransmissionType;

import java.math.BigDecimal;
import java.util.Set;

public record CarModelDto(
        String id,
        String brand,
        String model,
        BodyType bodyType,
        BigDecimal basePrice,
        Set<Integer> availableYears,
        Set<EngineDto> availableEngines,
        Set<TransmissionType> availableTransmissions,
        Set<DriveType> availableDrives
) {
}
