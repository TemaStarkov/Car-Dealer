package org.example.integration.rest.dto.order;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateCustomOrderRequest(
        String customerId,
        String modelId,
        Map<String, String> componentChoices,
        LocalDateTime expectedDeliveryDate
) {
}
