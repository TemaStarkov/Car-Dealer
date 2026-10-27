package org.example.domain.model.user;

import lombok.experimental.SuperBuilder;
import org.example.domain.model.enums.UserRole;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
public class WarehouseAdmin extends User {

    private final String warehouseId;

    private WarehouseAdmin(String id, String firstName,
                           String lastName, LocalDateTime createdAt, LocalDateTime updatedAt,
                           UserRole role, boolean active, String warehouseId, String section) {

        super(id, firstName, lastName, createdAt, updatedAt,
                UserRole.WAREHOUSE_ADMIN, active);
        this.warehouseId = warehouseId;

        validateWarehouseAdmin();
    }

    private void validateWarehouseAdmin() {

        if (warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("Warehouse ID is required");
        }
    }

    @Override
    public boolean canManageCars() {

        return true;
    }

    @Override
    public boolean canManageOrders() {

        return false;
    }

    @Override
    public boolean canManageUsers() {

        return false;
    }

    @Override
    public boolean canViewTestDrives() {

        return false;
    }

    public String getWarehouseId() {

        return warehouseId;
    }

    public boolean canManageParts() {

        return true;
    }

    public boolean canReceiveShipments() {

        return true;
    }

    @Override
    public String toString() {

        return String.format("Warehouse Admin %s %s (Warehouse: %s)",
                firstName, lastName, warehouseId);
    }
}