package com.edipo.ledger.application.service;

import com.edipo.ledger.application.CreateTransactionCommand;
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

        if (accountRepository.findById(command.getAccountId()).isEmpty()) {
            throw new AccountNotFoundException(command.getAccountId());
        }

        OperationType operationType = OperationType.fromId(command.getOperationTypeId());
        BigDecimal normalizedAmount = normalizeAmount(command.getAmount(), operationType);

        Transaction transaction = new Transaction(
                null,
                command.getAccountId(),
                operationType,
                normalizedAmount,
                OffsetDateTime.now()
        );

        Transaction savedTransaction =  transactionRepository.save(transaction);

        return savedTransaction;
       /* return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getAccountId(),
                savedTransaction.getOperationType().getId(),
                savedTransaction.getAmount(),
                savedTransaction.getEventDate()
        )*/
    }

    private void validateCommand(CreateTransactionCommand command) {
        if (command.getAccountId() == null || command.getAccountId() <= 0) {
            throw new IllegalArgumentException("Account id must be a positive number");
        }

        if (command.getAmount() == null) {
            throw new InvalidAmountException("Amount must not be null");
        }

        if (command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
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