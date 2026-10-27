package org.example.integration.rest.dto.assembly;

import org.example.domain.model.enums.AssemblyOrderStatus;

import java.util.UUID;

public record UpdateAssemblyOrderRequest(
        AssemblyOrderStatus status,
        UUID warehouseAdminId
) {
}