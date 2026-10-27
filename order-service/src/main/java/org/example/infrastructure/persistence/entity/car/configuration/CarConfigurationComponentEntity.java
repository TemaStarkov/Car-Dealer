package org.example.infrastructure.persistence.entity.car.configuration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.example.domain.model.enums.ComponentType;
import org.example.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "car_configuration_components")
public class CarConfigurationComponentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuration_id", nullable = false)
    private CarConfigurationEntity configuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ComponentType componentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    private ComponentEntity component;

    public CarConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CarConfigurationEntity configuration) {
        this.configuration = configuration;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentEntity getComponent() {
        return component;
    }

    public void setComponent(ComponentEntity component) {
        this.component = component;
    }
}
