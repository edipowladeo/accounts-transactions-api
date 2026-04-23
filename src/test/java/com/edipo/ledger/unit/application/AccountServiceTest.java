package com.edipo.ledger.unit.application;

import com.edipo.ledger.application.AccountService;
import com.edipo.ledger.application.CreateAccountCommand;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import com.edipo.ledger.domain.exception.InvalidAmountException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import com.edipo.ledger.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        accountService = new AccountService(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("Should create account when document number does not exist")
    void shouldCreateAccount_whenDocumentNumberDoesNotExist() {
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber, BigDecimal.ZERO);

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(false);

        Account savedAccount = new Account(1L, documentNumber, BigDecimal.ZERO);
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
        CreateAccountCommand command = new CreateAccountCommand(documentNumber, BigDecimal.ZERO);

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
    @DisplayName("Should throw exception when credit limit is negative")
    void shouldThrowException_whenDocumentNumberIsNegative() {
        String documentNumber = "12345678900";
        CreateAccountCommand command = new CreateAccountCommand(documentNumber, new BigDecimal("-10.00"));

        when(accountRepository.existsByDocumentNumber(documentNumber))
                .thenReturn(true);

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> accountService.createAccount(command)
        );

        assertEquals(
                "Available credit limit cannot be negative",
                exception.getMessage()
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should return account when account exists")
    void shouldReturnAccount_whenAccountExists() {
        long accountId = 1L;
        Account account = new Account(accountId, "12345678900", BigDecimal.ZERO);

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

    @ParameterizedTest(name = "''{0}'' should normalize to ''{1}'' and save successfully")
    @DisplayName("Should normalize document number before saving")
    @CsvSource({
            // Leading zeros only
            "002456,              002456",
            "012345678900,        012345678900",
            // Special characters only
            "123.456.789-00,      12345678900",
            "12.345.678/0001-90,  12345678000190",
            // Both special characters and leading zeros
            "012.345.678-90,      01234567890",
            "012.345.678/0001-90, 012345678000190",
            // Already clean
            "12345678900,         12345678900",
    })
    void shouldNormalizeDocumentNumber_whenCreatingAccount(String input, String normalized) {
        CreateAccountCommand command = new CreateAccountCommand(input, BigDecimal.ZERO);

        when(accountRepository.existsByDocumentNumber(normalized.trim())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(new Account(1L, normalized.trim(), BigDecimal.ZERO));

        Account result = accountService.createAccount(command);

        assertEquals(normalized.trim(), result.documentNumber());
        verify(accountRepository).existsByDocumentNumber(normalized.trim());
        verify(accountRepository).save(argThat(account ->
                normalized.trim().equals(account.documentNumber())
        ));
    }

    @ParameterizedTest(name = "''{0}'' should normalize to ''{1}'' and throw duplicate exception")
    @DisplayName("Should throw duplicate exception when normalized document already exists")
    @CsvSource({
            // Leading zeros only
            "002456,              002456",
            "012345678900,        012345678900",
            // Special characters only
            "123.456.789-00,      12345678900",
            "12.345.678/0001-90,  12345678000190",
            // Both
            "012.345.678-90,      01234567890",
            "012.345.678/0001-90, 012345678000190",
    })
    void shouldThrowDuplicate_whenNormalizedDocumentAlreadyExists(String input, String normalized) {
        CreateAccountCommand command = new CreateAccountCommand(input, BigDecimal.ZERO);

        when(accountRepository.existsByDocumentNumber(normalized.trim())).thenReturn(true);

        DuplicateDocumentException exception = assertThrows(
                DuplicateDocumentException.class,
                () -> accountService.createAccount(command)
        );

        assertEquals("Account already exists for document number: " + normalized.trim(), exception.getMessage());
        verify(accountRepository).existsByDocumentNumber(normalized.trim());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should return account balance when account exists")
    void shouldReturnBalance_whenAccountExists() {
        long accountId = 1L;
        Account account = new Account(accountId, "12345678900");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.getBalanceByAccountId(accountId)).thenReturn(new BigDecimal("23.45"));

        BigDecimal result = accountService.getBalance(accountId);

        assertEquals(new BigDecimal("23.45"), result);
        verify(accountRepository).findById(accountId);
        verify(transactionRepository).getBalanceByAccountId(accountId);
    }

    @Test
    @DisplayName("Should throw exception when account does not exist on balance retrieval")
    void shouldThrowException_whenBalanceAccountDoesNotExist() {
        long accountId = 999L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getBalance(accountId)
        );

        assertEquals("Account not found for id: 999", exception.getMessage());
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(transactionRepository);
    }
}