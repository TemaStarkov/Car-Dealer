package org.example.domain.model.user;

import lombok.experimental.SuperBuilder;
import org.example.domain.model.enums.UserRole;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
public class Customer extends User {

    private Customer(String id, String firstName,
                     String lastName, LocalDateTime createdAt, LocalDateTime updatedAt,
                     UserRole role, boolean active) {

        super(id, firstName, lastName, createdAt, updatedAt, UserRole.USER, active);
    }

    @Override
    public boolean canManageCars() {

        return false;
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

    public boolean canOrderCar() {

        return active;
    }

    public boolean canBookTestDrive() {

        return active;
    }
}