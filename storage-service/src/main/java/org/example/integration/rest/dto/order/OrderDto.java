package org.example.integration.rest.dto.order;

import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;
import org.example.integration.rest.dto.car.CarDto;
import org.example.integration.rest.dto.car.CarModelDto;
import org.example.integration.rest.dto.configuration.CarConfigurationDto;

import java.time.LocalDateTime;

public record OrderDto(
        String id,
        String orderNumber,
        OrderStatus status,
        OrderType type,
        String customerFullName,
        String managerFullName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CarDto car,
        CarModelDto carModel,
        CarConfigurationDto configuration,
        LocalDateTime expectedDeliveryDate,
        LocalDateTime deliveryDate
) {
}
