package com.edipo.ledger.unit.application;

import com.edipo.ledger.application.service.AccountService;
import com.edipo.ledger.application.CreateAccountCommand;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    @DisplayName("Should create account when document number does not exist")
    void shouldCreateAccount_whenDocumentNumberDoesNotExist() {
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber);

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(false);

        Account savedAccount = new Account(1L, documentNumber);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        Account result = accountService.createAccount(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(documentNumber, result.documentNumber());

        verify(accountRepository).existsByDocumentNumber(documentNumber);
        verify(accountRepository).save(argThat(account ->
                account.id() == null &&
                documentNumber.equals(account.documentNumber())
        ));
    }

    @Test
    @DisplayName("Should throw exception when document number already exists")
    void shouldThrowException_whenDocumentNumberAlreadyExists() {
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber);

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(true);

        DuplicateDocumentException exception = assertThrows(
                DuplicateDocumentException.class,
                () -> accountService.createAccount(command)
        );

        assertEquals(
                "Account already exists for document number: 12345678900",
                exception.getMessage()
        );

        verify(accountRepository).existsByDocumentNumber(documentNumber);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should return account when account exists")
    void shouldReturnAccount_whenAccountExists() {
        long accountId = 1L;
        Account account = new Account(accountId, "12345678900");

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        Account result = accountService.getById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.id());
        assertEquals("12345678900", result.documentNumber());

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when account does not exist")
    void shouldThrowException_whenAccountDoesNotExist() {
        long accountId = 999L;

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getById(accountId)
        );

        assertEquals("Account not found for id: 999", exception.getMessage());
        verify(accountRepository).findById(accountId);
    }
}