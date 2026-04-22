package com.edipo.ledger.application;

import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.InvalidAmountException;
import com.edipo.ledger.domain.model.OperationType;
import com.edipo.ledger.domain.model.Transaction;
import com.edipo.ledger.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction create(CreateTransactionCommand command) {
        validateCommand(command);

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        OperationType operationType = OperationType.fromId(command.operationTypeId());
        BigDecimal normalizedAmount = normalizeAmount(command.amount(), operationType);

        Account newAccount = new Account(
                account.id(),
                account.documentNumber(),
                account.availableCreditLimit().add(normalizedAmount)
        );

        if (newAccount.availableCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Available credit limit cannot be negative");
        }

        Transaction transaction = new Transaction(
                null,
                command.accountId(),
                operationType,
                normalizedAmount,
                OffsetDateTime.now()
        );

        accountRepository.update(newAccount);
        return transactionRepository.save(transaction);
    }

    private void validateCommand(CreateTransactionCommand command) {
        if (command.accountId() == null || command.accountId() <= 0) {
            throw new IllegalArgumentException("Account id must be a positive number");
        }

        if (command.amount() == null) {
            throw new InvalidAmountException("Amount must not be null");
        }

        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        if (command.amount().scale() > 2) {
            throw new InvalidAmountException("Amount must not have more than 2 decimal places");
        }
    }

    private BigDecimal normalizeAmount(BigDecimal originalAmount, OperationType operationType) {
        BigDecimal absoluteAmount = originalAmount.abs();

        if (operationType.isDebt()) {
            return absoluteAmount.negate();
        }

        return absoluteAmount;
    }
}