package org.example;

import org.example.grpc.CarResponse;
import org.example.integration.grpc.CarGrpcClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogApiIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarGrpcClient carGrpcClient;

    @Test
    void shouldReturnCarsFromCatalogViaGrpc() throws Exception {
        CarResponse car = CarResponse.newBuilder()
                .setId("test-id")
                .setBrand("BMW")
                .setModel("320i")
                .setStatus("AVAILABLE")
                .setFuelType("GASOLINE")
                .setBodyType("SEDAN")
                .setTransmissionType("AUTOMATIC")
                .setDriveType("RWD")
                .setPrice(50000.0)
                .build();

        Mockito.when(carGrpcClient.listAvailableCars()).thenReturn(List.of(car));

        mockMvc.perform(get("/api/catalog/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("BMW"));
    }
}
