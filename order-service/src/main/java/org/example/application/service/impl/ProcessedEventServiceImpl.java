package org.example.application.service.impl;

import org.example.application.service.ProcessedEventService;
import org.example.infrastructure.persistence.entity.event.ProcessedEventEntity;
import org.example.infrastructure.persistence.repository.ProcessedEventEntityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProcessedEventServiceImpl implements ProcessedEventService {

    private final ProcessedEventEntityRepository processedEventRepository;

    public ProcessedEventServiceImpl(ProcessedEventEntityRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    public boolean isProcessed(UUID eventId) {
        return processedEventRepository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(UUID eventId) {
        processedEventRepository.save(new ProcessedEventEntity(eventId, Instant.now()));
    }
}