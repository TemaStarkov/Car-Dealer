package org.example.integration.rest.dto.testdrive;

import java.time.LocalDateTime;

public record RescheduleTestDriveRequest(
        LocalDateTime newTime
) {
}
