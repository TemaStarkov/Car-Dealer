package org.example.integration.rest.dto.order;

import org.example.domain.model.enums.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus status
) {
}
