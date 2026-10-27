package org.example.infrastructure.persistence.repository;

import org.example.domain.model.enums.ComponentType;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.example.infrastructure.persistence.entity.car.configuration.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComponentEntityRepository extends JpaRepository<ComponentEntity, UUID> {

    Optional<ComponentEntity> findByIdAndRemovedFalse(UUID id);

    List<ComponentEntity> findAllByTypeAndRemovedFalse(ComponentType type);

    List<ComponentEntity> findAllByTypeAndCompatibleModelsContainingAndRemovedFalse(ComponentType type, CarModelEntity model);

    Optional<ComponentEntity> findFirstByTypeAndBaseTrueAndRemovedFalse(ComponentType type);
}
