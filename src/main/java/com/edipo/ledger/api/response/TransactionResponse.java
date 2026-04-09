package com.edipo.ledger.api.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionResponse {

    private Long transactionId;
    private Long accountId;
    private Integer operationTypeId;
    private BigDecimal amount;
    private OffsetDateTime eventDate;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long transactionId,
            Long accountId,
            Integer operationTypeId,
            BigDecimal amount,
            OffsetDateTime eventDate
    ) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.amount = amount;
        this.eventDate = eventDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Integer operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OffsetDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(OffsetDateTime eventDate) {
        this.eventDate = eventDate;
    }
}