package com.edipo.ledger.unit.application;

import com.edipo.ledger.application.AccountService;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GetAccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    @DisplayName("should return account when account exists")
    void shouldReturnAccountWhenAccountExists() {
        // given
        long accountId = 1L;
        Account account = new Account(accountId, "12345678900");

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        // when
        Account result = accountService.getById(accountId);

        // then
        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals("12345678900", result.getDocumentNumber());

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("should throw exception when account does not exist")
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        // given
        long accountId = 999L;

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // when / then
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getById(accountId)
        );

        assertEquals("Account not found for id: 999", exception.getMessage());
        verify(accountRepository).findById(accountId);
    }
}