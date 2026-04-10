package com.edipo.ledger.integration.infrastructure;

import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.infrastructure.persistence.jdbc.AccountJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // todo check if needed
class AccountJdbcRepositoryIT {

    @Autowired
    private AccountJdbcRepository accountJdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from transactions");
        jdbcTemplate.update("delete from accounts");
    }

    @Test
    @DisplayName("should save account and return it with generated id")
    void shouldSaveAccountAndReturnItWithGeneratedId() {
        Account account = new Account(null, "12345678900");

        Account saved = accountJdbcRepository.save(account);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("12345678900", saved.getDocumentNumber());
    }

    @Test
    @DisplayName("should find account by id")
    void shouldFindAccountById() {
        jdbcTemplate.update(
                "insert into accounts (document_number) values (?)",
                "11122233344"
        );

        Long accountId = jdbcTemplate.queryForObject(
                "select account_id from accounts where document_number = ?",
                Long.class,
                "11122233344"
        );

        Optional<Account> result = accountJdbcRepository.findById(accountId);

        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
        assertEquals("11122233344", result.get().getDocumentNumber());
    }

    @Test
    @DisplayName("should return empty when account id does not exist")
    void shouldReturnEmptyWhenAccountIdDoesNotExist() {
        Optional<Account> result = accountJdbcRepository.findById(999999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return true when document number exists")
    void shouldReturnTrueWhenDocumentNumberExists() {
        jdbcTemplate.update(
                "insert into accounts (document_number) values (?)",
                "55566677788"
        );

        boolean exists = accountJdbcRepository.existsByDocumentNumber("55566677788");

        assertTrue(exists);
    }

    @Test
    @DisplayName("should return false when document number does not exist")
    void shouldReturnFalseWhenDocumentNumberDoesNotExist() {
        boolean exists = accountJdbcRepository.existsByDocumentNumber("00000000000");

        assertFalse(exists);
    }
}