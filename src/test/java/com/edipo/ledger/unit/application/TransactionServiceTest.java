package com.edipo.ledger.unit.application;

import com.edipo.ledger.application.CreateTransactionCommand;
import com.edipo.ledger.application.service.TransactionService;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.InvalidAmountException;
import com.edipo.ledger.domain.exception.InvalidOperationTypeException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.model.OperationType;
import com.edipo.ledger.domain.model.Transaction;
import com.edipo.ledger.domain.repository.AccountRepository;
import com.edipo.ledger.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when operation is credit and amount is positive, then creates transaction with positive amount")
    void whenOperationIsCreditAndAmountIsPositive_thenCreatesTransactionWithPositiveAmount() {
        Long accountId = 1L;
        Integer operationTypeId = OperationType.PAYMENT.getId();
        BigDecimal inputAmount = new BigDecimal("100.50");

        CreateTransactionCommand command =
                new CreateTransactionCommand(accountId, operationTypeId, inputAmount);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));

        Transaction savedTransaction = new Transaction(
                10L,
                accountId,
                OperationType.PAYMENT,
                new BigDecimal("100.50"),
                OffsetDateTime.now()
        );

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = transactionService.create(command);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(accountId, result.getAccountId());
        assertEquals(OperationType.PAYMENT, result.getOperationType());
        assertEquals(new BigDecimal("100.50"), result.getAmount());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction persisted = captor.getValue();
        assertNull(persisted.getId());
        assertEquals(accountId, persisted.getAccountId());
        assertEquals(OperationType.PAYMENT, persisted.getOperationType());
        assertEquals(new BigDecimal("100.50"), persisted.getAmount());
        assertNotNull(persisted.getEventDate());

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("when operation is debt and amount is positive, then creates transaction with negative amount")
    void whenOperationIsDebtAndAmountIsPositive_thenCreatesTransactionWithNegativeAmount() {
        Long accountId = 1L;
        Integer operationTypeId = OperationType.PURCHASE.getId();
        BigDecimal inputAmount = new BigDecimal("200.00");

        CreateTransactionCommand command =
                new CreateTransactionCommand(accountId, operationTypeId, inputAmount);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));

        Transaction savedTransaction = new Transaction(
                11L,
                accountId,
                OperationType.PURCHASE,
                new BigDecimal("-200.00"),
                OffsetDateTime.now()
        );

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = transactionService.create(command);

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertEquals(accountId, result.getAccountId());
        assertEquals(OperationType.PURCHASE, result.getOperationType());
        assertEquals(new BigDecimal("-200.00"), result.getAmount());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction persisted = captor.getValue();
        assertNull(persisted.getId());
        assertEquals(accountId, persisted.getAccountId());
        assertEquals(OperationType.PURCHASE, persisted.getOperationType());
        assertEquals(new BigDecimal("-200.00"), persisted.getAmount());
        assertNotNull(persisted.getEventDate());

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("when account id is null, then throws exception")
    void whenAccountIdIsNull_thenThrowsException() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(null, OperationType.PAYMENT.getId(), new BigDecimal("10.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Account id must be a positive number", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when account id is zero, then throws exception")
    void whenAccountIdIsZero_thenThrowsException() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(0L, OperationType.PAYMENT.getId(), new BigDecimal("10.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Account id must be a positive number", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when account id is negative, then throws exception")
    void whenAccountIdIsNegative_thenThrowsException() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(-1L, OperationType.PAYMENT.getId(), new BigDecimal("10.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Account id must be a positive number", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when amount is null, then throws exception")
    void whenAmountIsNull_thenThrowsException() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(1L, OperationType.PAYMENT.getId(), null);

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Amount must not be null", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when amount is zero, then throws exception")
    void whenAmountIsZero_thenThrowsException() {
        CreateTransactionCommand command =
                new CreateTransactionCommand(1L, OperationType.PAYMENT.getId(), BigDecimal.ZERO);

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("when account does not exist, then throws exception")
    void whenAccountDoesNotExist_thenThrowsException() {
        Long accountId = 99L;

        CreateTransactionCommand command =
                new CreateTransactionCommand(accountId, OperationType.PAYMENT.getId(), new BigDecimal("10.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.create(command)
        );

        assertNotNull(exception);
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    @DisplayName("when operation type id is invalid, then throws exception")
    void whenOperationTypeIdIsInvalid_thenThrowsException() {
        Long accountId = 1L;
        Integer invalidOperationTypeId = 999;

        CreateTransactionCommand command =
                new CreateTransactionCommand(accountId, invalidOperationTypeId, new BigDecimal("10.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));

        assertThrows(
                InvalidOperationTypeException.class,
                () -> transactionService.create(command)
        );

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(transactionRepository);
    }

    @ParameterizedTest
    @EnumSource(OperationType.class)
    @DisplayName("when amount is negative for any operation type, then throws exception")
    void whenAmountIsNegativeForAnyOperationType_thenThrowsException(OperationType operationType) {
        CreateTransactionCommand command =
                new CreateTransactionCommand(1L, operationType.getId(), new BigDecimal("-100.00"));

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> transactionService.create(command)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());

        verifyNoInteractions(accountRepository, transactionRepository);
    }
}