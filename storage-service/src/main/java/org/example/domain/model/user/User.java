package org.example.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.example.domain.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
public abstract class User {
    @Builder.Default
    protected final String id = UUID.randomUUID().toString();

    protected final String firstName;
    protected final String lastName;
    protected final LocalDateTime createdAt;
    protected final LocalDateTime updatedAt;
    protected final UserRole role;
    protected final boolean active;

    protected User(String id, String firstName, String lastName, LocalDateTime createdAt,
                   LocalDateTime updatedAt, UserRole role, boolean active) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.role = role;
        this.active = active;

        validate();
    }

    private void validate() {

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    public String getFullName() {

        return firstName + " " + lastName;
    }

    public abstract boolean canManageCars();
    public abstract boolean canManageOrders();
    public abstract boolean canManageUsers();
    public abstract boolean canViewTestDrives();

    @Override
    public String toString() {

        return String.format("%s %s - %s",
                firstName, lastName, role);
    }
}