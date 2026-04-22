package com.edipo.ledger.infrastructure.persistence.jdbc;

import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountJdbcRepository implements AccountRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AccountJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account save(Account account) {
        String sql = """
            INSERT INTO accounts (document_number, available_credit_limit)
            VALUES (:documentNumber, :availableCreditLimit)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("documentNumber", account.documentNumber())
                .addValue("availableCreditLimit", account.availableCreditLimit());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"account_id"});

        Long generatedId = Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve generated account ID"));

        return new Account(generatedId, account.documentNumber(), account.availableCreditLimit());
    }

    @Override
    public Account update(Account account) {
        String sql = """
        UPDATE accounts
        SET document_number = :documentNumber,
            available_credit_limit = :availableCreditLimit
        WHERE account_id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", account.id())
                .addValue("documentNumber", account.documentNumber())
                .addValue("availableCreditLimit", account.availableCreditLimit());

        jdbcTemplate.update(sql, params);

        return account;
    }



    @Override
    public Optional<Account> findById(Long id) {
        String sql = """
                SELECT account_id, document_number, available_credit_limit
                FROM accounts
                WHERE account_id = :accountId
                """;

        var results = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("accountId", id),
                (rs, rowNum) -> new Account(
                        rs.getLong("account_id"),
                        rs.getString("document_number"),
                        rs.getBigDecimal("available_credit_limit")
                )
        );

        return results.stream().findFirst();
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM accounts
                    WHERE document_number = :documentNumber
                )
                """;

        Boolean exists = jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("documentNumber", documentNumber),
                Boolean.class
        );

        return exists;
    }
}