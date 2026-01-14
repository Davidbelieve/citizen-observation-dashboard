package com.carbon.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 * Contains username and password for authentication.
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Data
public class LoginRequest {
    
    /**
     * Username for authentication.
     * Cannot be blank.
     */
    @NotBlank(message = "Username is required")
    private String username;
    
    /**
     * Password for authentication.
     * Cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;
    
    // Explicit setters (Lombok should generate these, but adding explicitly for compatibility)
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Explicit getters (Lombok should generate these, but adding explicitly for compatibility)
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
}