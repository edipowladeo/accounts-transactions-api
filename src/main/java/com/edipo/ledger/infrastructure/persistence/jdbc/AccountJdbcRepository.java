package com.edipo.ledger.infrastructure.persistence.jdbc;

import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.repository.AccountRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
                INSERT INTO accounts (document_number)
                VALUES (:documentNumber)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("documentNumber", account.getDocumentNumber());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"account_id"});

        Number key = keyHolder.getKey();
        Long generatedId = key != null ? key.longValue() : null;

        return new Account(generatedId, account.getDocumentNumber());
    }

    @Override
    public Optional<Account> findById(Long id) {
        String sql = """
                SELECT account_id, document_number
                FROM accounts
                WHERE account_id = :accountId
                """;

        var results = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource("accountId", id),
                (rs, rowNum) -> new Account(
                        rs.getLong("account_id"),
                        rs.getString("document_number")
                )
        );

        return results.stream().findFirst();
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        String sql = """
                SELECT COUNT(1)
                FROM accounts
                WHERE document_number = :documentNumber
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("documentNumber", documentNumber),
                Integer.class
        );

        return count != null && count > 0;
    }
}