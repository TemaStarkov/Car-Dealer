package org.example.integration.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.service.ProcessedEventService;
import org.example.domain.model.enums.OrderStatus;
import org.example.infrastructure.persistence.entity.order.OrderEntity;
import org.example.infrastructure.persistence.repository.OrderEntityRepository;
import org.example.integration.kafka.KafkaTopics;
import org.example.integration.kafka.event.OrderApprovedEvent;
import org.example.integration.kafka.event.OrderRejectedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class OrderApprovalResultConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessedEventService processedEventService;
    private final OrderEntityRepository orderRepository;

    public OrderApprovalResultConsumer(ObjectMapper objectMapper,
                                       ProcessedEventService processedEventService,
                                       OrderEntityRepository orderRepository) {
        this.objectMapper = objectMapper;
        this.processedEventService = processedEventService;
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = KafkaTopics.ORDER_APPROVAL_RESULT,
            groupId = "order-service"
    )
    @Transactional
    public void consume(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            if (jsonNode.hasNonNull("reason")) {
                handleRejected(payload);
            } else {
                handleApproved(payload);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to process order approval result event", e);
        }
    }

    private void handleApproved(String payload) throws Exception {
        OrderApprovedEvent event = objectMapper.readValue(payload, OrderApprovedEvent.class);

        if (processedEventService.isProcessed(event.eventId())) {
            return;
        }

        OrderEntity order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.setStatus(OrderStatus.CAR_READY_FOR_PICKUP);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
        processedEventService.markAsProcessed(event.eventId());
    }

    private void handleRejected(String payload) throws Exception {
        OrderRejectedEvent event = objectMapper.readValue(payload, OrderRejectedEvent.class);

        if (processedEventService.isProcessed(event.eventId())) {
            return;
        }

        OrderEntity order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
        processedEventService.markAsProcessed(event.eventId());
    }
}