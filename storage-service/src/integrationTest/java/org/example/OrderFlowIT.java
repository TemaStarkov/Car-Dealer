package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.OrderEntityRepository;
import org.example.infrastructure.persistence.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private CarEntityRepository carRepository;

    @Autowired
    private OrderEntityRepository orderRepository;

    @Test
    void shouldCreateInStockOrderAndUpdateStatus() throws Exception {
        String customerId = userRepository.findAllByRoleAndRemovedFalse(org.example.domain.model.enums.UserRole.USER)
                .getFirst()
                .getId()
                .toString();
        String carId = carRepository.findAllByStatusAndRemovedFalse(org.example.domain.model.enums.CarStatus.AVAILABLE)
                .getFirst()
                .getId()
                .toString();

        String createPayload = """
                {
                  "customerId": "%s",
                  "carId": "%s"
                }
                """.formatted(customerId, carId);

        String responseBody = mockMvc.perform(post("/api/orders/in-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("IN_STOCK"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode responseJson = objectMapper.readTree(responseBody);
        String orderId = responseJson.get("id").asText();

        String updatePayload = """
                {
                  "status": "APPROVED_BY_MANAGER"
                }
                """;

        mockMvc.perform(patch("/api/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED_BY_MANAGER"));

        assertFalse(orderRepository.findAllByRemovedFalse().isEmpty());
        assertEquals(org.example.domain.model.enums.CarStatus.SOLD,
                carRepository.findByIdAndRemovedFalse(java.util.UUID.fromString(carId)).orElseThrow().getStatus());
    }
}
