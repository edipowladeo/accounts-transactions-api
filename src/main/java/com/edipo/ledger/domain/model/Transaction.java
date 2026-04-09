package com.edipo.ledger.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Transaction {
    private Long id;
    private Long accountId;
    private OperationType operationType;
    private BigDecimal amount;
    private OffsetDateTime eventDate;

    public Transaction(
            Long id,
            Long accountId,
            OperationType operationType,
            BigDecimal amount,
            OffsetDateTime eventDate
    ) {
        this.id = id;
        this.accountId = accountId;
        this.operationType = operationType;
        this.amount = amount;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OffsetDateTime getEventDate() {
        return eventDate;
    }
}