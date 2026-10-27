package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.CarListResponse;
import org.example.grpc.CarServiceGrpc;
import org.example.grpc.EmptyRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CarGrpcServiceIT extends AbstractPostgresIntegrationTest {

    private ManagedChannel channel;
    private CarServiceGrpc.CarServiceBlockingStub blockingStub;

    @BeforeEach
    void setup() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .build();
        blockingStub = CarServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    @Test
    void shouldListAvailableCarsViaGrpc() {
        CarListResponse response = blockingStub.listAvailableCars(EmptyRequest.newBuilder().build());
        assertNotNull(response);
        assertFalse(response.getCarsList().isEmpty(), "Cars list should not be empty if seeded");
    }
}
