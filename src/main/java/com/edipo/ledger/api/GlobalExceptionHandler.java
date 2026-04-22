package com.edipo.ledger.api;

import com.edipo.ledger.api.response.ApiErrorResponse;
import com.edipo.ledger.domain.exception.AccountNotFoundException;
import com.edipo.ledger.domain.exception.DuplicateDocumentException;
import com.edipo.ledger.domain.exception.InvalidAmountException;
import com.edipo.ledger.domain.exception.InvalidOperationTypeException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ACCOUNT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("INVALID_AMOUNT", ex.getMessage()));
    }

    @ExceptionHandler(InvalidOperationTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOperationType(InvalidOperationTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("INVALID_OPERATION_TYPE", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateDocument(DuplicateDocumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("DUPLICATE_DOCUMENT", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Class<?> targetClass = ex.getBindingResult().getTarget() != null
                ? ex.getBindingResult().getTarget().getClass()
                : null;

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fieldName = error.getField();
                    if (targetClass != null) {
                        try {
                            Field field = targetClass.getDeclaredField(fieldName);
                            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                            if (jsonProperty != null) {
                                fieldName = jsonProperty.value();
                            }
                        } catch (NoSuchFieldException ignored) {}
                    }
                    return fieldName + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("INVALID_REQUEST", message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex
    ) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName()
        );

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse("INVALID_PARAMETER", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("INTERNAL_ERROR", "Unexpected internal error"));
    }
}