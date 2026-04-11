package com.edipo.ledger.integration.api;

import com.edipo.ledger.api.controller.AccountController;
import com.edipo.ledger.application.service.AccountService;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import com.edipo.ledger.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    @DisplayName("should create account and return 201")
    void shouldCreateAccountAndReturn201() throws Exception {
        String requestBody = """
            {
              "document_number": "12345678900"
            }
            """;

        Account createdAccount = new Account(1L, "12345678900");

        when(accountService.createAccount(argThat(command ->
                "12345678900".equals(command.documentNumber())
        ))).thenReturn(createdAccount);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    @DisplayName("should return custom 400 error when document number is blank")
    void shouldReturnCustom400ErrorWhenDocumentNumberIsBlank() throws Exception {
        String requestBody = """
            {
              "document_number": ""
            }
            """;

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("document_number: must not be blank"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return custom 400 error when document number is missing")
    void shouldReturnCustom400ErrorWhenDocumentNumberIsMissing() throws Exception {
        String requestBody = """
            {
            }
            """;

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("document_number: must not be blank"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return custom 409 error when document number already exists")
    void shouldReturnCustom409ErrorWhenDocumentNumberAlreadyExists() throws Exception {
        String requestBody = """
            {
              "document_number": "12345678900"
            }
            """;

        when(accountService.createAccount(argThat(command ->
                "12345678900".equals(command.documentNumber())
        ))).thenThrow(new DuplicateDocumentException("12345678900"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account already exists for document number: 12345678900"))
                .andExpect(jsonPath("$.code").value("DUPLICATE_DOCUMENT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return account when account exists")
    void shouldReturnAccountWhenAccountExists() throws Exception {
        Account account = new Account(1L, "12345678900");

        when(accountService.getById(1L)).thenReturn(account);

        mockMvc.perform(get("/accounts?accountId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    @DisplayName("should return custom 404 error when account does not exist")
    void shouldReturnCustom404ErrorWhenAccountDoesNotExist() throws Exception {
        when(accountService.getById(99L))
                .thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/accounts?accountId=99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found for id: 99"))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return custom 400 error when account id is invalid")
    void shouldReturnCustom400ErrorWhenAccountIdIsInvalid() throws Exception {
        mockMvc.perform(get("/accounts?accountId=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'accountId'"))
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}