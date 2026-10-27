package org.example.integration.kafka.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderSentForApprovalEvent(
        UUID eventId,
        UUID orderId,
        String orderType,
        UUID carId,
        UUID carModelId,
        List<UUID> componentIds,
        UUID traceId,
        Instant occurredAt
) {
}