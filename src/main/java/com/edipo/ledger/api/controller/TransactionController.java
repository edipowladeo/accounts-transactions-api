package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.request.CreateTransactionRequest;
import com.edipo.ledger.api.response.TransactionResponse;
import com.edipo.ledger.application.CreateTransactionCommand;
import com.edipo.ledger.application.TransactionService;
import com.edipo.ledger.domain.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
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