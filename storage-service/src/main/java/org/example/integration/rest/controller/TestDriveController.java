package org.example.integration.rest.controller;

import org.example.application.service.TestDriveService;
import org.example.integration.rest.dto.testdrive.RequestTestDriveRequest;
import org.example.integration.rest.dto.testdrive.RescheduleTestDriveRequest;
import org.example.integration.rest.dto.testdrive.TestDriveOrderDto;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;
    private final ApiMapper apiMapper;

    public TestDriveController(TestDriveService testDriveService, ApiMapper apiMapper) {

        this.testDriveService = testDriveService;
        this.apiMapper = apiMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TestDriveOrderDto request(@RequestBody RequestTestDriveRequest request) {

        return apiMapper.toDto(testDriveService.requestTestDrive(
                request.customerId(),
                request.carId(),
                request.requestedTime()
        ));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public TestDriveOrderDto confirm(@PathVariable String id) {

        return apiMapper.toDto(testDriveService.confirmTestDrive(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public TestDriveOrderDto complete(@PathVariable String id) {

        return apiMapper.toDto(testDriveService.completeTestDrive(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TestDriveOrderDto cancel(@PathVariable String id) {

        return apiMapper.toDto(testDriveService.cancelTestDrive(id));
    }

    @PostMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public TestDriveOrderDto markNoShow(@PathVariable String id) {

        return apiMapper.toDto(testDriveService.markNoShow(id));
    }

    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TestDriveOrderDto reschedule(@PathVariable String id, @RequestBody RescheduleTestDriveRequest request) {

        return apiMapper.toDto(testDriveService.rescheduleTestDrive(id, request.newTime()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<TestDriveOrderDto> getOrders(@RequestParam(required = false) String customerId,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (customerId != null) {
            return testDriveService.getTestDrivesByCustomer(customerId).stream().map(apiMapper::toDto).toList();
        }
        if (date != null) {
            return testDriveService.getTestDrivesByDate(date).stream().map(apiMapper::toDto).toList();
        }
        throw new IllegalArgumentException("Either customerId or date must be provided");
    }
}
