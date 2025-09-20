package com.sweetshop.sweet_shop_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for error responses.
 * Provides consistent error message format across the API.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Error message describing what went wrong
     */
    private String error;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * Timestamp of the error
     */
    private String timestamp;
    
    /**
     * Constructor with just error message
     * @param error The error message
     */
    public ErrorResponse(String error) {
        this.error = error;
    }
}