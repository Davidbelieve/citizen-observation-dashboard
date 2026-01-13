package com.carbon.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains all required information for creating a new user account.
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Data
public class RegisterRequest {
    
    /**
     * Desired username for the new account.
     * Must be 3-50 characters long.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * Email address for the new account (optional).
     * If provided, must be a valid email format and unique in the system.
     * Validation is done in the service layer.
     */
    private String email;
    
    /**
     * Password for the new account.
     * Must be at least 6 characters long.
     * Will be encrypted before storage using BCrypt.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    /**
     * Full name of the user (optional).
     */
    private String fullName;
    
    // Explicit getters and setters (Lombok should generate these, but adding explicitly for compatibility)
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}