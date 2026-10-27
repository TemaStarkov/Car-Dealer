package org.example.domain.model.order;

import lombok.experimental.SuperBuilder;
import org.example.domain.model.car.Car;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
public class InStockOrder extends Order<InStockOrder> {

    private final Car car;

    private InStockOrder(String id, String orderNumber, Customer customer,
                         Manager manager, LocalDateTime createdAt,
                         LocalDateTime updatedAt, OrderStatus status,
                         OrderType type, Car car) {

        super(id, orderNumber, customer, manager, createdAt, updatedAt,
                status, OrderType.IN_STOCK);
        this.car = car;

        validateInStockOrder();
    }

    private void validateInStockOrder() {

        if (car == null) {
            throw new IllegalArgumentException("Car is required for in-stock order");
        }
        if (!car.isAvailable()) {
            throw new IllegalStateException("Car is not available for sale");
        }
    }

    @Override
    public boolean canBeCancelled() {

        return status == OrderStatus.CREATED ||
                status == OrderStatus.APPROVED_BY_MANAGER;
    }

    @Override
    public String getOrderDetails() {

        return String.format("Заказ на авто в наличии: %s %s %d, VIN: %s",
                car.getBrand(), car.getModel(), car.getYear(), car.getVin());
    }

    public Car getCar() {
        return car;
    }

    public InStockOrder markCarReady() {

        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be paid first");
        }

        return readyForPickup();
    }
}