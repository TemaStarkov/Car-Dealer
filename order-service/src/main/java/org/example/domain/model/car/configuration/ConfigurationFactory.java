package org.example.domain.model.car.configuration;

import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.enums.ComponentType;
import org.example.domain.exception.DomainValidationException;
import org.example.domain.exception.IncompatibleComponentException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class ConfigurationFactory {

    public CarConfiguration createValid(CarModel model, Map<ComponentType, Component> components) {
        validateRequiredComponents(model, components);
        validateCompatibility(model, components);

        BigDecimal total = calculateTotalPrice(model, components);

        return CarConfiguration.builder()
                .model(model)
                .components(components)
                .totalPrice(total)
                .isValid(true)
                .validationMessage("Конфигурация валидна")
                .build();
    }

    public CarConfiguration createInvalid(CarModel model, Map<ComponentType, Component> components, String errorMessage) {
        BigDecimal total = calculateTotalPrice(model, components);

        return CarConfiguration.builder()
                .model(model)
                .components(components)
                .totalPrice(total)
                .isValid(false)
                .validationMessage(errorMessage)
                .build();
    }

    private void validateRequiredComponents(CarModel model, Map<ComponentType, Component> components) {
        ComponentType[] required = {
                ComponentType.WHEELS,
                ComponentType.TRANSMISSION,
                ComponentType.STEERING_WHEEL,
                ComponentType.INTERIOR
        };

        String missing = java.util.Arrays.stream(required)
                .filter(type -> !components.containsKey(type))
                .map(ComponentType::getDisplayName)
                .collect(Collectors.joining(", "));

        if (!missing.isEmpty()) {
            throw new DomainValidationException(
                    "Отсутствуют обязательные узлы: " + missing
            );
        }
    }

    private void validateCompatibility(CarModel model, Map<ComponentType, Component> components) {
        String incompatible = components.values().stream()
                .filter(component -> !component.isCompatibleWith(model))
                .map(Component::getName)
                .collect(Collectors.joining(", "));

        if (!incompatible.isEmpty()) {
            throw new IncompatibleComponentException(
                    "Несовместимые компоненты для модели " + model.getModel() +
                            ": " + incompatible
            );
        }
    }

    private BigDecimal calculateTotalPrice(CarModel model, Map<ComponentType, Component> components) {
        BigDecimal componentsPrice = components.values().stream()
                .map(Component::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return model.getBasePrice().add(componentsPrice);
    }
}
