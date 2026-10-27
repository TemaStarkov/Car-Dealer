package org.example.domain.model.car.configuration;

import lombok.Builder;
import lombok.Value;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Component {

    @Builder.Default
    String id = UUID.randomUUID().toString();

    String name;
    ComponentType type;
    String description;
    BigDecimal price;

    @Builder.Default
    Set<CarModel> compatibleModels = Set.of();

    boolean isBase;

    private Component(
            String id,
            String name,
            ComponentType type,
            String description,
            BigDecimal price,
            Set<CarModel> compatibleModels,
            boolean isBase
    ) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.compatibleModels = compatibleModels != null ? compatibleModels : Set.of();
        this.isBase = isBase;

        validate();
    }

    private void validate() {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Component name is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Component type is required");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price is required");
        }
        if (compatibleModels.isEmpty() && !isBase) {
            throw new IllegalArgumentException("Non-base component must have compatible models");
        }
    }


    public boolean isCompatibleWith(CarModel model) {

        return compatibleModels.contains(model);
    }

    @Override
    public String toString() {

        String sign = price.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";

        return String.format("%s [%s]: %s%s ₽", name, type, sign, price);
    }
}