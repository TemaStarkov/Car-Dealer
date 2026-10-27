package org.example.domain.model.order;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class CustomOrder extends Order<CustomOrder> {

    private final CarModel carModel;
    private final CarConfiguration configuration;
    private final LocalDateTime expectedDeliveryDate;
    private LocalDateTime deliveryDate;

    private CustomOrder(String id, String orderNumber, Customer customer,
                        Manager manager, LocalDateTime createdAt,
                        LocalDateTime updatedAt, OrderStatus status,
                        OrderType type, CarModel carModel,
                        CarConfiguration configuration,
                        LocalDateTime expectedDeliveryDate,
                        LocalDateTime deliveryDate) {

        super(id, orderNumber, customer, manager, createdAt, updatedAt,
                status, OrderType.CUSTOM);
        this.carModel = carModel;
        this.configuration = configuration;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.deliveryDate = deliveryDate;

        validateCustomOrder();
    }

    private void validateCustomOrder() {

        if (carModel == null) {
            throw new IllegalArgumentException("Car model is required");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration is required");
        }
        if (expectedDeliveryDate == null) {
            throw new IllegalArgumentException("Expected delivery date is required");
        }
        if (!configuration.isValid()) {
            throw new IllegalArgumentException("Invalid car configuration");
        }
    }

    @Override
    public boolean canBeCancelled() {

        return status == OrderStatus.CREATED ||
                status == OrderStatus.APPROVED_BY_WAREHOUSE ||
                status == OrderStatus.AWAITING_PAYMENT ||
                status == OrderStatus.PAID;
    }

    @Override
    public String getOrderDetails() {

        return String.format("Заказ на сборку: %s, конфигурация: %s ₽, ожидаемая поставка: %s",
                carModel.getModel(),
                configuration.getTotalPrice(),
                expectedDeliveryDate.toLocalDate());
    }

    public CustomOrder markDelivery() {

        if (status != OrderStatus.AWAITING_DELIVERY) {
            throw new IllegalStateException("Order is not awaiting delivery");
        }

        this.status = OrderStatus.READY_FOR_PICKUP;
        this.deliveryDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public CustomOrder awaitDelivery() {

        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be paid first");
        }

        this.status = OrderStatus.AWAITING_DELIVERY;
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public BigDecimal getTotalPrice() {
        return configuration.getTotalPrice();
    }
}