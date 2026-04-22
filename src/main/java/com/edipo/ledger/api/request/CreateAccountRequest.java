package com.edipo.ledger.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateAccountRequest {

    @NotBlank
    @JsonProperty("document_number")
    private String documentNumber;

    @NotNull
    @JsonProperty("available_credit_limit")
    private BigDecimal availableCreditLimit;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public BigDecimal getAvailableCreditLimit() {
        return availableCreditLimit;
    }
}