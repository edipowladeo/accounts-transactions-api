package com.edipo.ledger.api.response;

import com.edipo.ledger.domain.model.Account;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountResponse {

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("available_credit_limit")
    private BigDecimal availableCreditLimit;

    public AccountResponse() {
    }

    public AccountResponse(Long accountId, String documentNumber, BigDecimal availableCreditLimit) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
        this.availableCreditLimit = availableCreditLimit;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.id(), account.documentNumber(), account.availableCreditLimit());
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}