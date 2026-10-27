package org.example.integration.rest.dto.configuration;

import org.example.domain.model.enums.ComponentType;

import java.math.BigDecimal;

public record ComponentDto(
        String id,
        String name,
        ComponentType type,
        String description,
        BigDecimal price,
        boolean base
) {
}
