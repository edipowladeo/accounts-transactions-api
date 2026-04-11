package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.request.CreateTransactionRequest;
import com.edipo.ledger.api.response.ApiErrorResponse;
import com.edipo.ledger.api.response.TransactionResponse;
import com.edipo.ledger.application.CreateTransactionCommand;
import com.edipo.ledger.application.service.TransactionService;
import com.edipo.ledger.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    "The transaction amount is normalized according to the operation type rules."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload — one or more fields failed validation " +
                            "(e.g. null account_id, null operation_type_id, null/zero/negative amount, " +
                            "malformed JSON, or empty body)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the given account_id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
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
                            schema = @Schema(implementation = CreateTransactionRequest.class)
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