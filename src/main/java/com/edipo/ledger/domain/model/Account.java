package com.edipo.ledger.domain.model;

public class Account {
    private Long id;
    private String documentNumber;

    public Account(Long id, String documentNumber) {
        this.id = id;
        this.documentNumber = documentNumber;
    }

    public Long getId() {
        return id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}