package org.example.application.service;

import java.util.UUID;

public interface ProcessedEventService {

    boolean isProcessed(UUID eventId);

    void markAsProcessed(UUID eventId);
}