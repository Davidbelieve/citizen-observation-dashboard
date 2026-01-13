package com.carbon.dashboard.controller;

import com.carbon.dashboard.dto.AuthResponse;
import com.carbon.dashboard.dto.LoginRequest;
import com.carbon.dashboard.dto.RegisterRequest;
import com.carbon.dashboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST Controller for authentication operations.
 * Handles user registration, login, and password management.
 * 
 * Base URL: /api/auth
 * 
 * Endpoints:
 * - POST /api/auth/register - Create new user account
 * - POST /api/auth/login - Authenticate user and get JWT token
 * - PUT /api/auth/change-password - Change user password
 * 
 * These endpoints are PUBLIC (no authentication required).
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Registers a new user account.
     * Creates user with encrypted password and returns JWT token.
     * 
     * POST /api/auth/register
     * 
     * Request Body Example:
     * {
     *   "username": "john",
     *   "email": "john@email.com",
     *   "password": "password123",
     * }
     * 
     * Success Response (201 CREATED):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "username": "john",
     *   "email": "john@email.com",
     *   "message": "Registration successful"
     * }
     * 
     * Error Response (400 BAD REQUEST):
     * {
     *   "error": "Username already exists"
     * }
     * 
     * @param request RegisterRequest with user details
     * @return ResponseEntity with AuthResponse or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Authenticates user and returns JWT token.
     * Verifies credentials and generates token for session.
     * 
     * POST /api/auth/login
     * 
     * Request Body Example:
     * {
     *   "username": "john",
     *   "password": "password123"
     * }
     * 
     * Success Response (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "username": "john",
     *   "email": "john@email.com",
     *   "fullName": "John Smith",
     *   "message": "Login successful"
     * }
     * 
     * Error Response (401 UNAUTHORIZED):
     * {
     *   "error": "Invalid username or password"
     * }
     * 
     * @param request LoginRequest with username and password
     * @return ResponseEntity with AuthResponse or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Debug: Log what was received (remove in production)
        System.out.println("DEBUG - Received login request:");
        System.out.println("  Username: " + (request.getUsername() != null ? request.getUsername() : "NULL"));
        System.out.println("  Password: " + (request.getPassword() != null ? "***" : "NULL"));
        
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("DEBUG - BadCredentialsException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (UsernameNotFoundException e) {
            System.out.println("DEBUG - UsernameNotFoundException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            System.out.println("DEBUG - Exception during login: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
    
    /**
     * Changes user password.
     * Requires authentication (user must be logged in).
     * 
     * PUT /api/auth/change-password
     * 
     * Request Body Example:
     * {
     *   "username": "john",
     *   "newPassword": "newpassword456"
     * }
     * 
     * Success Response (200 OK):
     * {
     *   "message": "Password changed successfully"
     * }
     * 
     * Error Response (400 BAD REQUEST):
     * {
     *   "error": "User not found"
     * }
     * 
     * @param request Map containing username and newPassword
     * @return ResponseEntity with success or error message
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String newPassword = request.get("newPassword");
            
            userService.changePassword(username, newPassword);
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}