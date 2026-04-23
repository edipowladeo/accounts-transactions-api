package com.edipo.ledger.api.controller;

import com.edipo.ledger.api.ApiExamples;
import com.edipo.ledger.api.response.ApiErrorResponse;
import com.edipo.ledger.api.response.BalanceResponse;
import com.edipo.ledger.application.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/balance")
@Tag(name = "Balance", description = "Endpoints for account balance")
public class BalanceController {

    private final AccountService accountService;

    public BalanceController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Get account balance",
            operationId = "getAccountBalance"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceResponse.class),
                            examples = @ExampleObject(
                                    name = "Balance response",
                                    value = ApiExamples.BALANCE_RESPONSE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid accountId parameter",
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
                    description = "Account not found",
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
    public ResponseEntity<BalanceResponse> getBalance(
            @Parameter(description = "Numeric ID of the account", example = "1")
            @RequestParam Long accountId
    ) {
        BigDecimal balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(new BalanceResponse(accountId, balance));
    }
}