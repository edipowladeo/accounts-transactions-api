package com.edipo.ledger.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class ApiErrorResponse {

    private final String code;

    private final String message;

    private final OffsetDateTime timestamp;

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}