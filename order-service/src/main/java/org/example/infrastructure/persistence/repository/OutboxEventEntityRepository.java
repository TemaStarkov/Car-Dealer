package org.example.infrastructure.persistence.repository;

import org.example.domain.model.enums.OutboxEventStatus;
import org.example.infrastructure.persistence.entity.outbox.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventEntityRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}