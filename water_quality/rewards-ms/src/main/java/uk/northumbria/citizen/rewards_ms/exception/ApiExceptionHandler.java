package uk.northumbria.citizen.rewards_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the rewards microservice.
 * Handles exceptions and returns appropriate HTTP responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    
    /**
     * Handles RuntimeException and returns a 500 Internal Server Error response.
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
    
    /**
     * Handles general Exception and returns a 500 Internal Server Error response.
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Server error: " + ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}

