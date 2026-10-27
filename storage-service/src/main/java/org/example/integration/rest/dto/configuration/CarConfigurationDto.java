package org.example.integration.rest.dto.configuration;

import org.example.integration.rest.dto.car.CarModelDto;

import java.math.BigDecimal;
import java.util.Map;

public record CarConfigurationDto(
        String id,
        CarModelDto model,
        Map<String, ComponentDto> components,
        BigDecimal totalPrice,
        boolean valid,
        String validationMessage
) {
}
