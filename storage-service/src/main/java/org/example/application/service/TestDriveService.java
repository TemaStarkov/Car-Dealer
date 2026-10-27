package org.example.application.service;

import org.example.domain.model.enums.TestDriveStatus;
import org.example.domain.model.order.TestDriveOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TestDriveService {

    TestDriveOrder requestTestDrive(String customerId, String carId, LocalDateTime requestedTime);
    TestDriveOrder confirmTestDrive(String orderId);
    TestDriveOrder completeTestDrive(String orderId);
    TestDriveOrder cancelTestDrive(String orderId);
    TestDriveOrder markNoShow(String orderId);
    List<TestDriveOrder> getTestDrivesByDate(LocalDate date);
    List<TestDriveOrder> getTestDrivesByCustomer(String customerId);
    TestDriveOrder rescheduleTestDrive(String orderId, LocalDateTime newTime);

}