package org.example.integration.rest.controller;

import org.example.application.service.CatalogService;
import org.example.integration.rest.dto.car.CarDto;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CatalogService catalogService;
    private final ApiMapper apiMapper;

    public CarController(CatalogService catalogService, ApiMapper apiMapper) {
        this.catalogService = catalogService;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'WAREHOUSE_ADMIN', 'ADMIN')")
    public List<CarDto> getAllCars() {
        return catalogService.getAvailableCars().stream()
                .map(apiMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'WAREHOUSE_ADMIN', 'ADMIN')")
    public CarDto getCarById(@PathVariable String id) {
        return apiMapper.toDto(catalogService.getCarById(id));
    }
}
