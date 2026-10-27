package org.example.integration.rest.controller;

import org.example.application.service.OrderService;
import org.example.integration.rest.dto.order.CreateCustomOrderRequest;
import org.example.integration.rest.dto.order.CreateInStockOrderRequest;
import org.example.integration.rest.dto.order.OrderDto;
import org.example.integration.rest.dto.order.UpdateOrderStatusRequest;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ApiMapper apiMapper;

    public OrderController(OrderService orderService, ApiMapper apiMapper) {

        this.orderService = orderService;
        this.apiMapper = apiMapper;
    }

    @PostMapping("/in-stock")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderDto createInStockOrder(@RequestBody CreateInStockOrderRequest request) {

        return apiMapper.toDto(orderService.createInStockOrder(request.customerId(), request.carId()));
    }

    @PostMapping("/custom")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderDto createCustomOrder(@RequestBody CreateCustomOrderRequest request) {

        return apiMapper.toDto(orderService.createCustomOrder(
                request.customerId(),
                request.modelId(),
                request.componentChoices(),
                request.expectedDeliveryDate()
        ));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAREHOUSE_ADMIN', 'ADMIN')")
    public OrderDto updateStatus(@PathVariable String id, @RequestBody UpdateOrderStatusRequest request) {
        return apiMapper.toDto(orderService.updateOrderStatus(id, request.status()));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#id)")
    public OrderDto cancelOrder(@PathVariable String id) {
        return apiMapper.toDto(orderService.cancelOrder(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<OrderDto> getOrders() {
        return orderService.getOrdersForCurrentUser()
                .stream()
                .map(apiMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @orderSecurity.isOrderOwner(#id)")
    public OrderDto getOrderById(@PathVariable String id) {
        return apiMapper.toDto(orderService.getOrderById(id));
    }
}
