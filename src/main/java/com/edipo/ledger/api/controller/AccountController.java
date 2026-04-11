package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.ApiExamples;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
                                    value = ApiExamples.ACCOUNT_RESPONSE
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
                                            value = ApiExamples.INVALID_REQUEST_BLANK_DOCUMENT
                                    ),
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
                                    value = ApiExamples.DUPLICATE_DOCUMENT
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody(
                    description = "Account creation payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateAccountRequest.class),
                            examples = @ExampleObject(
                                    name = "Create account",
                                    value = ApiExamples.CREATE_ACCOUNT_REQUEST
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateAccountRequest request
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
                                    value = ApiExamples.ACCOUNT_RESPONSE
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
                                    value = ApiExamples.INVALID_PARAMETER_ACCOUNT_ID
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
                                    value = ApiExamples.ACCOUNT_NOT_FOUND
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(
                    description = "Numeric ID of the account",
                    example = "1"
            )
            @RequestParam Long accountId
    ) {
        Account account = accountService.getById(accountId);
        return ResponseEntity.ok(AccountResponse.from(account));
    }
}