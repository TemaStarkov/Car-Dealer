package org.example.application.service;

import org.example.domain.model.enums.OrderStatus;
import org.example.domain.model.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createInStockOrder(String customerId, String carId);
    Order createCustomOrder(String customerId, String modelId,
                            Map<String, String> componentChoices,
                            LocalDateTime expectedDeliveryDate);
    Order updateOrderStatus(String orderId, OrderStatus newStatus);
    List<Order> getOrdersByCustomer(String customerId);
    List<Order> getOrdersByManager(String managerId);
    List<Order> getAllOrders();
    List<Order> getOrdersForCurrentUser();
    Order getOrderById(String id);
    Order cancelOrder(String id);

}