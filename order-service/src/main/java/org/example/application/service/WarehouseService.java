package org.example.application.service;

import org.example.domain.model.car.Car;
import org.example.domain.model.enums.*;
import org.example.domain.model.part.Part;

import java.math.BigDecimal;
import java.util.List;

public interface WarehouseService {

    Car addCar(String brand, String model, int year, BodyType bodyType,
               FuelType fuelType, TransmissionType transmissionType, DriveType driveType,
               double power, double volume, String color, BigDecimal price, String vin);

    Car updateCar(String carId, String brand, String model, Integer year,
                  BodyType bodyType, FuelType fuelType, TransmissionType transmissionType,
                  DriveType driveType, Double power, Double volume, String color,
                  BigDecimal price, CarStatus status);

    List<Car> getAllCars();
    Car getCarById(String id);
    void removeCar(String carId);

    Part addPart(String name, String partNumber, PartCategory category,
                 String manufacturer, BigDecimal price, String description,
                 int quantity, String location);

    Part updatePart(String partId, String name, String partNumber, PartCategory category,
                    String manufacturer, BigDecimal price, String description,
                    Integer quantity, String location, PartStatus status);

    List<Part> getAllParts();
    Part getPartById(String id);
    void removePart(String partId);

}