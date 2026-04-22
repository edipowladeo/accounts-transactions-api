package com.edipo.ledger.integration.api;

import com.edipo.ledger.SecurityConfig;
import com.edipo.ledger.api.controller.TransactionController;
import com.edipo.ledger.application.CreateTransactionCommand;
import com.edipo.ledger.application.TransactionService;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.model.OperationType;
import com.edipo.ledger.domain.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @DisplayName("should create transaction and return 201")
    void shouldCreateTransactionAndReturn201() throws Exception {
        String requestBody = """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": 123.45
            }
            """;

        OffsetDateTime eventDate = OffsetDateTime.parse("2026-04-10T10:15:30Z");
        Transaction createdTransaction = new Transaction(
                10L,
                1L,
                OperationType.PAYMENT,
                new BigDecimal("123.45"),
                eventDate
        );

        when(transactionService.create(any(CreateTransactionCommand.class)))
                .thenReturn(createdTransaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transaction_id").value(10))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.operation_type_id").value(4))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.created_at").value("2026-04-10T10:15:30Z"));

        ArgumentCaptor<CreateTransactionCommand> captor =
                ArgumentCaptor.forClass(CreateTransactionCommand.class);
        verify(transactionService).create(captor.capture());
        CreateTransactionCommand captured = captor.getValue();
        assertEquals(1L, captured.accountId());
        assertEquals(4, captured.operationTypeId());
        assertEquals(0, captured.amount().compareTo(new BigDecimal("123.45")));
    }

    @Test
    @DisplayName("should return 400 when accountId is null")
    void shouldReturn400WhenAccountIdIsNull() throws Exception {
        String requestBody = """
            {
              "account_id": null,
              "operation_type_id": 4,
              "amount": 123.45
            }
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("account_id: must not be null"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when operationTypeId is null")
    void shouldReturn400WhenOperationTypeIdIsNull() throws Exception {
        String requestBody = """
            {
              "account_id": 1,
              "operation_type_id": null,
              "amount": 123.45
            }
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("operation_type_id: must not be null"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when amount is null")
    void shouldReturn400WhenAmountIsNull() throws Exception {
        String requestBody = """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": null
            }
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("amount: must not be null"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- Validation: amount constraints ---

    @Test
    @DisplayName("should return 400 when amount is zero")
    void shouldReturn400WhenAmountIsZero() throws Exception {
        String requestBody = """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": 0
            }
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("amount: must be greater than 0"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when amount is negative")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        String requestBody = """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": -10.00
            }
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("amount: must be greater than 0"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- Validation: malformed / empty body ---

    @Test
    @DisplayName("should return 400 when request body is empty")
    void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when request body is malformed JSON")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        String malformedJson = """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": 123.45
            """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- Domain errors ---

    @Test
    @DisplayName("should return 404 when account does not exist")
    void shouldReturn404WhenAccountDoesNotExist() throws Exception {
        String requestBody = """
            {
              "account_id": 99,
              "operation_type_id": 4,
              "amount": 123.45
            }
            """;

        when(transactionService.create(any(CreateTransactionCommand.class)))
                .thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found for id: 99"))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}