package org.example.domain.model.enums;

import lombok.Getter;

@Getter
public enum ComponentType {
    WHEELS("Колеса"),
    TRANSMISSION("Трансмиссия"),
    STEERING_WHEEL("Руль"),
    INTERIOR("Интерьер салона"),
    ENGINE("Двигатель"),
    SUSPENSION("Подвеска"),
    BRAKES("Тормозная система"),
    LIGHTING("Освещение"),
    MULTIMEDIA("Мультимедиа"),
    EXTERIOR("Экстерьер");

    private final String displayName;

    ComponentType(String displayName) {
        this.displayName = displayName;
    }
}