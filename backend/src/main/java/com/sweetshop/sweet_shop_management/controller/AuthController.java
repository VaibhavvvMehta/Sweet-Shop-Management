package com.sweetshop.sweet_shop_management.controller;

import com.sweetshop.sweet_shop_management.dto.AuthResponse;
import com.sweetshop.sweet_shop_management.dto.ErrorResponse;
import com.sweetshop.sweet_shop_management.dto.UserLoginRequest;
import com.sweetshop.sweet_shop_management.dto.UserRegistrationRequest;
import com.sweetshop.sweet_shop_management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST Controller for authentication operations.
 * Handles user registration, login, and authentication-related endpoints.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication, registration, and login")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided details. Username and email must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or user already exists",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "User registration details")
            @Valid @RequestBody UserRegistrationRequest request, 
            BindingResult bindingResult) {
        
        log.info("Registration request received for username: {}", request.getUsername());
        
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                
                log.warn("Validation error during registration: {}", errorMessage);
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(errorMessage));
            }
            
            // Attempt registration
            AuthResponse response = authService.registerUser(request);
            
            log.info("User {} registered successfully", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed for username {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
            
        } catch (Exception e) {
            log.error("Unexpected error during registration for username {}: {}", 
                     request.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user with username/email and password. Returns JWT token on success."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "User login credentials")
            @Valid @RequestBody UserLoginRequest request,
            BindingResult bindingResult) {
        
        log.info("Login request received for user: {}", request.getUsernameOrEmail());
        
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            String validationErrors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            
            log.warn("Validation error during login: {}", validationErrors);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(validationErrors));
        }
        
        try {
            AuthResponse response = authService.loginUser(request);
            log.info("User {} logged in successfully", request.getUsernameOrEmail());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: {}", request.getUsernameOrEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
                
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", 
                request.getUsernameOrEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred during login"));
        }
    }

    @Operation(
        summary = "Health check",
        description = "Returns the health status of the authentication service"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}