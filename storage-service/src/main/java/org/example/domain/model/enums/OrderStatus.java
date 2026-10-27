package org.example.domain.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED,
    APPROVED_BY_MANAGER,
    APPROVED_BY_WAREHOUSE,
    AWAITING_PAYMENT,
    PAID,
    AWAITING_DELIVERY,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}
