package org.example.application.service.impl;

import org.example.application.service.CatalogService;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.model.car.Car;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.SearchFilters;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.CarModelEntityRepository;
import org.example.infrastructure.persistence.specification.CarSpecifications;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CarEntityRepository carRepository;
    private final CarModelEntityRepository carModelRepository;
    private final PersistenceMapper persistenceMapper;

    public CatalogServiceImpl(CarEntityRepository carRepository,
                              CarModelEntityRepository carModelRepository,
                              PersistenceMapper persistenceMapper) {

        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public List<Car> getAvailableCars() {
        return carRepository.findAllByStatusAndRemovedFalse(org.example.domain.model.enums.CarStatus.AVAILABLE)
                .stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findCarsWithFilters(SearchFilters filters) {
        return carRepository.findAll(CarSpecifications.withFilters(filters)).stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(String id) {
        return carRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(id))
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
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
                .orElseThrow(() -> new EntityNotFoundException("Car model not found with id: " + id));
    }
}
