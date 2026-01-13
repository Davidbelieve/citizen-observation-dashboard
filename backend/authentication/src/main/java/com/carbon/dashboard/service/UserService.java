package com.carbon.dashboard.service;

import com.carbon.dashboard.dto.LoginRequest;
import com.carbon.dashboard.dto.RegisterRequest;
import com.carbon.dashboard.dto.AuthResponse;
import com.carbon.dashboard.model.User;
import com.carbon.dashboard.repository.UserRepository;
import com.carbon.dashboard.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Service class for user authentication and management.
 */
@Service
public class UserService implements UserDetailsService {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    
    /**
     * Validates email format if email is provided.
     * 
     * @param email email address to validate
     * @return true if email is valid or null/empty, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email is optional, so null/empty is valid
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Registers a new user account with encrypted password.
     * Only username and password are required. Email and fullName are optional.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Handle email - validate only if provided
        String email = request.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            // Validate email format if provided
            if (!isValidEmail(email)) {
                throw new RuntimeException("Email must be valid");
            }
            // Check email uniqueness only if email is provided
            if (userRepository.existsByEmail(email.trim())) {
                throw new RuntimeException("Email already exists");
            }
            email = email.trim();
        } else {
            email = null; // Set to null if empty or not provided
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEnabled(true);
        
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(
            token, 
            user.getUsername(), 
            user.getEmail(), 
            user.getFullName(), 
            "Registration successful"
        );
    }
    
    /**
     * Authenticates user credentials and generates JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(
            token, 
            user.getUsername(), 
            user.getEmail(), 
            user.getFullName(), 
            "Login successful"
        );
    }
    
    /**
     * Changes user password with encryption.
     */
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Loads user details by username for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                new ArrayList<>()
        );
    }
}