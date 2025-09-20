package com.sweetshop.sweet_shop_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetshop.sweet_shop_management.dto.AuthResponse;
import com.sweetshop.sweet_shop_management.dto.UserLoginRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController login endpoint.
 * Follows TDD approach: Red-Green-Refactor
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@WebMvcTest(AuthController.class)
@Import(com.sweetshop.sweet_shop_management.config.TestSecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerLoginTest {

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
     * Tests successful user login with valid credentials
     */
    @Test
    @DisplayName("Should login user successfully and return JWT token")
    @WithMockUser
    void testLoginUser_Success() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("testuser", "password123");

        AuthResponse expectedResponse = new AuthResponse(
            "Login successful",
            "testuser",
            "test@example.com",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Mock JWT token
        );

        when(authService.loginUser(any(UserLoginRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."));
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests login with invalid credentials
     */
    @Test
    @DisplayName("Should return unauthorized for invalid credentials")
    @WithMockUser
    void testLoginUser_InvalidCredentials() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("testuser", "wrongpassword");

        when(authService.loginUser(any(UserLoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests login with missing credentials
     */
    @Test
    @DisplayName("Should return validation error for missing credentials")
    @WithMockUser
    void testLoginUser_MissingCredentials() throws Exception {
        // Arrange - Request with empty username and password
        UserLoginRequest request = new UserLoginRequest("", "");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests login with non-existent user
     */
    @Test
    @DisplayName("Should return unauthorized for non-existent user")
    @WithMockUser
    void testLoginUser_UserNotFound() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("nonexistentuser", "password123");

        when(authService.loginUser(any(UserLoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests login with malformed JSON
     */
    @Test
    @DisplayName("Should return bad request for malformed JSON")
    @WithMockUser
    void testLoginUser_MalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    /**
     * RED PHASE TEST: This test should FAIL initially
     * Tests login with account that is disabled/inactive
     */
    @Test
    @DisplayName("Should return unauthorized for disabled account")
    @WithMockUser
    void testLoginUser_DisabledAccount() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("disableduser", "password123");

        when(authService.loginUser(any(UserLoginRequest.class)))
                .thenThrow(new BadCredentialsException("Account is disabled"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Account is disabled"));
    }
}