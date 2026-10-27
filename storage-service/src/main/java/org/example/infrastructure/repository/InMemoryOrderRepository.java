package org.example.infrastructure.repository;

import org.example.domain.model.order.Order;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.enums.OrderType;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InMemoryOrderRepository extends InMemoryBaseRepository<Order<?>, String> {

    @Override
    protected String getId(Order<?> order) {

        return order.getId();
    }

    @Override
    protected void validate(Order<?> order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }

    public Optional<Order<?>> findByOrderNumber(String orderNumber) {

        return findFirst(o -> o.getOrderNumber().equals(orderNumber));
    }

    public List<Order<?>> findByCustomer(Customer customer) {

        return find(o -> o.getCustomer().equals(customer));
    }

    public List<Order<?>> findByManager(Manager manager) {

        return find(o -> o.getManager().equals(manager));
    }

    public List<Order<?>> findByStatus(OrderStatus status) {

        return find(o -> o.getStatus() == status);
    }

    public List<Order<?>> findByType(OrderType type) {

        return find(o -> o.getType() == type);
    }

    public List<Order<?>> findByDateRange(LocalDateTime from, LocalDateTime to) {

        return find(o ->
                o.getCreatedAt().isAfter(from) &&
                        o.getCreatedAt().isBefore(to)
        );
    }

    public List<Order<?>> findByCustomerAndStatus(Customer customer, OrderStatus status) {

        return find(o ->
                o.getCustomer().equals(customer) &&
                        o.getStatus() == status
        );
    }

    public List<Order<?>> findByManagerAndStatus(Manager manager, OrderStatus status) {

        return find(o ->
                o.getManager().equals(manager) &&
                        o.getStatus() == status
        );
    }

    public long countByStatus(OrderStatus status) {

        return storage.values().stream()
                .filter(o -> o.getStatus() == status)
                .count();
    }

    public boolean existsByOrderNumber(String orderNumber) {

        return storage.values().stream()
                .anyMatch(o -> o.getOrderNumber().equals(orderNumber));
    }
}