package org.example.integration.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record OrderRejectedEvent(
        UUID eventId,
        UUID orderId,
        UUID assemblyOrderId,
        String reason,
        UUID traceId,
        Instant occurredAt
) {
}