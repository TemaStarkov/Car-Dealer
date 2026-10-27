package org.example.integration.rest.dto.assembly;

import org.example.domain.model.enums.AssemblyOrderStatus;

import java.time.Instant;
import java.util.UUID;

public record AssemblyOrderDto(
        UUID id,
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        UUID carModelId,
        UUID warehouseAdminId,
        Instant createdAt,
        Instant updatedAt,
        AssemblyOrderStatus status,
        boolean removed
) {
}