package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.event.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventEntityRepository extends JpaRepository<ProcessedEventEntity, UUID> {
}