package org.example.integration.rest.mapper;

import org.example.domain.model.car.Car;
import org.example.domain.model.car.CarModel;
import org.example.domain.model.car.Engine;
import org.example.domain.model.car.SearchFilters;
import org.example.domain.model.car.configuration.CarConfiguration;
import org.example.domain.model.car.configuration.Component;
import org.example.domain.model.order.CustomOrder;
import org.example.domain.model.order.InStockOrder;
import org.example.domain.model.order.Order;
import org.example.domain.model.order.TestDriveOrder;
import org.example.domain.model.part.Part;
import org.example.integration.rest.dto.car.CarDto;
import org.example.integration.rest.dto.car.CarModelDto;
import org.example.integration.rest.dto.car.CarSearchRequest;
import org.example.integration.rest.dto.car.EngineDto;
import org.example.integration.rest.dto.configuration.CarConfigurationDto;
import org.example.integration.rest.dto.configuration.ComponentDto;
import org.example.integration.rest.dto.order.OrderDto;
import org.example.integration.rest.dto.part.PartDto;
import org.example.integration.rest.dto.testdrive.TestDriveOrderDto;
import org.mapstruct.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ApiMapper {

    EngineDto toDto(Engine engine);

    CarDto toDto(Car car);

    CarModelDto toDto(CarModel model);

    PartDto toDto(Part part);

    ComponentDto toDto(Component component);

    default CarConfigurationDto toDto(CarConfiguration configuration) {
        Map<String, ComponentDto> components = configuration.getComponents().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> toDto(entry.getValue()),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));

        return new CarConfigurationDto(
                configuration.getId(),
                toDto(configuration.getModel()),
                components,
                configuration.getTotalPrice(),
                configuration.isValid(),
                configuration.getValidationMessage()
        );
    }

    default OrderDto toDto(Order<?> order) {
        if (order instanceof InStockOrder inStockOrder) {
            return new OrderDto(
                    inStockOrder.getId(),
                    inStockOrder.getOrderNumber(),
                    inStockOrder.getStatus(),
                    inStockOrder.getType(),
                    inStockOrder.getCustomer().getFullName(),
                    inStockOrder.getManager().getFullName(),
                    inStockOrder.getCreatedAt(),
                    inStockOrder.getUpdatedAt(),
                    toDto(inStockOrder.getCar()),
                    null,
                    null,
                    null,
                    null
            );
        }

        if (order instanceof CustomOrder customOrder) {
            return new OrderDto(
                    customOrder.getId(),
                    customOrder.getOrderNumber(),
                    customOrder.getStatus(),
                    customOrder.getType(),
                    customOrder.getCustomer().getFullName(),
                    customOrder.getManager().getFullName(),
                    customOrder.getCreatedAt(),
                    customOrder.getUpdatedAt(),
                    null,
                    toDto(customOrder.getCarModel()),
                    toDto(customOrder.getConfiguration()),
                    customOrder.getExpectedDeliveryDate(),
                    customOrder.getDeliveryDate()
            );
        }

        throw new IllegalArgumentException("Unsupported order type: " + order.getClass().getName());
    }

    default TestDriveOrderDto toDto(TestDriveOrder order) {
        return new TestDriveOrderDto(
                order.getId(),
                order.getCustomer().getFullName(),
                order.getManager().getFullName(),
                toDto(order.getCar()),
                order.getRequestedTime(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    default SearchFilters toDomain(CarSearchRequest request) {
        return SearchFilters.builder()
                .minPrice(request.minPrice())
                .maxPrice(request.maxPrice())
                .brand(request.brand())
                .model(request.model())
                .bodyType(request.bodyType())
                .fuelType(request.fuelType())
                .minPower(request.minPower())
                .maxPower(request.maxPower())
                .minVolume(request.minVolume())
                .maxVolume(request.maxVolume())
                .transmissionType(request.transmissionType())
                .driveType(request.driveType())
                .color(request.color())
                .build();
    }
}
