package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.order.TestDriveOrderEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestDriveOrderEntityRepository extends JpaRepository<TestDriveOrderEntity, UUID> {

    Optional<TestDriveOrderEntity> findByIdAndRemovedFalse(UUID id);

    List<TestDriveOrderEntity> findAllByCustomerAndRemovedFalse(UserEntity customer);

    List<TestDriveOrderEntity> findAllByRequestedTimeBetweenAndRemovedFalse(LocalDateTime start, LocalDateTime end);
}
