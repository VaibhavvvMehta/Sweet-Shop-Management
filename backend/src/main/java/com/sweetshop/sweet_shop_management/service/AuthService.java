package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.dto.AuthResponse;
import com.sweetshop.sweet_shop_management.dto.UserLoginRequest;
import com.sweetshop.sweet_shop_management.dto.UserRegistrationRequest;

/**
 * Service interface for authentication operations.
 * Handles user registration, login, and token management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
public interface AuthService {
    
    /**
     * Registers a new user in the system
     * @param request User registration request containing user details
     * @return AuthResponse with success message and user information
     * @throws IllegalArgumentException if username or email already exists
     */
    AuthResponse registerUser(UserRegistrationRequest request);
    
    /**
     * Authenticates a user and generates a JWT token
     * @param request User login request containing credentials
     * @return AuthResponse with JWT token and user information
     * @throws BadCredentialsException if credentials are invalid
     */
    AuthResponse loginUser(UserLoginRequest request);
}