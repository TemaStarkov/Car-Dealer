package org.example.integration.rest.controller;

import org.example.application.service.CatalogService;
import org.example.integration.rest.dto.car.CarDto;
import org.example.integration.rest.dto.car.CarModelDto;
import org.example.integration.rest.dto.car.CarSearchRequest;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'WAREHOUSE_ADMIN', 'ADMIN')")
public class CatalogController {

    private final CatalogService catalogService;
    private final ApiMapper apiMapper;

    public CatalogController(CatalogService catalogService, ApiMapper apiMapper) {

        this.catalogService = catalogService;
        this.apiMapper = apiMapper;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars(@ModelAttribute CarSearchRequest request) {

        if (request == null) {
            return catalogService.getAvailableCars().stream().map(apiMapper::toDto).toList();
        }

        boolean empty = request.minPrice() == null
                && request.maxPrice() == null
                && request.brand() == null
                && request.model() == null
                && request.bodyType() == null
                && request.fuelType() == null
                && request.minPower() == null
                && request.maxPower() == null
                && request.minVolume() == null
                && request.maxVolume() == null
                && request.transmissionType() == null
                && request.driveType() == null
                && request.color() == null;

        return (empty ? catalogService.getAvailableCars() : catalogService.findCarsWithFilters(apiMapper.toDomain(request)))
                .stream()
                .map(apiMapper::toDto)
                .toList();
    }

    @GetMapping("/cars/{id}")
    public CarDto getCarById(@PathVariable String id) {
        return apiMapper.toDto(catalogService.getCarById(id));
    }

    @GetMapping("/models")
    public List<CarModelDto> getModels() {
        return catalogService.getAllModels().stream().map(apiMapper::toDto).toList();
    }

    @GetMapping("/models/{id}")
    public CarModelDto getModelById(@PathVariable String id) {
        return apiMapper.toDto(catalogService.getModelById(id));
    }
}
