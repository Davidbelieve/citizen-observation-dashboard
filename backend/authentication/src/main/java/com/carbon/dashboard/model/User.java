package com.carbon.dashboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User entity representing a user account in the system.
 * Stores user credentials with encrypted passwords in H2 in-memory database.
 * This entity is used for authentication and authorization.
 * 
 * Key Features:
 * - Passwords are encrypted using BCrypt before storage
 * - Email is optional (can be null)
 * - Username must be unique
 * - Automatic timestamp management for created_at and updated_at
 * - Account can be enabled/disabled
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Unique identifier for the user.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Username for login (unique across all users).
     * Required field with maximum 50 characters.
     * Used as the primary identifier for authentication.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * Email address (optional, unique if provided).
     * Maximum 100 characters.
     * Must be a valid email format if provided (validated in DTO).
     */
    @Column(unique = true, length = 100)
    private String email;
    
    /**
     * Encrypted password using BCrypt hashing algorithm.
     * Never stored in plain text.
     * Required field.
     * 
     * Format: BCrypt hash (60 characters)
     * Example: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * User's full name (optional).
     * Maximum 100 characters.
     */
    @Column(length = 100)
    private String fullName;
    
    /**
     * Timestamp when the user account was created.
     * Automatically set when entity is first persisted.
     * Cannot be updated after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user account was last updated.
     * Automatically updated whenever entity is modified.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Account enabled status.
     * true = account is active and can login
     * false = account is disabled and cannot login
     * Defaults to true.
     */
    @Column(nullable = false)
    private boolean enabled = true;
    
    /**
     * JPA lifecycle callback executed before persisting a new entity.
     * Sets the creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * JPA lifecycle callback executed before updating an existing entity.
     * Updates the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Explicit getters and setters (Lombok should generate these, but adding explicitly for compatibility)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}