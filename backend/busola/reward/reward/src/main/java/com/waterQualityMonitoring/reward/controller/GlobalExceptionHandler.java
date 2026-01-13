package com.waterQualityMonitoring.reward.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.waterQualityMonitoring.reward.service.exception.CrowdsourcedServiceUnavailableException;
import com.waterQualityMonitoring.reward.service.exception.ResourceNotFoundException;

/**
 * Centralised exception handler for the Rewards API to keep error responses
 * consistent.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation failures triggered by bean validation annotations.
     *
     * @param ex thrown validation exception
     * @return 400 response with validation feedback
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", ex.getMessage());
    }

    /**
     * Handles illegal argument scenarios arising from bad client input.
     *
     * @param ex thrown exception
     * @return 400 response describing the issue
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
    }

    /**
     * Handles scenarios where the crowdsourced service cannot be reached.
     *
     * @param ex propagated exception
     * @return 503 response indicating downstream service issues
     */
    @ExceptionHandler(CrowdsourcedServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleCrowdsourcedUnavailable(CrowdsourcedServiceUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Crowdsourced service unavailable", ex.getMessage());
    }

    /**
     * Handles not found scenarios.
     *
     * @param ex propagated exception
     * @return 404 response with descriptive message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    /**
     * Fallback handler for unexpected exceptions.
     *
     * @param ex thrown exception
     * @return 500 response with a generic error description
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String details) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "details", details);
        return ResponseEntity.status(status).body(body);
    }
}

