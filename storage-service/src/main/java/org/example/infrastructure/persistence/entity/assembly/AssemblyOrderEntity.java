package org.example.infrastructure.persistence.entity.assembly;

import jakarta.persistence.*;
import org.example.domain.model.enums.AssemblyOrderStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assembly_orders")
public class AssemblyOrderEntity {

    @Id
    private UUID id;

    @Column(name = "source_order_id", nullable = false, unique = true)
    private UUID sourceOrderId;

    @Column(name = "source_order_type", nullable = false)
    private String sourceOrderType;

    @Column(name = "car_id")
    private UUID carId;

    @Column(name = "car_model_id")
    private UUID carModelId;

    @Column(name = "warehouse_admin_id")
    private UUID warehouseAdminId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssemblyOrderStatus status;

    @Column(name = "removed", nullable = false)
    private boolean removed;

    public AssemblyOrderEntity() {
    }

    public AssemblyOrderEntity(UUID id,
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

    public void setStatus(AssemblyOrderStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
        this.updatedAt = Instant.now();
    }

    public void setWarehouseAdminId(UUID warehouseAdminId) {
        this.warehouseAdminId = warehouseAdminId;
        this.updatedAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}