package org.example.infrastructure.repository;

import org.example.domain.model.car.CarModel;
import org.example.domain.model.enums.BodyType;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class InMemoryCarModelRepository extends InMemoryBaseRepository<CarModel, String> {

    @Override
    protected String getId(CarModel model) {

        return model.getId();
    }

    @Override
    protected void validate(CarModel model) {

        if (model == null) {
            throw new IllegalArgumentException("Car model cannot be null");
        }
    }

    public Optional<CarModel> findByBrandAndModel(String brand, String model) {

        return findFirst(m ->
                m.getBrand().equalsIgnoreCase(brand) &&
                        m.getModel().equalsIgnoreCase(model)
        );
    }

    public List<CarModel> findByBrand(String brand) {

        return find(m -> m.getBrand().equalsIgnoreCase(brand));
    }

    public List<CarModel> findByBodyType(BodyType bodyType) {

        return find(m -> m.getBodyType() == bodyType);
    }

    public List<CarModel> findByPriceLessThan(BigDecimal maxPrice) {

        return find(m -> m.getBasePrice().compareTo(maxPrice) <= 0);
    }
}