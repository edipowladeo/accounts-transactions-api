package com.edipo.ledger.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Payload for creating a transaction")
public class CreateTransactionRequest {

    @NotNull
    @Schema(description = "Account identifier", example = "1")
    @JsonProperty("account_id")
    private Long accountId;

    @NotNull
    @Schema(description = "Operation type identifier", example = "4")
    @JsonProperty("operation_type_id")
    private Integer operationTypeId;

    @NotNull
    @Positive
    @Schema(description = "Transaction amount, must be positive", example = "100.50")
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