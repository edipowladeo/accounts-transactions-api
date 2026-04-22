package com.edipo.ledger.domain.model;

import java.math.BigDecimal;

public record Account(
        Long id,
        String documentNumber,
        BigDecimal availableCreditLimit
) { }