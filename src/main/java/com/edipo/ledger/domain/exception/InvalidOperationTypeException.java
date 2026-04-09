package com.edipo.ledger.domain.exception;

public class InvalidOperationTypeException extends RuntimeException {
    public InvalidOperationTypeException(Integer operationTypeId) {
        super("Invalid operation type id: " + operationTypeId);
    }
}