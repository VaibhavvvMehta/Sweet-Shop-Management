package com.sweetshop.sweet_shop_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 * Contains all necessary information for creating a new user account.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    
    /**
     * Desired username for the new account
     * Must be unique and between 3-50 characters
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * Email address for the new account
     * Must be unique and valid email format
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * Password for the new account
     * Must be at least 6 characters long
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    /**
     * Optional first name of the user
     */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    /**
     * Optional last name of the user
     */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
}