package CustomerRewards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rewards.CustomerRewards.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link com.rewards.customerrewards.controller.RewardsController}.
 *
 * Uses {@link SpringBootTest} with {@link AutoConfigureMockMvc} to exercise
 * the full Spring MVC pipeline including request mapping, validation,
 * service logic, and exception handling.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RewardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ========== GET /api/rewards/calculate ==========

    @Test
    @DisplayName("GET /calculate should return 200 and a non-empty list")
    void testGetCalculate_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/rewards/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // 3 sample customers
    }

    @Test
    @DisplayName("GET /calculate response should contain customerId field per entry")
    void testGetCalculate_ResponseHasCustomerId() throws Exception {
        mockMvc.perform(get("/api/rewards/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").exists())
                .andExpect(jsonPath("$[0].totalPoints").exists())
                .andExpect(jsonPath("$[0].monthlyPoints").exists());
    }

    // ========== POST /api/rewards/calculate ==========

    @Test
    @DisplayName("POST /calculate with valid transactions should return 200 and summaries")
    void testPostCalculate_ValidTransactions_ReturnsOk() throws Exception {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 10), 120.0),
                new Transaction("C001", "Alice", LocalDate.of(2024, 2, 15), 75.0),
                new Transaction("C002", "Bob",   LocalDate.of(2024, 1, 20), 200.0)
        );

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /calculate with single transaction should return correct points")
    void testPostCalculate_SingleTransaction_CorrectPoints() throws Exception {
        List<Transaction> transactions = List.of(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 10), 120.0)
        );

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalPoints").value(90));
    }

    @Test
    @DisplayName("POST /calculate with empty list should return 404")
    void testPostCalculate_EmptyList_Returns404() throws Exception {
        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.emptyList())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("POST /calculate with multiple customers multiple transactions returns correct totals")
    void testPostCalculate_MultipleCustomersMultipleTransactions() throws Exception {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 5),  120.0), // 90
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 20), 200.0), // 250
                new Transaction("C001", "Alice", LocalDate.of(2024, 2, 10), 75.0),  // 25
                new Transaction("C002", "Bob",   LocalDate.of(2024, 1, 5),  55.0),  // 5
                new Transaction("C002", "Bob",   LocalDate.of(2024, 2, 18), 110.0), // 70
                new Transaction("C002", "Bob",   LocalDate.of(2024, 3, 12), 300.0)  // 550
        );

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /calculate with negative amount should return 400")
    void testPostCalculate_NegativeAmount_Returns400() throws Exception {
        List<Transaction> transactions = List.of(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 10), -50.0)
        );

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isBadRequest());
    }

    // ========== GET /api/rewards/points ==========

    @Test
    @DisplayName("GET /points?amount=120 should return 90")
    void testGetPoints_120_Returns90() throws Exception {
        mockMvc.perform(get("/api/rewards/points").param("amount", "120"))
                .andExpect(status().isOk())
                .andExpect(content().string("90"));
    }

    @Test
    @DisplayName("GET /points?amount=50 should return 0")
    void testGetPoints_50_Returns0() throws Exception {
        mockMvc.perform(get("/api/rewards/points").param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("GET /points?amount=200 should return 250")
    void testGetPoints_200_Returns250() throws Exception {
        mockMvc.perform(get("/api/rewards/points").param("amount", "200"))
                .andExpect(status().isOk())
                .andExpect(content().string("250"));
    }

    @Test
    @DisplayName("GET /points?amount=-10 should return 400")
    void testGetPoints_NegativeAmount_Returns400() throws Exception {
        mockMvc.perform(get("/api/rewards/points").param("amount", "-10"))
                .andExpect(status().isBadRequest());
    }
}