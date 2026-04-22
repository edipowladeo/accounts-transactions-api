package com.edipo.ledger.api;

public final class ApiExamples {

    private ApiExamples() {}

    public static final String CREATE_ACCOUNT_REQUEST = """
            {
              "document_number": "12345678900"
            }
            """;

    public static final String ACCOUNT_RESPONSE = """
            {
              "account_id": 1,
              "document_number": "12345678900"
            }
            """;

    public static final String DUPLICATE_DOCUMENT = """
            {
              "message": "Account already exists for document number: 12345678900",
              "code": "DUPLICATE_DOCUMENT",
              "timestamp": "2024-01-15T10:30:00Z"
            }
            """;

    public static final String ACCOUNT_NOT_FOUND = """
            {
              "message": "Account not found for id: 99",
              "code": "ACCOUNT_NOT_FOUND",
              "timestamp": "2024-01-15T10:30:00Z"
            }
            """;

    public static final String INVALID_REQUEST_BLANK_DOCUMENT = """
            {
              "message": "document_number: must not be blank",
              "code": "INVALID_REQUEST",
              "timestamp": "2024-01-15T10:30:00Z"
            }
            """;

    public static final String INVALID_PARAMETER_ACCOUNT_ID = """
            {
              "message": "Invalid value 'abc' for parameter 'accountId'",
              "code": "INVALID_PARAMETER",
              "timestamp": "2024-01-15T10:30:00Z"
            }
            """;

    public static final String BALANCE_RESPONSE = """
            {
              "account_id": 1,
              "balance": 23.45
            }
            """;

    public static final String CREATE_TRANSACTION_REQUEST =
            """
            {
              "account_id": 1,
              "operation_type_id": 4,
              "amount": 123.45
            }
            """;

    public static final String TRANSACTION_RESPONSE =
            """
            {
              "transaction_id": 10,
              "account_id": 1,
              "operation_type_id": 4,
              "amount": 123.45,
              "created_at": "2026-04-10T10:15:30Z"
            }
            """;

    public static final String ERROR_INVALID_REQUEST =
            """
            {
              "code": "INVALID_REQUEST",
              "message": "amount: must be greater than 0",
              "timestamp": "2026-04-10T10:15:30Z"
            }
            """;

    public static final String ERROR_ACCOUNT_NOT_FOUND =
            """
            {
              "code": "ACCOUNT_NOT_FOUND",
              "message": "Account not found for id: 99",
              "timestamp": "2026-04-10T10:15:30Z"
            }
            """;
}