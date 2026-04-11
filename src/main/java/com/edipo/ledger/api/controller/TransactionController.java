package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.ApiExamples;
import com.edipo.ledger.api.request.CreateTransactionRequest;
import com.edipo.ledger.api.response.ApiErrorResponse;
import com.edipo.ledger.api.response.TransactionResponse;
import com.edipo.ledger.application.CreateTransactionCommand;
import com.edipo.ledger.application.service.TransactionService;
import com.edipo.ledger.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Endpoints for transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Create a transaction",
            description = "Creates a new transaction for an existing account. " +
                    "The transaction amount is normalised according to the operation type rules."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    name = "Created transaction",
                                    value = ApiExamples.TRANSACTION_RESPONSE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload — one or more fields failed validation " +
                            "(null or invalid account_id, operation_type_id, or amount — " +
                            "amount must be positive with up to 2 decimal places, e.g. 12.12)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = ApiExamples.ERROR_INVALID_REQUEST
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the given account_id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Account not found",
                                    value = ApiExamples.ERROR_ACCOUNT_NOT_FOUND
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid transaction data (business rule violation)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody(
                    description = "Transaction creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateTransactionRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Payment transaction",
                                            value = ApiExamples.CREATE_TRANSACTION_REQUEST
                                    ),
                                    @ExampleObject(
                                            name = "Null account_id (400)",
                                            value = ApiExamples.CREATE_TRANSACTION_REQUEST_NULL_ACCOUNT
                                    ),
                                    @ExampleObject(
                                            name = "Null operation_type_id (400)",
                                            value = ApiExamples.CREATE_TRANSACTION_REQUEST_NULL_OPERATION
                                    ),
                                    @ExampleObject(
                                            name = "Negative amount (400)",
                                            value = ApiExamples.CREATE_TRANSACTION_REQUEST_NEGATIVE_AMOUNT
                                    )
                            }
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateTransactionRequest request
    ) {
        var command = new CreateTransactionCommand(
                request.getAccountId(),
                request.getOperationTypeId(),
                request.getAmount()
        );

        Transaction response = transactionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(response));
    }
}