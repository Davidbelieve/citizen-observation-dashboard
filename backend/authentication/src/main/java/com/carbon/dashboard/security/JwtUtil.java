package com.carbon.dashboard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) operations.
 * Handles token generation, validation, and extraction of user information.
 * 
 * JWT Structure:
 * - Header: Algorithm and token type
 * - Payload: User data (username) and expiration
 * - Signature: Encrypted using secret key
 * 
 * Token Format: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.signature
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Component
public class JwtUtil {
    
    /**
     * Secret key for signing JWT tokens.
     * Injected from application.properties.
     * MUST be at least 256 bits (32 characters) for HS256 algorithm.
     */
    @Value("${jwt.secret:carbonDashboardSecretKeyForJWTTokenGenerationAndValidation12345}")
    private String secret;
    
    /**
     * Token expiration time in milliseconds.
     * Default: 86400000 ms = 24 hours.
     * Injected from application.properties.
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;
    
    /**
     * Generates a JWT token for an authenticated user.
     * Token contains username and expiration time.
     * 
     * Process:
     * 1. Create signing key from secret
     * 2. Set subject (username)
     * 3. Set issue and expiration dates
     * 4. Sign with HS256 algorithm
     * 
     * @param username the username to encode in the token
     * @return JWT token string
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        // Create signing key from secret
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        
        return Jwts.builder()
                .setSubject(username)              // Who the token is for
                .setIssuedAt(now)                  // When token was created
                .setExpiration(expiryDate)         // When token expires
                .signWith(key, SignatureAlgorithm.HS256)  // Sign with secret key
                .compact();                        // Build the token string
    }
    
    /**
     * Extracts username from a JWT token.
     * Decodes the token and retrieves the subject claim (username).
     * 
     * @param token the JWT token string
     * @return username encoded in the token
     * @throws JwtException if token is invalid or expired
     */
    public String getUsernameFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    /**
     * Validates a JWT token.
     * Checks if token is properly signed and not expired.
     * 
     * Validation checks:
     * - Token signature matches secret key
     * - Token has not expired
     * - Token structure is valid
     * 
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid, expired, or malformed
            return false;
        }
    }
}