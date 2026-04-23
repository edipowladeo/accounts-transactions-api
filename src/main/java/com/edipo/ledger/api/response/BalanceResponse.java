package com.edipo.ledger.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Account balance response")
public class BalanceResponse {

    @JsonProperty("account_id")
    @Schema(description = "Account identifier", example = "1")
    private Long accountId;

    @Schema(description = "Current account balance", example = "23.45")
    private BigDecimal balance;

    public BalanceResponse() {
    }

    public BalanceResponse(Long accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}