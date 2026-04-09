package com.edipo.ledger.domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("Account not found: " + accountId);
    }
}