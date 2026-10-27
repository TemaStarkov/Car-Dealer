package org.example.infrastructure.security;

import org.example.infrastructure.persistence.repository.OrderEntityRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("orderSecurity")
public class OrderSecurity {

    private final OrderEntityRepository orderRepository;
    private final CurrentUserService currentUserService;

    public OrderSecurity(OrderEntityRepository orderRepository, CurrentUserService currentUserService) {
        this.orderRepository = orderRepository;
        this.currentUserService = currentUserService;
    }

    public boolean isOrderOwner(String orderId) {
        UUID parsedOrderId = UUID.fromString(orderId);
        UUID currentKeycloakId = currentUserService.getCurrentUserId();

        return orderRepository.findByIdAndRemovedFalse(parsedOrderId)
                .map(order -> order.getCustomer().getKeycloakId().equals(currentKeycloakId))
                .orElse(false);
    }
}