package com.edipo.ledger.application;

import java.math.BigDecimal;

public record CreateAccountCommand(
        String documentNumber,
        BigDecimal availableCreditLimit
) { }