package org.example.application.service;

import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;

import java.util.List;
import java.util.Map;

public interface ConfiguratorService {

    CarConfiguration configureCar(String modelId, Map<ComponentType, String> componentChoices);
    List<Component> getCompatibleComponents(String modelId, ComponentType componentType);
    boolean validateConfiguration(String modelId, Map<ComponentType, String> componentChoices);

}