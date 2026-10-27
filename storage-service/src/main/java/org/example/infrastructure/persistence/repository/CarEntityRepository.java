package org.example.infrastructure.persistence.repository;

import org.example.domain.model.enums.CarStatus;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarEntityRepository extends JpaRepository<CarEntity, UUID>, JpaSpecificationExecutor<CarEntity> {

    Optional<CarEntity> findByIdAndRemovedFalse(UUID id);

    List<CarEntity> findAllByRemovedFalse();

    Optional<CarEntity> findByVinAndRemovedFalse(String vin);

    List<CarEntity> findAllByStatusAndRemovedFalse(CarStatus status);

    @Override
    List<CarEntity> findAll(Specification<CarEntity> specification);
}
