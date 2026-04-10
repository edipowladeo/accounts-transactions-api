package com.edipo.ledger.unit.application;

import com.edipo.ledger.application.service.AccountService;
import com.edipo.ledger.application.CreateAccountCommand;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AccountServiceCreateTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    @DisplayName("when document number does not exist, then creates account")
    void whenDocumentNumberDoesNotExist_thenCreatesAccount() {
        // given
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber);

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(false);

        Account savedAccount = new Account(1L, documentNumber);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        // when
        Account result = accountService.createAccount(command);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(documentNumber, result.getDocumentNumber());

        verify(accountRepository).existsByDocumentNumber(documentNumber);
        verify(accountRepository).save(argThat(account ->
                account.getId() == null &&
                documentNumber.equals(account.getDocumentNumber())
        ));
    }

    @Test
    @DisplayName("when document number already exists, then throws exception")
    void whenDocumentNumberAlreadyExists_thenThrowsException() {
        // given
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber);

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(true);

        // when / then
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
}