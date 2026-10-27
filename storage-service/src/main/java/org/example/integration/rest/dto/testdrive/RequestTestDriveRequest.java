package org.example.integration.rest.dto.testdrive;

import java.time.LocalDateTime;

public record RequestTestDriveRequest(
        String customerId,
        String carId,
        LocalDateTime requestedTime
) {
}
