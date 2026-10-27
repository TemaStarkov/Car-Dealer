package org.example.infrastructure.persistence.entity.car.configuration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.example.domain.model.enums.ComponentType;
import org.example.infrastructure.persistence.entity.BaseEntity;
import org.example.infrastructure.persistence.entity.car.CarModelEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "components")
public class ComponentEntity extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ComponentType type;

    @Column(length = 512)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "component_compatible_models",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "car_model_id")
    )
    private Set<CarModelEntity> compatibleModels = new HashSet<>();

    @Column(nullable = false)
    private boolean base;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Set<CarModelEntity> getCompatibleModels() {
        return compatibleModels;
    }

    public void setCompatibleModels(Set<CarModelEntity> compatibleModels) {
        this.compatibleModels = compatibleModels;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }
}
