package com.edipo.ledger.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotNull
    @JsonProperty("account_id")
    private Long accountId;

    @NotNull
    @JsonProperty("operation_type_id")
    private Integer operationTypeId;

    @NotNull
    @DecimalMin(value = "0.01")
    @Positive
    private BigDecimal amount;

    public Long getAccountId() {
        return accountId;
    }

    public Integer getOperationTypeId() {
        return operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}