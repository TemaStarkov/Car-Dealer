package org.example.domain.model.part;

import lombok.Builder;
import lombok.Value;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.enums.PartCategory;
import org.example.domain.model.enums.PartStatus;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Part {

    @Builder.Default
    String id = UUID.randomUUID().toString();

    String name;
    String partNumber;
    PartCategory category;
    String manufacturer;
    BigDecimal price;
    String description;

    @Builder.Default
    Set<CarModel> compatibleModels = Set.of();

    @Builder.Default
    PartStatus status = PartStatus.IN_STOCK;

    int quantity;
    String location;

    private Part(
            String id,
            String name,
            String partNumber,
            PartCategory category,
            String manufacturer,
            BigDecimal price,
            String description,
            Set<CarModel> compatibleModels,
            PartStatus status,
            int quantity,
            String location
    ) {

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.partNumber = partNumber;
        this.category = category;
        this.manufacturer = manufacturer;
        this.price = price;
        this.description = description;
        this.compatibleModels = compatibleModels != null ? compatibleModels : Set.of();
        this.status = status != null ? status : PartStatus.IN_STOCK;
        this.quantity = quantity;
        this.location = location;

        validate();
    }

    private void validate() {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Part name is required");
        }

        if (partNumber == null || partNumber.isBlank()) {
            throw new IllegalArgumentException("Part number is required");
        }

        if (category == null) {
            throw new IllegalArgumentException("Part category is required");
        }

        if (manufacturer == null || manufacturer.isBlank()) {
            throw new IllegalArgumentException("Manufacturer is required");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public boolean isCompatibleWith(CarModel model) {

        return compatibleModels.isEmpty() || compatibleModels.contains(model);
    }

    public boolean isInStock() {

        return status == PartStatus.IN_STOCK && quantity > 0;
    }

    public Part decreaseQuantity(int amount) {

        return toBuilder()
                .quantity(quantity - amount)
                .status((quantity - amount) == 0 ? PartStatus.OUT_OF_STOCK : status)
                .build();
    }

    public Part increaseQuantity(int amount) {

        return toBuilder()
                .quantity(quantity + amount)
                .status(PartStatus.IN_STOCK)
                .build();
    }

    public Part updatePrice(BigDecimal newPrice) {

        return toBuilder()
                .price(newPrice)
                .build();
    }

    public Part addCompatibleModel(CarModel model) {

        Set<CarModel> newCompatible = new HashSet<>(compatibleModels);
        newCompatible.add(model);

        return toBuilder()
                .compatibleModels(Set.copyOf(newCompatible))
                .build();
    }

    @Override
    public String toString() {

        return String.format("%s (%s) - %s - %s ₽ [%d шт.]",
                name, partNumber, manufacturer, price, quantity);
    }
}