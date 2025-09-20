package com.sweetshop.sweet_shop_management.service.impl;

import com.sweetshop.sweet_shop_management.dto.AuthResponse;
import com.sweetshop.sweet_shop_management.dto.UserDTO;
import com.sweetshop.sweet_shop_management.dto.UserLoginRequest;
import com.sweetshop.sweet_shop_management.dto.UserRegistrationRequest;
import com.sweetshop.sweet_shop_management.model.Role;
import com.sweetshop.sweet_shop_management.model.User;
import com.sweetshop.sweet_shop_management.repository.RoleRepository;
import com.sweetshop.sweet_shop_management.repository.UserRepository;
import com.sweetshop.sweet_shop_management.service.AuthService;
import com.sweetshop.sweet_shop_management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of AuthService for handling authentication operations.
 * Provides user registration with password hashing and role assignment.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    
    // Constants for validation and messages
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String USERNAME_EXISTS_MESSAGE = "Username already exists";
    private static final String EMAIL_EXISTS_MESSAGE = "Email already exists"; 
    private static final String DEFAULT_ROLE_NOT_FOUND_MESSAGE = "Default USER role not found";
    private static final String REGISTRATION_SUCCESS_MESSAGE = "User registered successfully";
    private static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";
    private static final String ACCOUNT_DISABLED_MESSAGE = "Account is disabled";
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Registers a new user with default USER role
     * @param request User registration request containing user details
     * @return AuthResponse with success message and user information
     * @throws IllegalArgumentException if username or email already exists or validation fails
     */
    @Override
    public AuthResponse registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user with username: {}", request.getUsername());
        
        // Validate input
        validateRegistrationRequest(request);
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username {} already exists", request.getUsername());
            throw new IllegalArgumentException(USERNAME_EXISTS_MESSAGE);
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException(EMAIL_EXISTS_MESSAGE);
        }
        
        // Create new user entity
        User user = createUserFromRequest(request);
        
        // Assign default USER role
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException(DEFAULT_ROLE_NOT_FOUND_MESSAGE));
        
        user.addRole(userRole);
        
        // Save user to database
        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully with ID: {}", savedUser.getUsername(), savedUser.getId());
        
        // Generate JWT token for immediate login after registration
        String roles = savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.joining(","));
        
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getEmail(), roles);
        
        // Return success response with token
        return new AuthResponse(
                REGISTRATION_SUCCESS_MESSAGE,
                token,
                convertToUserDTO(savedUser)
        );
    }
    
    /**
     * Converts User entity to UserDTO
     * @param user User entity
     * @return UserDTO with user information
     */
    private UserDTO convertToUserDTO(User user) {
        String primaryRole = user.getRoles().stream()
                .map(role -> role.getName().name())
                .findFirst()
                .orElse("USER");
        
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(), // Use the existing getFullName() method that handles nulls properly
                primaryRole,
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    
    /**
     * Validates the registration request
     * @param request The registration request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (request.getPassword() == null || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        // Basic email validation
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    /**
     * Creates a User entity from the registration request
     * @param request The registration request
     * @return User entity with hashed password
     */
    private User createUserFromRequest(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }
        
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName().trim());
        }
        
        user.setIsActive(true);
        
        return user;
    }
    
    /**
     * GREEN PHASE: Implementing login functionality to make tests pass
     */
    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsernameOrEmail());
        
        // Validate input
        validateLoginRequest(request);
        
        // Find user by username or email
        User user = findUserByUsernameOrEmail(request.getUsernameOrEmail());
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getUsernameOrEmail());
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
        
        // Check if user account is active
        if (!user.getIsActive()) {
            log.warn("Attempted login with disabled account: {}", request.getUsernameOrEmail());
            throw new BadCredentialsException(ACCOUNT_DISABLED_MESSAGE);
        }
        
        // Generate JWT token
        String roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.joining(","));
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), roles);
        
        log.info("User {} authenticated successfully", user.getUsername());
        
        return new AuthResponse(
                LOGIN_SUCCESS_MESSAGE,
                token,
                convertToUserDTO(user)
        );
    }
    
    /**
     * Validates the login request
     * @param request The login request to validate
     * @throws BadCredentialsException if validation fails
     */
    private void validateLoginRequest(UserLoginRequest request) {
        if (request == null) {
            throw new BadCredentialsException("Login request cannot be null");
        }
        
        if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().trim().isEmpty()) {
            throw new BadCredentialsException("Username or email is required");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadCredentialsException("Password is required");
        }
    }
    
    /**
     * Finds user by username or email
     * @param usernameOrEmail Username or email to search
     * @return User entity
     * @throws BadCredentialsException if user not found
     */
    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        // Try to find by username first
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        
        // If not found by username, try by email
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }
        
        // If still not found, throw exception
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", usernameOrEmail);
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
        
        return userOpt.get();
    }
    
    /**
     * Basic email validation
     * @param email Email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") && 
               email.length() > 5;
    }
}