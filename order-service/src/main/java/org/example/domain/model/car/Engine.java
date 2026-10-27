package org.example.domain.model.car;

import lombok.Builder;
import lombok.Value;
import org.example.domain.model.enums.FuelType;

@Value
@Builder
public class Engine {
    double power;
    double volume;
    FuelType fuelType;
    int cylinders;
    String model;

    public static class EngineBuilder {
        public Engine build() {

            validate();
            return new Engine(power, volume, fuelType, cylinders, model);
        }

        private void validate() {

            if (power <= 0) {
                throw new IllegalArgumentException("Power must be positive");
            }
            if (volume <= 0 && fuelType != FuelType.ELECTRIC) {
                throw new IllegalArgumentException("Volume must be positive for non-electric engines");
            }
            if (fuelType == null) {
                throw new IllegalArgumentException("Fuel type is required");
            }
        }
    }
}