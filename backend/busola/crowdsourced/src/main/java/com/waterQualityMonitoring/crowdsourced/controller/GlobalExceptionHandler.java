package com.waterQualityMonitoring.crowdsourced.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.waterQualityMonitoring.crowdsourced.dto.ApiError;

import jakarta.persistence.EntityNotFoundException;

/**
 * Centralised exception handler for the Crowdsourced API.
 * <p>
 * Converts common exceptions into structured {@link ApiError} responses so that
 * consumers receive consistent error payloads.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where requested entities could not be found in the database.
     *
     * @param ex exception thrown by the persistence layer
     * @return 404 response containing the error detail
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    /**
     * Handles validation exceptions produced by bean validation annotations.
     *
     * @param ex validation exception containing binding errors
     * @return 400 response describing the first discovered validation failure
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElseGet(() -> ex.getBindingResult().getGlobalErrors().stream()
                        .findFirst()
                        .map(error -> error.getDefaultMessage())
                        .orElse("Validation failed"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(message));
    }

    /**
     * Handles invalid arguments passed into service methods.
     *
     * @param ex thrown exception
     * @return 400 response with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }

    /**
     * Fallback handler for uncaught exceptions.
     *
     * @param ex thrown exception
     * @return 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Unexpected error occurred"));
    }
}

