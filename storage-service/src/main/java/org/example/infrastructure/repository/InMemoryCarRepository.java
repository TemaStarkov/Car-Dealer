package org.example.infrastructure.repository;

import org.example.domain.model.car.Car;
import org.example.domain.model.enums.CarStatus;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class InMemoryCarRepository extends InMemoryBaseRepository<Car, String> {

    @Override
    protected String getId(Car car) {

        return car.getId();
    }

    @Override
    protected void validate(Car car) {

        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
    }

    public Optional<Car> findByVin(String vin) {

        return findFirst(car -> car.getVin().equals(vin));
    }

    public List<Car> findAvailable() {

        return find(Car::isAvailable);
    }

    public List<Car> findByBrand(String brand) {

        return find(car -> car.getBrand().equalsIgnoreCase(brand));
    }

    public List<Car> findByModel(String model) {

        return find(car -> car.getModel().equalsIgnoreCase(model));
    }

    public List<Car> findByStatus(CarStatus status) {

        return find(car -> car.getStatus() == status);
    }

    public List<Car> findByPriceRange(BigDecimal min, BigDecimal max) {

        return find(car ->
                car.getPrice().compareTo(min) >= 0 &&
                        car.getPrice().compareTo(max) <= 0
        );
    }

    public List<Car> findForTestDrive() {

        return find(Car::isForTestDrive);
    }
}