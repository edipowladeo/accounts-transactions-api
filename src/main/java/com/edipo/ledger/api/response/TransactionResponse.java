package com.edipo.ledger.api.response;

import com.edipo.ledger.domain.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Transaction response")
public class TransactionResponse {

    @JsonProperty("transaction_id")
    @Schema(description = "Transaction identifier", example = "10")
    private Long transactionId;

    @JsonProperty("account_id")
    @Schema(description = "Account identifier", example = "1")
    private Long accountId;

    @JsonProperty("operation_type_id")
    @Schema(description = "Operation type identifier", example = "4")
    private Integer operationTypeId;

    @Schema(description = "Transaction amount", example = "-100.50")
    private BigDecimal amount;

    @JsonProperty("created_at")
    @Schema(description = "Transaction event date", example = "2026-04-10T12:30:00Z")
    private OffsetDateTime createdAt;

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
        this.createdAt = eventDate;
    }

    public static TransactionResponse from(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return new TransactionResponse(
                transaction.id(),
                transaction.accountId(),
                transaction.operationType().getId(),
                transaction.amount(),
                transaction.eventDate()
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}