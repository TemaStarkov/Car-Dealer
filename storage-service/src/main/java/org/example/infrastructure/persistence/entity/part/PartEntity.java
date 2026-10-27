package org.example.infrastructure.persistence.entity.part;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.example.domain.model.enums.PartCategory;
import org.example.domain.model.enums.PartStatus;
import org.example.infrastructure.persistence.entity.BaseEntity;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parts")
public class PartEntity extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String partNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PartCategory category;

    @Column(nullable = false, length = 128)
    private String manufacturer;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(length = 512)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "part_compatible_models",
            joinColumns = @JoinColumn(name = "part_id"),
            inverseJoinColumns = @JoinColumn(name = "car_model_id")
    )
    private Set<CarModelEntity> compatibleModels = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PartStatus status;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 64)
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public PartCategory getCategory() {
        return category;
    }

    public void setCategory(PartCategory category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CarModelEntity> getCompatibleModels() {
        return compatibleModels;
    }

    public void setCompatibleModels(Set<CarModelEntity> compatibleModels) {
        this.compatibleModels = compatibleModels;
    }

    public PartStatus getStatus() {
        return status;
    }

    public void setStatus(PartStatus status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
