package com.edipo.ledger.api.response;

import com.edipo.ledger.domain.model.Account;
import com.edipo.ledger.domain.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionResponse {

    @JsonProperty("transaction_id")
    private Long transactionId;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("operation_type_id")
    private Integer operationTypeId;
    private BigDecimal amount;
    @JsonProperty("event_date")
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

    public static TransactionResponse from(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(), //todo check nullability
                transaction.getOperationType().getId(),
                transaction.getAmount(),
                transaction.getEventDate()
        );
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