package org.example.integration.rest.dto.part;

import org.example.domain.model.enums.PartCategory;
import org.example.domain.model.enums.PartStatus;

import java.math.BigDecimal;

public record UpdatePartRequest(
        String name,
        String partNumber,
        PartCategory category,
        String manufacturer,
        BigDecimal price,
        String description,
        Integer quantity,
        String location,
        PartStatus status
) {
}
