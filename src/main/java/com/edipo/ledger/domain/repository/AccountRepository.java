package com.edipo.ledger.domain.repository;

import com.edipo.ledger.domain.model.Account;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(Long id);
    boolean existsByDocumentNumber(String documentNumber);
}