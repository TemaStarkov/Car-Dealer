package org.example.integration.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.exception.ServiceUnavailableException;
import org.example.grpc.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarGrpcClient {

    @GrpcClient("storage-service")
    private CarServiceGrpc.CarServiceBlockingStub carServiceBlockingStub;

    public List<CarResponse> listAvailableCars() {
        try {
            CarListResponse response = carServiceBlockingStub.listAvailableCars(EmptyRequest.newBuilder().build());
            return response.getCarsList();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE || e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new ServiceUnavailableException("Storage service is unavailable", e);
            }
            throw new RuntimeException("Error calling storage-service via gRPC: " + e.getStatus(), e);
        }
    }

    public CarResponse getCarById(String id) {
        try {
            return carServiceBlockingStub.getCarById(CarRequest.newBuilder().setId(id).build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new EntityNotFoundException("Car not found with id: " + id);
            }
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE || e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new ServiceUnavailableException("Storage service is unavailable", e);
            }
            throw new RuntimeException("Error calling storage-service via gRPC: " + e.getStatus(), e);
        }
    }
}
