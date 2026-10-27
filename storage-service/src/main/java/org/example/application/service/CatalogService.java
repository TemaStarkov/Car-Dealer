package org.example.application.service;

import org.example.domain.model.car.Car;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.SearchFilters;

import java.util.List;

public interface CatalogService {

    List<Car> getAvailableCars();
    List<Car> findCarsWithFilters(SearchFilters filters);
    Car getCarById(String id);
    List<CarModel> getAllModels();
    CarModel getModelById(String id);

}