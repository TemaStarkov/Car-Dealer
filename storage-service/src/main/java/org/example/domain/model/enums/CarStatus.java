package org.example.domain.model.enums;

import lombok.Getter;

@Getter
public enum CarStatus {
    AVAILABLE,
    SOLD,
    RESERVED,
    TEST_DRIVE,
    IN_TRANSIT,
    ON_ORDER
}
