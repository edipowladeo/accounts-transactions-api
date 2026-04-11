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
                .addValue("accountId", transaction.accountId())
                .addValue("operationTypeId", transaction.operationType().getId())
                .addValue("amount", transaction.amount())
                .addValue("eventDate", transaction.eventDate());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"transaction_id"});

        Number key = keyHolder.getKey();
        Long generatedId = key != null ? key.longValue() : null;

        return new Transaction(
                generatedId,
                transaction.accountId(),
                transaction.operationType(),
                transaction.amount(),
                transaction.eventDate()
        );
    }
}