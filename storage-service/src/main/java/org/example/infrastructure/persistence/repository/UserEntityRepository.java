package org.example.infrastructure.persistence.repository;

import org.example.domain.model.enums.UserRole;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByIdAndRemovedFalse(UUID id);

    Optional<UserEntity> findByKeycloakIdAndRemovedFalse(UUID keycloakId);

    List<UserEntity> findAllByRemovedFalse();

    List<UserEntity> findAllByRoleAndRemovedFalse(UserRole role);

    List<UserEntity> findAllByRoleAndActiveTrueAndRemovedFalse(UserRole role);
}
