package com.edipo.ledger.application;

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

        return transactionRepository.save(transaction);
    }

    private void validateCommand(CreateTransactionCommand command) {
   //todo
    }

    private BigDecimal normalizeAmount(BigDecimal originalAmount, OperationType operationType) {
   //todo
    }
}