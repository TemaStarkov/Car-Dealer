package org.example.integration.rest.controller;

import org.example.application.service.WarehouseService;
import org.example.integration.rest.dto.car.CarDto;
import org.example.integration.rest.dto.car.CreateCarRequest;
import org.example.integration.rest.dto.car.UpdateCarRequest;
import org.example.integration.rest.dto.part.CreatePartRequest;
import org.example.integration.rest.dto.part.PartDto;
import org.example.integration.rest.dto.part.UpdatePartRequest;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/warehouse")
@PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final ApiMapper apiMapper;

    public WarehouseController(WarehouseService warehouseService, ApiMapper apiMapper) {

        this.warehouseService = warehouseService;
        this.apiMapper = apiMapper;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {

        return warehouseService.getAllCars().stream().map(apiMapper::toDto).toList();
    }

    @GetMapping("/cars/{id}")
    public CarDto getCar(@PathVariable String id) {

        return apiMapper.toDto(warehouseService.getCarById(id));
    }

    @PostMapping("/cars")
    public CarDto createCar(@RequestBody CreateCarRequest request) {

        return apiMapper.toDto(warehouseService.addCar(
                request.brand(),
                request.model(),
                request.year(),
                request.bodyType(),
                request.fuelType(),
                request.transmissionType(),
                request.driveType(),
                request.power(),
                request.volume(),
                request.color(),
                request.price(),
                request.vin()
        ));
    }

    @PutMapping("/cars/{id}")
    public CarDto updateCar(@PathVariable String id, @RequestBody UpdateCarRequest request) {

        return apiMapper.toDto(warehouseService.updateCar(
                id,
                request.brand(),
                request.model(),
                request.year(),
                request.bodyType(),
                request.fuelType(),
                request.transmissionType(),
                request.driveType(),
                request.power(),
                request.volume(),
                request.color(),
                request.price(),
                request.status()
        ));
    }

    @DeleteMapping("/cars/{id}")
    @ResponseStatus(NO_CONTENT)
    public void removeCar(@PathVariable String id) {

        warehouseService.removeCar(id);
    }

    @GetMapping("/parts")
    public List<PartDto> getParts() {

        return warehouseService.getAllParts().stream().map(apiMapper::toDto).toList();
    }

    @GetMapping("/parts/{id}")
    public PartDto getPart(@PathVariable String id) {

        return apiMapper.toDto(warehouseService.getPartById(id));
    }

    @PostMapping("/parts")
    public PartDto createPart(@RequestBody CreatePartRequest request) {

        return apiMapper.toDto(warehouseService.addPart(
                request.name(),
                request.partNumber(),
                request.category(),
                request.manufacturer(),
                request.price(),
                request.description(),
                request.quantity(),
                request.location()
        ));
    }

    @PutMapping("/parts/{id}")
    public PartDto updatePart(@PathVariable String id, @RequestBody UpdatePartRequest request) {

        return apiMapper.toDto(warehouseService.updatePart(
                id,
                request.name(),
                request.partNumber(),
                request.category(),
                request.manufacturer(),
                request.price(),
                request.description(),
                request.quantity(),
                request.location(),
                request.status()
        ));
    }

    @DeleteMapping("/parts/{id}")
    @ResponseStatus(NO_CONTENT)
    public void removePart(@PathVariable String id) {

        warehouseService.removePart(id);
    }
}
