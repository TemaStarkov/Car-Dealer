package org.example.domain.model.user;

import lombok.experimental.SuperBuilder;
import org.example.domain.model.enums.UserRole;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
public class SystemAdmin extends User {

    private SystemAdmin(String id, String firstName,
                        String lastName, LocalDateTime createdAt, LocalDateTime updatedAt,
                        UserRole role, boolean active) {

        super(id, firstName, lastName, createdAt, updatedAt,
                UserRole.ADMIN, active);
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

        return true;
    }

    @Override
    public boolean canViewTestDrives() {

        return true;
    }

    @Override
    public String toString() {

        return String.format("System Admin %s %s", firstName, lastName);
    }
}