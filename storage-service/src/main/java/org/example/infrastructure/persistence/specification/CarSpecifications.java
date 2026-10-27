package org.example.infrastructure.persistence.specification;

import org.example.domain.model.car.SearchFilters;
import org.example.infrastructure.persistence.entity.car.CarEntity;
import org.springframework.data.jpa.domain.Specification;

public final class CarSpecifications {

    private CarSpecifications() {
    }

    public static Specification<CarEntity> withFilters(SearchFilters filters) {
        return Specification.where(notRemoved())
                .and(statusAvailable())
                .and(hasBrand(filters.getBrand()))
                .and(hasModel(filters.getModel().orElse(null)))
                .and(hasBodyType(filters.getBodyType()))
                .and(hasFuelType(filters.getFuelType()))
                .and(hasTransmission(filters.getTransmissionType()))
                .and(hasDriveType(filters.getDriveType()))
                .and(hasColor(filters.getColor()))
                .and(hasMinPrice(filters.getMinPrice()))
                .and(hasMaxPrice(filters.getMaxPrice()))
                .and(hasMinPower(filters.getMinPower()))
                .and(hasMaxPower(filters.getMaxPower()))
                .and(hasMinVolume(filters.getMinVolume()))
                .and(hasMaxVolume(filters.getMaxVolume()));
    }

    private static Specification<CarEntity> notRemoved() {
        return (root, query, cb) -> cb.isFalse(root.get("removed"));
    }

    private static Specification<CarEntity> statusAvailable() {
        return (root, query, cb) -> cb.equal(root.get("status"), org.example.domain.model.enums.CarStatus.AVAILABLE);
    }

    private static Specification<CarEntity> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? cb.conjunction() : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    private static Specification<CarEntity> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? cb.conjunction() : cb.equal(cb.lower(root.get("model")), model.toLowerCase());
    }

    private static Specification<CarEntity> hasBodyType(Object bodyType) {
        return (root, query, cb) -> bodyType == null ? cb.conjunction() : cb.equal(root.get("bodyType"), bodyType);
    }

    private static Specification<CarEntity> hasFuelType(Object fuelType) {
        return (root, query, cb) -> fuelType == null ? cb.conjunction() : cb.equal(root.get("fuelType"), fuelType);
    }

    private static Specification<CarEntity> hasTransmission(Object transmissionType) {
        return (root, query, cb) -> transmissionType == null ? cb.conjunction() : cb.equal(root.get("transmissionType"), transmissionType);
    }

    private static Specification<CarEntity> hasDriveType(Object driveType) {
        return (root, query, cb) -> driveType == null ? cb.conjunction() : cb.equal(root.get("driveType"), driveType);
    }

    private static Specification<CarEntity> hasColor(String color) {
        return (root, query, cb) ->
                color == null ? cb.conjunction() : cb.equal(cb.lower(root.get("color")), color.toLowerCase());
    }

    private static Specification<CarEntity> hasMinPrice(java.math.BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<CarEntity> hasMaxPrice(java.math.BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<CarEntity> hasMinPower(Double minPower) {
        return (root, query, cb) -> minPower == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("engine").get("power"), minPower);
    }

    private static Specification<CarEntity> hasMaxPower(Double maxPower) {
        return (root, query, cb) -> maxPower == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("engine").get("power"), maxPower);
    }

    private static Specification<CarEntity> hasMinVolume(Double minVolume) {
        return (root, query, cb) -> minVolume == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("engine").get("volume"), minVolume);
    }

    private static Specification<CarEntity> hasMaxVolume(Double maxVolume) {
        return (root, query, cb) -> maxVolume == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("engine").get("volume"), maxVolume);
    }
}
