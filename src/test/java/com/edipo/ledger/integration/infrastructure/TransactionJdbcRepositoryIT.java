package com.edipo.ledger.integration.infrastructure;

import com.edipo.ledger.domain.model.OperationType;
import com.edipo.ledger.domain.model.Transaction;
import com.edipo.ledger.infrastructure.persistence.jdbc.TransactionJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class TransactionJdbcRepositoryIT {

    @Autowired
    private TransactionJdbcRepository transactionJdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from transactions");
        jdbcTemplate.update("delete from accounts");
    }

    @Test
    @DisplayName("should save transaction and return it with generated id")
    void shouldSaveTransactionAndReturnItWithGeneratedId() {
        Long accountId = insertAccount("12345678900");
        OffsetDateTime eventDate = OffsetDateTime.now().withNano(0);

        Transaction transaction = new Transaction(
                null,
                accountId,
                OperationType.PAYMENT,
                new BigDecimal("150.75"),
                eventDate
        );

        Transaction saved = transactionJdbcRepository.save(transaction);

        assertNotNull(saved);
        assertNotNull(saved.id());
        assertEquals(accountId, saved.accountId());
        assertEquals(OperationType.PAYMENT, saved.operationType());
        assertEquals(new BigDecimal("150.75"), saved.amount());
        assertNotNull(saved.eventDate());
    }

    @Test
    @DisplayName("should persist negative amount transaction correctly")
    void shouldPersistNegativeAmountTransactionCorrectly() {
        Long accountId = insertAccount("98765432100");
        OffsetDateTime eventDate = OffsetDateTime.now().withNano(0);

        Transaction transaction = new Transaction(
                null,
                accountId,
                OperationType.WITHDRAWAL,
                new BigDecimal("-50.00"),
                eventDate
        );

        Transaction saved = transactionJdbcRepository.save(transaction);

        BigDecimal persistedAmount = jdbcTemplate.queryForObject(
                "select amount from transactions where transaction_id = ?",
                BigDecimal.class,
                saved.id()
        );

        Integer persistedOperationTypeId = jdbcTemplate.queryForObject(
                "select operation_type_id from transactions where transaction_id = ?",
                Integer.class,
                saved.id()
        );

        assertEquals(new BigDecimal("-50.00"), persistedAmount);
        assertEquals(OperationType.WITHDRAWAL.getId(), persistedOperationTypeId);
    }

    @Test
    @DisplayName("should persist event date correctly")
    void shouldPersistEventDateCorrectly() {
        Long accountId = insertAccount("11122233344");
        OffsetDateTime eventDate = OffsetDateTime.of(
                2026, 4, 10, 12, 30, 45, 0, ZoneOffset.UTC
        );

        Transaction transaction = new Transaction(
                null,
                accountId,
                OperationType.PAYMENT,
                new BigDecimal("200.00"),
                eventDate
        );

        Transaction saved = transactionJdbcRepository.save(transaction);

        OffsetDateTime persistedTimestamp = jdbcTemplate.queryForObject(
                "select event_date from transactions where transaction_id = ?",
                (rs, rowNum) -> rs.getObject(1, OffsetDateTime.class),
                saved.id()
        );

        assertNotNull(persistedTimestamp);
        assertEquals(eventDate.toLocalDateTime(), persistedTimestamp.toLocalDateTime());
    }

    @Test
    @DisplayName("should store operation type id mapped from enum")
    void shouldStoreOperationTypeIdMappedFromEnum() {
        Long accountId = insertAccount("55566677788");

        Transaction transaction = new Transaction(
                null,
                accountId,
                OperationType.INSTALLMENT_PURCHASE,
                new BigDecimal("-300.00"),
                OffsetDateTime.now().withNano(0)
        );

        Transaction saved = transactionJdbcRepository.save(transaction);

        Integer operationTypeId = jdbcTemplate.queryForObject(
                "select operation_type_id from transactions where transaction_id = ?",
                Integer.class,
                saved.id()
        );

        assertEquals(OperationType.INSTALLMENT_PURCHASE.getId(), operationTypeId);
    }

    private Long insertAccount(String documentNumber) {
        jdbcTemplate.update(
                "insert into accounts (document_number) values (?)",
                documentNumber
        );

        return jdbcTemplate.queryForObject(
                "select account_id from accounts where document_number = ?",
                Long.class,
                documentNumber
        );
    }
}