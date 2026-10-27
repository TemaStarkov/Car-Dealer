package org.example.domain.model.user;

import lombok.experimental.SuperBuilder;
import org.example.domain.model.enums.UserRole;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
public class Manager extends User {

    private final String employeeId;

    private Manager(String id, String firstName,
                    String lastName, LocalDateTime createdAt, LocalDateTime updatedAt,
                    UserRole role, boolean active, String employeeId,
                    String department) {

        super(id, firstName, lastName, createdAt, updatedAt,
                UserRole.MANAGER, active);
        this.employeeId = employeeId;

        validateManager();
    }

    private void validateManager() {

        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
    }

    @Override
    public boolean canManageCars() {

        return true;
    }

    @Override
    public boolean canManageOrders() {

        return true;
    }

    @Override
    public boolean canManageUsers() {

        return false;
    }

    @Override
    public boolean canViewTestDrives() {

        return true;
    }

    public String getEmployeeId() {
        return employeeId; }

    @Override
    public String toString() {

        return String.format("Manager %s %s (ID: %s)",
                firstName, lastName, employeeId);
    }
}