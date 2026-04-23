package com.edipo.ledger.domain.repository;

import com.edipo.ledger.domain.model.Transaction;

import java.math.BigDecimal;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    BigDecimal getBalanceByAccountId(Long accountId);
}