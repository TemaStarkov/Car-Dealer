package org.example.infrastructure.repository;

import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;

public class InMemoryComponentRepository extends InMemoryBaseRepository<Component, String> {

    @Override
    protected String getId(Component component) {

        return component.getId();
    }

    @Override
    protected void validate(Component component) {

        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
    }

    public List<Component> findByType(ComponentType type) {

        return find(c -> c.getType() == type);
    }

    public List<Component> findCompatibleWithModel(CarModel model) {

        return find(c -> c.isCompatibleWith(model));
    }

    public List<Component> findBaseComponents() {

        return find(Component::isBase);
    }

    public Optional<Component> findBaseComponentByType(ComponentType type) {

        return findFirst(c -> c.getType() == type && c.isBase());
    }
}