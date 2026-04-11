package com.edipo.ledger.application;

import java.math.BigDecimal;

public record CreateTransactionCommand(
        Long accountId,
        int operationTypeId,
        BigDecimal amount
) { }