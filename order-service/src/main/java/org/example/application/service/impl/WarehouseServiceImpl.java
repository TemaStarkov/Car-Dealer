package org.example.application.service.impl;

import org.example.application.service.WarehouseService;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.model.car.Car;
import org.example.domain.model.car.Engine;
import org.example.domain.model.enums.*;
import org.example.domain.model.part.Part;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.example.infrastructure.persistence.entity.part.PartEntity;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.PartEntityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final CarEntityRepository carRepository;
    private final PartEntityRepository partRepository;
    private final PersistenceMapper persistenceMapper;

    public WarehouseServiceImpl(CarEntityRepository carRepository,
                                PartEntityRepository partRepository,
                                PersistenceMapper persistenceMapper) {
        this.carRepository = carRepository;
        this.partRepository = partRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public Car addCar(String brand, String model, int year, BodyType bodyType,
                      FuelType fuelType, TransmissionType transmissionType, DriveType driveType,
                      double power, double volume, String color, BigDecimal price, String vin) {

        if (carRepository.findByVinAndRemovedFalse(vin).isPresent()) {
            throw new IllegalArgumentException("Car with VIN " + vin + " already exists");
        }

        Engine engine = Engine.builder()
                .power(power)
                .volume(volume)
                .fuelType(fuelType)
                .cylinders(4)
                .build();

        Car car = Car.builder()
                .brand(brand)
                .model(model)
                .year(year)
                .bodyType(bodyType)
                .fuelType(fuelType)
                .transmissionType(transmissionType)
                .driveType(driveType)
                .engine(engine)
                .color(color)
                .price(price)
                .vin(vin)
                .build();

        CarEntity savedCar = carRepository.save(persistenceMapper.toEntity(car));
        return persistenceMapper.toDomain(savedCar);
    }

    @Override
    public Car updateCar(String carId, String brand, String model, Integer year,
                         BodyType bodyType, FuelType fuelType, TransmissionType transmissionType,
                         DriveType driveType, Double power, Double volume, String color,
                         BigDecimal price, CarStatus status) {

        CarEntity existingCarEntity = carRepository.findByIdAndRemovedFalse(UUID.fromString(carId))
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + carId));

        Car existingCar = persistenceMapper.toDomain(existingCarEntity);

        Engine engine = existingCar.getEngine();
        if (power != null || volume != null || fuelType != null) {
            engine = Engine.builder()
                    .power(power != null ? power : engine.getPower())
                    .volume(volume != null ? volume : engine.getVolume())
                    .fuelType(fuelType != null ? fuelType : engine.getFuelType())
                    .cylinders(engine.getCylinders())
                    .build();
        }

        Car updatedCar = Car.builder()
                .id(existingCar.getId())
                .brand(brand != null ? brand : existingCar.getBrand())
                .model(model != null ? model : existingCar.getModel())
                .year(year != null ? year : existingCar.getYear())
                .bodyType(bodyType != null ? bodyType : existingCar.getBodyType())
                .fuelType(fuelType != null ? fuelType : existingCar.getFuelType())
                .transmissionType(transmissionType != null ? transmissionType : existingCar.getTransmissionType())
                .driveType(driveType != null ? driveType : existingCar.getDriveType())
                .engine(engine)
                .color(color != null ? color : existingCar.getColor())
                .price(price != null ? price : existingCar.getPrice())
                .status(status != null ? status : existingCar.getStatus())
                .vin(existingCar.getVin())
                .build();

        CarEntity updatedEntity = persistenceMapper.toEntity(updatedCar);
        updatedEntity.setCreatedAt(existingCarEntity.getCreatedAt());
        updatedEntity.setUpdatedAt(existingCarEntity.getUpdatedAt());
        updatedEntity.setRemoved(existingCarEntity.isRemoved());

        return persistenceMapper.toDomain(carRepository.save(updatedEntity));
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAllByRemovedFalse().stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(String id) {
        return carRepository.findByIdAndRemovedFalse(UUID.fromString(id))
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
    }

    @Override
    public void removeCar(String carId) {
        CarEntity existingCar = carRepository.findByIdAndRemovedFalse(UUID.fromString(carId))
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + carId));
        existingCar.setRemoved(true);
        carRepository.save(existingCar);
    }

    @Override
    public Part addPart(String name, String partNumber, PartCategory category,
                        String manufacturer, BigDecimal price, String description,
                        int quantity, String location) {

        if (partRepository.findByPartNumberAndRemovedFalse(partNumber).isPresent()) {
            throw new IllegalArgumentException("Part with number " + partNumber + " already exists");
        }

        Part part = Part.builder()
                .name(name)
                .partNumber(partNumber)
                .category(category)
                .manufacturer(manufacturer)
                .price(price)
                .description(description)
                .quantity(quantity)
                .location(location)
                .build();

        PartEntity savedPart = partRepository.save(persistenceMapper.toEntity(part));
        return persistenceMapper.toDomain(savedPart);
    }

    @Override
    public Part updatePart(String partId, String name, String partNumber, PartCategory category,
                           String manufacturer, BigDecimal price, String description,
                           Integer quantity, String location, PartStatus status) {

        PartEntity existingPartEntity = partRepository.findByIdAndRemovedFalse(UUID.fromString(partId))
                .orElseThrow(() -> new EntityNotFoundException("Part not found with id: " + partId));

        Part existingPart = persistenceMapper.toDomain(existingPartEntity);

        Part updatedPart = existingPart.toBuilder()
                .name(name != null ? name : existingPart.getName())
                .partNumber(partNumber != null ? partNumber : existingPart.getPartNumber())
                .category(category != null ? category : existingPart.getCategory())
                .manufacturer(manufacturer != null ? manufacturer : existingPart.getManufacturer())
                .price(price != null ? price : existingPart.getPrice())
                .description(description != null ? description : existingPart.getDescription())
                .quantity(quantity != null ? quantity : existingPart.getQuantity())
                .location(location != null ? location : existingPart.getLocation())
                .status(status != null ? status : existingPart.getStatus())
                .build();

        PartEntity updatedEntity = persistenceMapper.toEntity(updatedPart);
        updatedEntity.setCreatedAt(existingPartEntity.getCreatedAt());
        updatedEntity.setUpdatedAt(existingPartEntity.getUpdatedAt());
        updatedEntity.setRemoved(existingPartEntity.isRemoved());

        return persistenceMapper.toDomain(partRepository.save(updatedEntity));
    }

    @Override
    public List<Part> getAllParts() {
        return partRepository.findAllByRemovedFalse().stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Part getPartById(String id) {
        return partRepository.findByIdAndRemovedFalse(UUID.fromString(id))
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Part not found with id: " + id));
    }

    @Override
    public void removePart(String partId) {
        PartEntity existingPart = partRepository.findByIdAndRemovedFalse(UUID.fromString(partId))
                .orElseThrow(() -> new EntityNotFoundException("Part not found with id: " + partId));
        existingPart.setRemoved(true);
        partRepository.save(existingPart);
    }
}
