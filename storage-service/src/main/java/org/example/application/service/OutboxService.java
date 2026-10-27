package org.example.application.service;

import java.util.UUID;

public interface OutboxService {

    void saveEvent(UUID aggregateId, String eventType, Object payload);
}