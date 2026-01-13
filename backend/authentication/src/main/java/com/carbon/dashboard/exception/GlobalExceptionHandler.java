package com.carbon.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Provides consistent error responses across all controllers.
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles cases where the request body cannot be parsed (missing or invalid JSON).
     * 
     * @param ex the HttpMessageNotReadableException
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid request body");
        response.put("message", "The request body could not be parsed. Please check your JSON format.");
        response.put("hint", "Make sure: 1) Content-Type header is set to 'application/json' (exactly, no extra spaces), 2) Body is set to 'raw' and 'JSON' in Postman, 3) JSON is valid");
        response.put("details", ex.getMessage());
        response.put("commonIssues", Map.of(
            "1", "Content-Type must be exactly 'application/json' (check for typos or extra spaces)",
            "2", "In Postman Body tab, make sure 'raw' is selected AND 'JSON' is selected from dropdown",
            "3", "JSON must be valid - check for missing quotes, commas, or brackets",
            "4", "Make sure there are no invisible characters in your JSON"
        ));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handles validation errors from @Valid annotations.
     * Returns a user-friendly error message with field-specific errors.
     * 
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> {
                        String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
                        // Add rejected value info if available
                        Object rejectedValue = error.getRejectedValue();
                        if (rejectedValue == null) {
                            message += " (field is null - request body may not be parsed correctly)";
                        }
                        return message;
                    },
                    (existing, replacement) -> existing
                ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("message", "Please check the request body and ensure all required fields are provided");
        response.put("errors", errors);
        response.put("hint", "CRITICAL: Make sure Content-Type header is set to 'application/json' in Postman Headers tab");
        response.put("postmanSteps", Map.of(
            "1", "Go to Headers tab and add: Content-Type = application/json",
            "2", "Go to Body tab, select 'raw', then select 'JSON' from dropdown",
            "3", "Enter your JSON: {\"username\": \"john\", \"password\": \"252536\"}"
        ));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handles generic runtime exceptions.
     * 
     * @param ex the RuntimeException
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handles all other exceptions.
     * 
     * @param ex the Exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "An unexpected error occurred");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
