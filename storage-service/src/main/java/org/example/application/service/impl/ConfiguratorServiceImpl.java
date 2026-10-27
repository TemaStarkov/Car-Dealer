package org.example.application.service.impl;

import org.example.application.service.ConfiguratorService;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;
import org.example.domain.model.car.configuration.ConfigurationFactory;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;
import org.example.infrastructure.persistence.mapper.PersistenceMapper;
import org.example.infrastructure.persistence.repository.CarModelEntityRepository;
import org.example.infrastructure.persistence.repository.ComponentEntityRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfiguratorServiceImpl implements ConfiguratorService {

    private final CarModelEntityRepository carModelRepository;
    private final ComponentEntityRepository componentRepository;
    private final PersistenceMapper persistenceMapper;
    private final ConfigurationFactory configurationFactory;

    public ConfiguratorServiceImpl(CarModelEntityRepository carModelRepository,
                                   ComponentEntityRepository componentRepository,
                                   PersistenceMapper persistenceMapper,
                                   ConfigurationFactory configurationFactory) {
        this.carModelRepository = carModelRepository;
        this.componentRepository = componentRepository;
        this.persistenceMapper = persistenceMapper;
        this.configurationFactory = configurationFactory;
    }

    @Override
    public CarConfiguration configureCar(String modelId, Map<ComponentType, String> componentChoices) {
        CarModel model = carModelRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(modelId))
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelId));

        Map<ComponentType, Component> selectedComponents = new HashMap<>();

        for (Map.Entry<ComponentType, String> entry : componentChoices.entrySet()) {
            Component component = componentRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(entry.getValue()))
                    .map(persistenceMapper::toDomain)
                    .orElseThrow(() -> new EntityNotFoundException("Component not found: " + entry.getValue()));
            selectedComponents.put(entry.getKey(), component);
        }

        ComponentType[] requiredTypes = {
                ComponentType.WHEELS,
                ComponentType.TRANSMISSION,
                ComponentType.STEERING_WHEEL,
                ComponentType.INTERIOR
        };

        for (ComponentType type : requiredTypes) {
            if (!selectedComponents.containsKey(type)) {
                componentRepository.findFirstByTypeAndBaseTrueAndRemovedFalse(type)
                        .map(persistenceMapper::toDomain)
                        .ifPresent(base -> selectedComponents.put(type, base));
            }
        }

        try {
            return configurationFactory.createValid(model, selectedComponents);
        } catch (Exception e) {
            return configurationFactory.createInvalid(model, selectedComponents, e.getMessage());
        }
    }

    @Override
    public List<Component> getCompatibleComponents(String modelId, ComponentType componentType) {
        CarModelEntity modelEntity = carModelRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(modelId))
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelId));

        return componentRepository.findAllByTypeAndCompatibleModelsContainingAndRemovedFalse(componentType, modelEntity).stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateConfiguration(String modelId, Map<ComponentType, String> componentChoices) {
        try {
            configureCar(modelId, componentChoices);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
