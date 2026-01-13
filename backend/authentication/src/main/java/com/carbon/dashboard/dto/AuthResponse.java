package com.carbon.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses.
 * Contains JWT token and user information sent back to the client.
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * JWT authentication token.
     * Client must include this in Authorization header for subsequent requests.
     */
    private String token;
    
    /**
     * Username of the authenticated user.
     */
    private String username;
    
    /**
     * Email address of the authenticated user.
     */
    private String email;
    
    /**
     * Full name of the authenticated user.
     */
    private String fullName;
    
    /**
     * Response message (e.g., "Login successful", "Registration successful").
     */
    private String message;
    
    // Default constructor (needed for Jackson deserialization)
    public AuthResponse() {
    }
    
    public AuthResponse(String token, String username, String email, String fullName, String message) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.message = message;
    }
    
    // Explicit getters and setters (Lombok should generate these, but adding explicitly for compatibility)
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}