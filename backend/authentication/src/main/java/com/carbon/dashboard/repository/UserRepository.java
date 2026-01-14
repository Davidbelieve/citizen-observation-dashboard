package com.carbon.dashboard.repository;

import com.carbon.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides CRUD operations and custom query methods for user data access.
 * Extends JpaRepository to inherit standard database operations.
 * 
 * Spring Data JPA automatically implements this interface at runtime,
 * so no implementation class is needed.
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their username.
     * Used during login authentication to locate user credentials.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by their email address.
     * Used for password recovery and duplicate email checking.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a username already exists in the system.
     * Used during registration to prevent duplicate usernames.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if an email address already exists in the system.
     * Used during registration to prevent duplicate email addresses.
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if an email address already exists in the system.
     * Used during registration to prevent duplicate email addresses.
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmailAndEmailIsNotNull(String email);
}