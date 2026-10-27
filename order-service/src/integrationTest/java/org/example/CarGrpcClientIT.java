package org.example;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.*;
import org.example.integration.grpc.CarGrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CarGrpcClientIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private CarGrpcClient carGrpcClient;

    @TestConfiguration
    static class TestConfig {
        @GrpcService
        public static class MockCarService extends CarServiceGrpc.CarServiceImplBase {
            @Override
            public void listAvailableCars(EmptyRequest request, StreamObserver<CarListResponse> responseObserver) {
                CarListResponse response = CarListResponse.newBuilder()
                        .addCars(CarResponse.newBuilder()
                                .setId("test-id")
                                .setBrand("TestBrand")
                                .setModel("TestModel")
                                .setStatus("AVAILABLE")
                                .setFuelType("GASOLINE")
                                .setBodyType("SEDAN")
                                .setTransmissionType("AUTOMATIC")
                                .setDriveType("AWD")
                                .build())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }
    }

    @Test
    void shouldCallGrpcServerAndReturnCars() {
        List<CarResponse> cars = carGrpcClient.listAvailableCars();
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("TestBrand", cars.get(0).getBrand());
    }
}
