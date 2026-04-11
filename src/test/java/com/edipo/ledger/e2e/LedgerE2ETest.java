package com.edipo.ledger.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LedgerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM transactions");
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    @DisplayName("should create account end to end")
    void shouldCreateAccountEndToEnd() throws Exception {
        String requestBody = """
                {
                  "document_number": "12345678900"
                }
                """;

        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account_id").isNumber())
                .andExpect(jsonPath("$.document_number").value("12345678900"))
                .andReturn();

        JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
        long accountId = responseJson.get("account_id").asLong();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE account_id = ? AND document_number = ?",
                Integer.class,
                accountId,
                "12345678900"
        );

        assertNotNull(count);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("should find account by id end to end")
    void shouldFindAccountByIdEndToEnd() throws Exception {
        jdbcTemplate.update(
                "INSERT INTO accounts (account_id, document_number) VALUES (?, ?)",
                100L,
                "99999999900"
        );

        mockMvc.perform(get("/accounts/100"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account_id").value(100))
                .andExpect(jsonPath("$.document_number").value("99999999900"));
    }

    @Test
    @DisplayName("should create transaction end to end")
    void shouldCreateTransactionEndToEnd() throws Exception {
        jdbcTemplate.update(
                "INSERT INTO accounts (account_id, document_number) VALUES (?, ?)",
                200L,
                "88888888800"
        );

        String requestBody = """
                {
                  "account_id": 200,
                  "operation_type_id": 1,
                  "amount": 100.00
                }
                """;

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transaction_id").isNumber())
                .andExpect(jsonPath("$.account_id").value(200))
                .andExpect(jsonPath("$.operation_type_id").value(1))
                .andExpect(jsonPath("$.amount").value(-100.00))
                .andExpect(jsonPath("$.created_at").exists())
                .andReturn();

        JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
        long transactionId = responseJson.get("transaction_id").asLong();

        Long persistedAccountId = jdbcTemplate.queryForObject(
                "SELECT account_id FROM transactions WHERE transaction_id = ?",
                Long.class,
                transactionId
        );

        Integer persistedOperationTypeId = jdbcTemplate.queryForObject(
                "SELECT operation_type_id FROM transactions WHERE transaction_id = ?",
                Integer.class,
                transactionId
        );

        BigDecimal persistedAmount = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions WHERE transaction_id = ?",
                BigDecimal.class,
                transactionId
        );

        Timestamp persistedEventDate = jdbcTemplate.queryForObject(
                "SELECT event_date FROM transactions WHERE transaction_id = ?",
                Timestamp.class,
                transactionId
        );

        assertEquals(200L, persistedAccountId);
        assertEquals(1, persistedOperationTypeId);
        assertEquals(0, new BigDecimal("-100.00").compareTo(persistedAmount));
        assertNotNull(persistedEventDate);
    }
}