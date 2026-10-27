package org.example.domain.model.car.configuration;

import lombok.Builder;
import lombok.Value;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class CarConfiguration {

    @Builder.Default
    String id = UUID.randomUUID().toString();

    CarModel model;
    Map<ComponentType, Component> components;
    BigDecimal totalPrice;
    boolean isValid;
    String validationMessage;

    private CarConfiguration(
            String id,
            CarModel model,
            Map<ComponentType, Component> components,
            BigDecimal totalPrice,
            boolean isValid,
            String validationMessage
    ) {
        this.id = id;
        this.model = model;
        this.components = components != null ? Map.copyOf(components) : Map.of();
        this.totalPrice = totalPrice;
        this.isValid = isValid;
        this.validationMessage = validationMessage;
    }

    public CarConfiguration withComponent(ComponentType type, Component newComponent) {
        Map<ComponentType, Component> newComponents = new HashMap<>(components);
        newComponents.put(type, newComponent);

        return toBuilder()
                .components(newComponents)
                .build();
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }

    public boolean hasRequiredComponents() {
        ComponentType[] required = {
                ComponentType.WHEELS,
                ComponentType.TRANSMISSION,
                ComponentType.STEERING_WHEEL,
                ComponentType.INTERIOR
        };

        return Arrays.stream(required)
                .allMatch(components::containsKey);
    }

    public String getConfigurationDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Модель: ").append(model.getBrand() + " " + model.getModel()).append("\n");
        sb.append("Базовая цена: ").append(model.getBasePrice()).append(" ₽\n");
        sb.append("Выбранные компоненты:\n");

        components.forEach((type, component) -> {
            String sign = component.getPrice().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            sb.append("  - ").append(type.getDisplayName()).append(": ")
                    .append(component.getName()).append(" (")
                    .append(sign).append(component.getPrice()).append(" ₽)\n");
        });

        sb.append("Итоговая цена: ").append(totalPrice).append(" ₽\n");
        sb.append("Статус: ").append(isValid ? "Валидна" : "Невалидна");

        if (!isValid) {
            sb.append("\nОшибка: ").append(validationMessage);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Configuration for %s %s: %s ₽ (%s)",
                model.getBrand(), model.getModel(), totalPrice, isValid ? "valid" : "invalid");
    }
}