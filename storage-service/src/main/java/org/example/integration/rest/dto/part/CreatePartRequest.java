package org.example.integration.rest.dto.part;

import org.example.domain.model.enums.PartCategory;

import java.math.BigDecimal;

public record CreatePartRequest(
        String name,
        String partNumber,
        PartCategory category,
        String manufacturer,
        BigDecimal price,
        String description,
        int quantity,
        String location
) {
}
