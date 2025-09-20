package com.sweetshop.sweet_shop_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetshop.sweet_shop_management.dto.UserRegistrationRequest;
import com.sweetshop.sweet_shop_management.dto.AuthResponse;
import com.sweetshop.sweet_shop_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController registration endpoint.
 * Follows TDD approach: Red-Green-Refactor
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@WebMvcTest(AuthController.class)
@Import(com.sweetshop.sweet_shop_management.config.TestSecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests successful user registration
     */
    @Test
    @DisplayName("Should register user successfully and return success response")
    @WithMockUser
    void testRegisterUser_Success() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser", 
            "test@example.com", 
            "password123",
            "Test",
            "User"
        );

        AuthResponse expectedResponse = new AuthResponse(
            "User registered successfully",
            "testuser",
            "test@example.com",
            null // No JWT token on registration
        );

        when(authService.registerUser(any(UserRegistrationRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests registration with invalid input (missing required fields)
     */
    @Test
    @DisplayName("Should return validation error for invalid registration request")
    @WithMockUser
    void testRegisterUser_ValidationError() throws Exception {
        // Arrange - Invalid request with missing email and short password
        UserRegistrationRequest request = new UserRegistrationRequest(
            "ab", // Too short username
            "", // Empty email
            "123", // Too short password
            "",
            ""
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests registration with duplicate username
     */
    @Test
    @DisplayName("Should return conflict error for duplicate username")
    @WithMockUser
    void testRegisterUser_DuplicateUsername() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
            "existinguser", 
            "existing@example.com", 
            "password123",
            "Existing",
            "User"
        );

        when(authService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests registration with duplicate email
     */
    @Test
    @DisplayName("Should return conflict error for duplicate email")
    @WithMockUser
    void testRegisterUser_DuplicateEmail() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
            "newuser", 
            "existing@example.com", 
            "password123",
            "New",
            "User"
        );

        when(authService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests registration with malformed JSON
     */
    @Test
    @DisplayName("Should return bad request for malformed JSON")
    @WithMockUser
    void testRegisterUser_MalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}