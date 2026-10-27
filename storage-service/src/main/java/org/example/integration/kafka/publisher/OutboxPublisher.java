package org.example.integration.kafka.publisher;

import org.example.domain.model.enums.OutboxEventStatus;
import org.example.infrastructure.persistence.entity.outbox.OutboxEventEntity;
import org.example.infrastructure.persistence.repository.OutboxEventEntityRepository;
import org.example.integration.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {

    private final OutboxEventEntityRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventEntityRepository outboxEventRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishNewEvents() {
        var events = outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus.NEW);

        for (OutboxEventEntity event : events) {
            try {
                String topic = resolveTopic(event.getEventType());

                kafkaTemplate.send(
                        topic,
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();

                event.markAsPublished();
                outboxEventRepository.save(event);
            } catch (Exception e) {
                event.markAsFailed();
                outboxEventRepository.save(event);
            }
        }
    }

    private String resolveTopic(String eventType) {
        if ("OrderApproved".equals(eventType) || "OrderRejected".equals(eventType)) {
            return KafkaTopics.ORDER_APPROVAL_RESULT;
        }

        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }
}