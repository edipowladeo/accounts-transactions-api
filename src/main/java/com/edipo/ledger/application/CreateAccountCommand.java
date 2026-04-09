package com.edipo.ledger.application;

public class CreateAccountCommand {
    private final String documentNumber;

    public CreateAccountCommand(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}