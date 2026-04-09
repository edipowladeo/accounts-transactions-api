package com.edipo.ledger.api.controller;

import com.edipo.ledger.application.AccountService;
import com.edipo.ledger.api.request.CreateAccountRequest;
import com.edipo.ledger.api.response.AccountResponse;
import com.edipo.ledger.application.CreateAccountCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        var command = new CreateAccountCommand(
                request.getDocumentNumber()
        );

        AccountResponse response = accountService.createAccount(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        //201 Created when inserted
        //todo 200 ok account already exists with equivalent data
        //todo 409 Conflict when the account exists but the request conflicts with existing data
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId
    ) {
        AccountResponse response = accountService.getAccountById(accountId);
        return ResponseEntity.ok(response);
    }
}