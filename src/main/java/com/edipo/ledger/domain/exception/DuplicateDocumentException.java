package com.edipo.ledger.domain.exception;

public class DuplicateDocumentException extends RuntimeException {
    public DuplicateDocumentException(String documentNumber) {
        super("Account already exists for document number: " + documentNumber);
    }
}