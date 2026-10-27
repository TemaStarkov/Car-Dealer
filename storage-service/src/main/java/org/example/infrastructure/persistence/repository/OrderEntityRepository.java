package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.order.OrderEntity;
import org.example.infrastructure.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByIdAndRemovedFalse(UUID id);

    List<OrderEntity> findAllByRemovedFalse();

    List<OrderEntity> findAllByCustomerAndRemovedFalse(UserEntity customer);

    List<OrderEntity> findAllByManagerAndRemovedFalse(UserEntity manager);
}
