package org.example.application.service.impl;

import org.example.application.service.CatalogService;
import org.example.domain.model.car.Car;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.Engine;
import org.example.domain.model.car.SearchFilters;
import org.example.domain.model.enums.*;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarModelEntityRepository;
import org.example.integration.grpc.CarGrpcClient;
import org.example.grpc.CarResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CarGrpcClient carGrpcClient;
    private final CarModelEntityRepository carModelRepository;
    private final PersistenceMapper persistenceMapper;

    public CatalogServiceImpl(CarGrpcClient carGrpcClient,
                              CarModelEntityRepository carModelRepository,
                              PersistenceMapper persistenceMapper) {
        this.carGrpcClient = carGrpcClient;
        this.carModelRepository = carModelRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public List<Car> getAvailableCars() {
        return carGrpcClient.listAvailableCars().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findCarsWithFilters(SearchFilters filters) {
        return getAvailableCars();
    }

    @Override
    public Car getCarById(String id) {
        return mapToDomain(carGrpcClient.getCarById(id));
    }

    @Override
    public List<CarModel> getAllModels() {
        return carModelRepository.findAllByRemovedFalse().stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public CarModel getModelById(String id) {
        return carModelRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(id))
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new org.example.domain.exception.EntityNotFoundException("Car model not found with id: " + id));
    }

    private Car mapToDomain(CarResponse response) {
        return Car.builder()
                .id(response.getId())
                .brand(response.getBrand())
                .model(response.getModel())
                .year(response.getYear())
                .bodyType(BodyType.valueOf(response.getBodyType()))
                .fuelType(FuelType.valueOf(response.getFuelType()))
                .transmissionType(TransmissionType.valueOf(response.getTransmissionType()))
                .driveType(DriveType.valueOf(response.getDriveType()))
                .color(response.getColor())
                .price(BigDecimal.valueOf(response.getPrice()))
                .status(CarStatus.valueOf(response.getStatus()))
                .vin(response.getVin())
                .engine(Engine.builder()
                        .power(response.getEnginePower())
                        .volume(response.getEngineVolume())
                        .fuelType(FuelType.valueOf(response.getFuelType()))
                        .cylinders(4)
                        .build())
                .build();
    }
}
