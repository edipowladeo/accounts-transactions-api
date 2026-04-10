package com.edipo.ledger.infrastructure.persistence.jdbc;

import com.edipo.ledger.domain.model.Transaction;
import com.edipo.ledger.domain.repository.TransactionRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionJdbcRepository implements TransactionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TransactionJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions (
                    account_id,
                    operation_type_id,
                    amount,
                    event_date
                )
                VALUES (
                    :accountId,
                    :operationTypeId,
                    :amount,
                    :eventDate
                )
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("accountId", transaction.getAccountId())
                .addValue("operationTypeId", transaction.getOperationType().getId())
                .addValue("amount", transaction.getAmount())
                .addValue("eventDate", transaction.getEventDate());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"transaction_id"});

        Number key = keyHolder.getKey();
        Long generatedId = key != null ? key.longValue() : null;

        return new Transaction(
                generatedId,
                transaction.getAccountId(),
                transaction.getOperationType(),
                transaction.getAmount(),
                transaction.getEventDate()
        );
    }
}