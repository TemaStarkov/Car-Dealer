package org.example.integration.rest.dto.assembly;

import java.util.UUID;

public record CreateAssemblyOrderRequest(
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        UUID carModelId,
        UUID warehouseAdminId
) {
}