package com.edipo.ledger.domain.repository;

import com.edipo.ledger.domain.model.Transaction;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
}