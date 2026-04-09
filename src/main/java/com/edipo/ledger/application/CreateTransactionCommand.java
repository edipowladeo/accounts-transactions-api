package com.edipo.ledger.application;

import java.math.BigDecimal;

public class CreateTransactionCommand {
    private final Long accountId;
    private final int operationTypeId;
    private final BigDecimal amount;

    public CreateTransactionCommand(Long accountId, int operationTypeId, BigDecimal amount) {
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public int getOperationTypeId() {
        return operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}