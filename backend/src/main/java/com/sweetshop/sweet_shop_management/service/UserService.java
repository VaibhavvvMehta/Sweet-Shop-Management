package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations.
 * Handles user profile management and user-related queries.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
public interface UserService {
    
    /**
     * Finds a user by username
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by email
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a username already exists in the system
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if an email already exists in the system
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Gets all active users
     * @return List of active users
     */
    List<User> getAllActiveUsers();
    
    /**
     * Searches users by a search term
     * @param searchTerm The term to search for
     * @return List of matching users
     */
    List<User> searchUsers(String searchTerm);
}