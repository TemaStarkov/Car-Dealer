package org.example.domain.model.assembly;

import org.example.domain.model.enums.AssemblyOrderStatus;

import java.time.Instant;
import java.util.UUID;

public class AssemblyOrder {

    private UUID id;
    private UUID sourceOrderId;
    private String sourceOrderType;
    private UUID carId;
    private UUID carModelId;
    private UUID warehouseAdminId;
    private Instant createdAt;
    private Instant updatedAt;
    private AssemblyOrderStatus status;
    private boolean removed;

    public AssemblyOrder(UUID id,
                         UUID sourceOrderId,
                         String sourceOrderType,
                         UUID carId,
                         UUID carModelId,
                         UUID warehouseAdminId,
                         Instant createdAt,
                         Instant updatedAt,
                         AssemblyOrderStatus status,
                         boolean removed) {
        this.id = id;
        this.sourceOrderId = sourceOrderId;
        this.sourceOrderType = sourceOrderType;
        this.carId = carId;
        this.carModelId = carModelId;
        this.warehouseAdminId = warehouseAdminId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.removed = removed;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceOrderId() {
        return sourceOrderId;
    }

    public String getSourceOrderType() {
        return sourceOrderType;
    }

    public UUID getCarId() {
        return carId;
    }

    public UUID getCarModelId() {
        return carModelId;
    }

    public UUID getWarehouseAdminId() {
        return warehouseAdminId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public AssemblyOrderStatus getStatus() {
        return status;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void markAsAssembled() {
        this.status = AssemblyOrderStatus.ASSEMBLED;
        this.updatedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = AssemblyOrderStatus.FAIL;
        this.updatedAt = Instant.now();
    }

    public void remove() {
        this.removed = true;
        this.updatedAt = Instant.now();
    }
}