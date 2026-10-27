package org.example.integration.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.service.OutboxService;
import org.example.application.service.ProcessedEventService;
import org.example.domain.model.enums.AssemblyOrderStatus;
import org.example.infrastructure.persistence.entity.assembly.AssemblyOrderEntity;
import org.example.infrastructure.persistence.repository.AssemblyOrderEntityRepository;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.integration.kafka.KafkaTopics;
import org.example.integration.kafka.event.OrderApprovedEvent;
import org.example.integration.kafka.event.OrderRejectedEvent;
import org.example.integration.kafka.event.OrderSentForApprovalEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderSentForApprovalConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessedEventService processedEventService;
    private final AssemblyOrderEntityRepository assemblyOrderRepository;
    private final CarEntityRepository carRepository;
    private final OutboxService outboxService;

    public OrderSentForApprovalConsumer(ObjectMapper objectMapper,
                                        ProcessedEventService processedEventService,
                                        AssemblyOrderEntityRepository assemblyOrderRepository,
                                        CarEntityRepository carRepository,
                                        OutboxService outboxService) {
        this.objectMapper = objectMapper;
        this.processedEventService = processedEventService;
        this.assemblyOrderRepository = assemblyOrderRepository;
        this.carRepository = carRepository;
        this.outboxService = outboxService;
    }

    @KafkaListener(
            topics = KafkaTopics.ORDER_SENT_FOR_APPROVAL,
            groupId = "storage-service"
    )
    @Transactional
    public void consume(String payload) {
        try {
            OrderSentForApprovalEvent event = objectMapper.readValue(
                    payload,
                    OrderSentForApprovalEvent.class
            );

            if (processedEventService.isProcessed(event.eventId())) {
                return;
            }

            if (assemblyOrderRepository.findBySourceOrderId(event.orderId()).isPresent()) {
                processedEventService.markAsProcessed(event.eventId());
                return;
            }

            AssemblyOrderEntity assemblyOrder = createAssemblyOrder(event);

            boolean canAssemble = checkAvailability(event);

            if (canAssemble) {
                assemblyOrder.setStatus(AssemblyOrderStatus.ASSEMBLED);
                assemblyOrderRepository.save(assemblyOrder);

                OrderApprovedEvent approvedEvent = new OrderApprovedEvent(
                        UUID.randomUUID(),
                        event.orderId(),
                        assemblyOrder.getId(),
                        event.traceId(),
                        Instant.now()
                );

                outboxService.saveEvent(
                        event.orderId(),
                        "OrderApproved",
                        approvedEvent
                );
            } else {
                assemblyOrder.setStatus(AssemblyOrderStatus.FAIL);
                assemblyOrderRepository.save(assemblyOrder);

                OrderRejectedEvent rejectedEvent = new OrderRejectedEvent(
                        UUID.randomUUID(),
                        event.orderId(),
                        assemblyOrder.getId(),
                        "Car or components are not available in storage",
                        event.traceId(),
                        Instant.now()
                );

                outboxService.saveEvent(
                        event.orderId(),
                        "OrderRejected",
                        rejectedEvent
                );
            }

            processedEventService.markAsProcessed(event.eventId());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to process OrderSentForApproval event", e);
        }
    }

    private AssemblyOrderEntity createAssemblyOrder(OrderSentForApprovalEvent event) {
        Instant now = Instant.now();

        AssemblyOrderEntity assemblyOrder = new AssemblyOrderEntity(
                UUID.randomUUID(),
                event.orderId(),
                event.orderType(),
                event.carId(),
                event.carModelId(),
                null,
                now,
                now,
                AssemblyOrderStatus.CREATED,
                false
        );

        return assemblyOrderRepository.save(assemblyOrder);
    }

    private boolean checkAvailability(OrderSentForApprovalEvent event) {
        if (event.carId() != null) {
            return carRepository.existsById(event.carId());
        }

        if (event.carModelId() != null) {
            return true;
        }

        return false;
    }
}