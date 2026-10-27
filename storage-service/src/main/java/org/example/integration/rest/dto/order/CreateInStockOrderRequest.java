package org.example.integration.rest.dto.order;

public record CreateInStockOrderRequest(
        String customerId,
        String carId
) {
}
