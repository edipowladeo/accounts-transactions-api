package com.edipo.ledger.application;

import com.edipo.ledger.domain.exception.InvalidAmountException;
import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.repository.AccountRepository;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(CreateAccountCommand command) {
        String normalizedDocument = normalizeDocument(command.documentNumber());

        if (command.availableCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Available credit limit cannot be negative");
        }

        if (accountRepository.existsByDocumentNumber(normalizedDocument)) {
            throw new DuplicateDocumentException(normalizedDocument);
        }

        Account account = new Account(null, normalizedDocument, command.availableCreditLimit());
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getById(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("Account id must be a positive number");
        }

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private String normalizeDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("Document cannot be null or empty");
        }

        String stripped = documentNumber.replaceAll("[.\\-/]", "");

        if (!stripped.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Document number must contain only digits. Formatting characters (. - /) are accepted but other characters are not.");
        }
        if (stripped.isBlank()) {
            throw new IllegalArgumentException("Document cannot be null or empty");
        }


        return stripped;
    }
}