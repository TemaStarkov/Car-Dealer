package org.example.domain.model.order;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
public abstract class Order<T extends Order<T>> {
    @Builder.Default
    protected final String id = UUID.randomUUID().toString();

    protected final String orderNumber;
    protected final Customer customer;
    protected final Manager manager;

    @Builder.Default
    protected final LocalDateTime createdAt =  LocalDateTime.now();

    protected LocalDateTime updatedAt;

    @Builder.Default
    protected OrderStatus status =  OrderStatus.CREATED;

    protected final OrderType type;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    protected Order() {

        this.id = UUID.randomUUID().toString();
        this.orderNumber = null;
        this.customer = null;
        this.manager = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.status = OrderStatus.CREATED;
        this.type = null;
    }

    protected Order(String id, String orderNumber, Customer customer, Manager manager,
                    LocalDateTime createdAt, LocalDateTime updatedAt,
                    OrderStatus status, OrderType type) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.manager = manager;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.status = status != null ? status : OrderStatus.CREATED;
        this.type = type;

        validate();
    }

    private void validate() {

        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("Order number is required");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (manager == null) {
            throw new IllegalArgumentException("Manager is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Order type is required");
        }
    }

    public T approve() {

        this.status = type == OrderType.IN_STOCK
                ? OrderStatus.APPROVED_BY_MANAGER
                : OrderStatus.APPROVED_BY_WAREHOUSE;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public T awaitPayment() {

        this.status = OrderStatus.AWAITING_PAYMENT;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public T pay() {

        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public T readyForPickup() {

        this.status = OrderStatus.READY_FOR_PICKUP;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public T complete() {

        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public T cancel() {

        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        return self();
    }

    public abstract boolean canBeCancelled();
    public abstract String getOrderDetails();

    @Override
    public String toString() {

        return String.format("Order %s [%s] - %s",
                orderNumber, type, status);
    }
}