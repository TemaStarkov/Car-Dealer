package org.example.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.service.OutboxService;
import org.example.domain.model.enums.OutboxEventStatus;
import org.example.infrastructure.persistence.entity.outbox.OutboxEventEntity;
import org.example.infrastructure.persistence.repository.OutboxEventEntityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventEntityRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxServiceImpl(OutboxEventEntityRepository outboxEventRepository,
                             ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveEvent(UUID aggregateId, String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            OutboxEventEntity event = new OutboxEventEntity(
                    UUID.randomUUID(),
                    aggregateId,
                    eventType,
                    jsonPayload,
                    OutboxEventStatus.NEW,
                    Instant.now(),
                    null
            );

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox event", e);
        }
    }
}