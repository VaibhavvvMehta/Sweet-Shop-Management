package com.sweetshop.sweet_shop_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for user information.
 * Used to send user data to the frontend without sensitive information.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    /**
     * Unique identifier for the user
     */
    private Long id;
    
    /**
     * Username of the user
     */
    private String username;
    
    /**
     * Email address of the user
     */
    private String email;
    
    /**
     * Full name of the user
     */
    private String fullName;
    
    /**
     * Primary role of the user (USER or ADMIN)
     */
    private String role;
    
    /**
     * Whether the user account is active
     */
    private Boolean isActive;
    
    /**
     * When the user account was created
     */
    private LocalDateTime createdAt;
    
    /**
     * When the user account was last updated
     */
    private LocalDateTime updatedAt;
}
