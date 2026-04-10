package com.edipo.ledger.api.response;

import com.edipo.ledger.domain.model.Account;

public class AccountResponse {

    private Long accountId;
    private String documentNumber;

    public AccountResponse() {
    }

    public AccountResponse(Long accountId, String documentNumber) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getDocumentNumber());
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