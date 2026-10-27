package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.part.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartEntityRepository extends JpaRepository<PartEntity, UUID> {

    Optional<PartEntity> findByIdAndRemovedFalse(UUID id);

    List<PartEntity> findAllByRemovedFalse();

    Optional<PartEntity> findByPartNumberAndRemovedFalse(String partNumber);
}
