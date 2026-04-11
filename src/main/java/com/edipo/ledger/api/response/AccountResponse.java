package com.edipo.ledger.api.response;

import com.edipo.ledger.domain.model.Account;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountResponse {

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("document_number")
    private String documentNumber;

    public AccountResponse() {
    }

    public AccountResponse(Long accountId, String documentNumber) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.id(), account.documentNumber());
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