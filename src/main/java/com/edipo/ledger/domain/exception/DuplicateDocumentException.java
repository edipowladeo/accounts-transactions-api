package com.edipo.ledger.domain.exception;

public class DuplicateDocumentException extends RuntimeException {
    public DuplicateDocumentException(String documentNumber) {
        super("Document number already exists: " + documentNumber);
    }
}