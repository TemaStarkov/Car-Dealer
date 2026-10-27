package org.example.infrastructure.persistence.repository;

import org.example.domain.model.enums.AssemblyOrderStatus;
import org.example.infrastructure.persistence.entity.assembly.AssemblyOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssemblyOrderEntityRepository extends JpaRepository<AssemblyOrderEntity, UUID> {

    Optional<AssemblyOrderEntity> findBySourceOrderId(UUID sourceOrderId);

    List<AssemblyOrderEntity> findAllByRemovedFalse();

    List<AssemblyOrderEntity> findAllByStatusAndRemovedFalse(AssemblyOrderStatus status);
}