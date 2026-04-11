package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.response.ApiErrorResponse;
import com.edipo.ledger.application.service.AccountService;
import com.edipo.ledger.api.request.CreateAccountRequest;
import com.edipo.ledger.api.response.AccountResponse;
import com.edipo.ledger.application.CreateAccountCommand;
import com.edipo.ledger.domain.model.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a new account",
            operationId = "createAccount"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "Created account",
                                    value = """
                                            {
                                              "account_id": 1,
                                              "document_number": "12345678900"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request — document_number is blank or missing",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Blank document_number",
                                            value = """
                                                    {
                                                      "message": "document_number: must not be blank",
                                                      "code": "INVALID_REQUEST",
                                                      "timestamp": "2024-01-15T10:30:00Z"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing document_number",
                                            value = """
                                                    {
                                                      "message": "document_number: must not be blank",
                                                      "code": "INVALID_REQUEST",
                                                      "timestamp": "2024-01-15T10:30:00Z"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict — an account with this document number already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Duplicate document number",
                                    value = """
                                            {
                                              "message": "Account already exists for document number: 12345678900",
                                              "code": "DUPLICATE_DOCUMENT",
                                              "timestamp": "2024-01-15T10:30:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        var command = new CreateAccountCommand(request.getDocumentNumber());
        Account account = accountService.createAccount(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.from(account));
    }

    @Operation(
            summary = "Get account by ID",
            operationId = "getAccountById"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "Found account",
                                    value = """
                                            {
                                              "account_id": 1,
                                              "document_number": "12345678900"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameter — accountId is not a valid integer",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid accountId",
                                    value = """
                                            {
                                              "message": "Invalid value 'abc' for parameter 'accountId'",
                                              "code": "INVALID_PARAMETER",
                                              "timestamp": "2024-01-15T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the given ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Account not found",
                                    value = """
                                            {
                                              "message": "Account not found for id: 99",
                                              "code": "ACCOUNT_NOT_FOUND",
                                              "timestamp": "2024-01-15T10:30:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "Numeric ID of the account to retrieve", example = "1")
            @PathVariable Long accountId
    ) {
        Account account = accountService.getById(accountId);
        return ResponseEntity.ok(AccountResponse.from(account));
    }
}