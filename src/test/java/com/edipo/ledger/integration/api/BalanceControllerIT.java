package com.edipo.ledger.integration.api;

import com.edipo.ledger.api.controller.BalanceController;
import com.edipo.ledger.application.AccountService;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    @DisplayName("should return account balance when request is valid")
    void shouldReturnAccountBalanceWhenRequestIsValid() throws Exception {
        when(accountService.getBalance(1L)).thenReturn(new BigDecimal("23.45"));

        mockMvc.perform(get("/balance?accountId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.balance").value(23.45));
    }

    @Test
    @DisplayName("should return 400 when account id type is invalid")
    void shouldReturn400WhenAccountIdTypeIsInvalid() throws Exception {
        mockMvc.perform(get("/balance?accountId=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'accountId'"))
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when account id value is invalid")
    void shouldReturn400WhenAccountIdValueIsInvalid() throws Exception {
        when(accountService.getBalance(0L)).thenThrow(new IllegalArgumentException("Account id must be a positive number"));

        mockMvc.perform(get("/balance?accountId=0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account id must be a positive number"))
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 400 when account id is missing")
    void shouldReturn400WhenAccountIdIsMissing() throws Exception {
        mockMvc.perform(get("/balance"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Missing required parameter 'accountId'"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return 404 when account does not exist")
    void shouldReturn404WhenAccountDoesNotExist() throws Exception {
        when(accountService.getBalance(99L)).thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/balance?accountId=99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found for id: 99"))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}