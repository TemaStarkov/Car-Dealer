package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarModelEntityRepository extends JpaRepository<CarModelEntity, UUID> {

    Optional<CarModelEntity> findByIdAndRemovedFalse(UUID id);

    List<CarModelEntity> findAllByRemovedFalse();

    Optional<CarModelEntity> findByBrandIgnoreCaseAndModelIgnoreCaseAndRemovedFalse(String brand, String model);
}
