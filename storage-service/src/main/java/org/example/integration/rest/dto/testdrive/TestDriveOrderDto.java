package org.example.integration.rest.dto.testdrive;

import org.example.domain.model.enums.TestDriveStatus;
import org.example.integration.rest.dto.car.CarDto;

import java.time.LocalDateTime;

public record TestDriveOrderDto(
        String id,
        String customerFullName,
        String managerFullName,
        CarDto car,
        LocalDateTime requestedTime,
        TestDriveStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
