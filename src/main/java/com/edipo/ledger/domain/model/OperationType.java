package com.edipo.ledger.domain.model;

import com.edipo.ledger.domain.exception.InvalidOperationTypeException;

public enum OperationType {
    PURCHASE(1, true),
    INSTALLMENT_PURCHASE(2, true),
    WITHDRAWAL(3, true),
    PAYMENT(4, false);

    private final int id;
    private final boolean debt;

    OperationType(int id, boolean debt) {
        this.id = id;
        this.debt = debt;
    }

    public int getId() {
        return id;
    }

    public boolean isDebt() {
        return debt;
    }

    public static OperationType fromId(int id) {
        for (OperationType value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new InvalidOperationTypeException("Invalid operation type id: " + id);
    }
}