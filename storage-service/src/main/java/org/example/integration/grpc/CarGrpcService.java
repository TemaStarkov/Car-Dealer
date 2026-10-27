package org.example.integration.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.application.service.WarehouseService;
import org.example.domain.model.car.Car;
import org.example.grpc.*;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class CarGrpcService extends CarServiceGrpc.CarServiceImplBase {

    private final WarehouseService warehouseService;

    public CarGrpcService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public void listAvailableCars(EmptyRequest request, StreamObserver<CarListResponse> responseObserver) {
        List<Car> availableCars = warehouseService.getAllCars().stream()
                .filter(Car::isAvailable)
                .toList();

        CarListResponse response = CarListResponse.newBuilder()
                .addAllCars(availableCars.stream()
                        .map(this::mapToCarResponse)
                        .collect(Collectors.toList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCarById(CarRequest request, StreamObserver<CarResponse> responseObserver) {
        try {
            Car car = warehouseService.getCarById(request.getId());
            responseObserver.onNext(mapToCarResponse(car));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private CarResponse mapToCarResponse(Car car) {
        return CarResponse.newBuilder()
                .setId(car.getId())
                .setBrand(car.getBrand())
                .setModel(car.getModel())
                .setYear(car.getYear())
                .setBodyType(car.getBodyType() != null ? car.getBodyType().name() : "")
                .setFuelType(car.getFuelType() != null ? car.getFuelType().name() : "")
                .setTransmissionType(car.getTransmissionType() != null ? car.getTransmissionType().name() : "")
                .setDriveType(car.getDriveType() != null ? car.getDriveType().name() : "")
                .setColor(car.getColor() != null ? car.getColor() : "")
                .setPrice(car.getPrice() != null ? car.getPrice().doubleValue() : 0.0)
                .setStatus(car.getStatus() != null ? car.getStatus().name() : "")
                .setVin(car.getVin() != null ? car.getVin() : "")
                .setEnginePower(car.getEngine() != null ? car.getEngine().getPower() : 0.0)
                .setEngineVolume(car.getEngine() != null ? car.getEngine().getVolume() : 0.0)
                .build();
    }
}
