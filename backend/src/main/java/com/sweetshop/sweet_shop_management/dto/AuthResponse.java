package com.sweetshop.sweet_shop_management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Used for both registration and login responses.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class AuthResponse {
    
    /**
     * Response message (success or error message)
     */
    private String message;
    
    /**
     * JWT token for authenticated requests
     */
    private String token;
    
    /**
     * User information including role
     */
    private UserDTO user;
    
    /**
     * Constructor for success responses with user data
     * @param message Success message
     * @param token JWT token
     * @param user User information
     */
    public AuthResponse(String message, String token, UserDTO user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }
}