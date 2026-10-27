package org.example.application.service.impl;

import org.example.application.service.AssemblyOrderService;
import org.example.domain.model.enums.AssemblyOrderStatus;
import org.example.infrastructure.persistence.entity.assembly.AssemblyOrderEntity;
import org.example.infrastructure.persistence.repository.AssemblyOrderEntityRepository;
import org.example.integration.rest.dto.assembly.AssemblyOrderDto;
import org.example.integration.rest.dto.assembly.CreateAssemblyOrderRequest;
import org.example.integration.rest.dto.assembly.UpdateAssemblyOrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssemblyOrderServiceImpl implements AssemblyOrderService {

    private final AssemblyOrderEntityRepository assemblyOrderRepository;

    public AssemblyOrderServiceImpl(AssemblyOrderEntityRepository assemblyOrderRepository) {
        this.assemblyOrderRepository = assemblyOrderRepository;
    }

    @Override
    @Transactional
    public AssemblyOrderDto create(CreateAssemblyOrderRequest request) {
        assemblyOrderRepository.findBySourceOrderId(request.sourceOrderId())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Assembly order already exists for source order: " + request.sourceOrderId()
                    );
                });

        Instant now = Instant.now();

        AssemblyOrderEntity entity = new AssemblyOrderEntity(
                UUID.randomUUID(),
                request.sourceOrderId(),
                request.sourceOrderType(),
                request.carId(),
                request.carModelId(),
                request.warehouseAdminId(),
                now,
                now,
                AssemblyOrderStatus.CREATED,
                false
        );

        AssemblyOrderEntity saved = assemblyOrderRepository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssemblyOrderDto> findAll() {
        return assemblyOrderRepository.findAllByRemovedFalse()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssemblyOrderDto findById(UUID id) {
        AssemblyOrderEntity entity = getActiveAssemblyOrder(id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public AssemblyOrderDto update(UUID id, UpdateAssemblyOrderRequest request) {
        AssemblyOrderEntity entity = getActiveAssemblyOrder(id);

        if (request.status() != null) {
            entity.setStatus(request.status());
        }

        if (request.warehouseAdminId() != null) {
            entity.setWarehouseAdminId(request.warehouseAdminId());
        }

        AssemblyOrderEntity saved = assemblyOrderRepository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AssemblyOrderEntity entity = getActiveAssemblyOrder(id);
        entity.setRemoved(true);
        assemblyOrderRepository.save(entity);
    }

    private AssemblyOrderEntity getActiveAssemblyOrder(UUID id) {
        AssemblyOrderEntity entity = assemblyOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assembly order not found: " + id));

        if (entity.isRemoved()) {
            throw new IllegalArgumentException("Assembly order not found: " + id);
        }

        return entity;
    }

    private AssemblyOrderDto toDto(AssemblyOrderEntity entity) {
        return new AssemblyOrderDto(
                entity.getId(),
                entity.getSourceOrderId(),
                entity.getSourceOrderType(),
                entity.getCarId(),
                entity.getCarModelId(),
                entity.getWarehouseAdminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getStatus(),
                entity.isRemoved()
        );
    }
}