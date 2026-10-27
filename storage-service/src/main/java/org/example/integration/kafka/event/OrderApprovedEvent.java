package org.example.integration.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record OrderApprovedEvent(
        UUID eventId,
        UUID orderId,
        UUID assemblyOrderId,
        UUID traceId,
        Instant occurredAt
) {
}