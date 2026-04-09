package com.edipo.ledger.domain.exception;

public class InvalidOperationTypeException extends RuntimeException {
    public InvalidOperationTypeException(String message) {
        super(message);
    }
}