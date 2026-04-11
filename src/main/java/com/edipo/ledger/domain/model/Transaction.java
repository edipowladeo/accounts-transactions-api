package com.edipo.ledger.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Transaction(
        Long id,
        Long accountId,
        OperationType operationType,
        BigDecimal amount,
        OffsetDateTime eventDate
) { }