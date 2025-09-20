package com.sweetshop.sweet_shop_management.integration;

import com.sweetshop.sweet_shop_management.dto.UserLoginRequest;
import com.sweetshop.sweet_shop_management.dto.UserRegistrationRequest;
import com.sweetshop.sweet_shop_management.model.Role;
import com.sweetshop.sweet_shop_management.model.User;
import com.sweetshop.sweet_shop_management.repository.RoleRepository;
import com.sweetshop.sweet_shop_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for authentication flow.
 * Tests complete user registration and login with database persistence.
 */
@Sql(scripts = {"/test-schema.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUpTestData() {
        // SQL scripts handle schema and data initialization
        // This method can be used for additional test setup if needed
    }

    @Test
    public void testCompleteRegistrationFlow() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("integrationUser");
        request.setEmail("integration@test.com");
        request.setPassword("password123");
        request.setFirstName("Integration");
        request.setLastName("User");

        // When - Register user
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("integrationUser"))
                .andReturn();

        // Then - Verify user is persisted in database
        User savedUser = userRepository.findByUsername("integrationUser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("integrationUser");
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Integration");
        assertThat(savedUser.getLastName()).isEqualTo("User");
        assertThat(savedUser.getIsActive()).isTrue();
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    public void testCompleteLoginFlow() throws Exception {
        // Given - Create user in database
        User user = new User();
        user.setUsername("loginUser");
        user.setEmail("login@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Login");
        user.setLastName("User");
        user.setIsActive(true);
        userRepository.save(user);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("loginUser");
        loginRequest.setPassword("password123");

        // When - Login user
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.username").value("loginUser"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // Then - Verify JWT token is valid
        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("token");
        assertThat(response).contains("Login successful");
    }

    @Test
    public void testRegistrationThenLoginFlow() throws Exception {
        // Given
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("fullFlowUser");
        registrationRequest.setEmail("fullflow@test.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFirstName("Full");
        registrationRequest.setLastName("Flow");

        // When - First register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // Verify user exists in database
        User savedUser = userRepository.findByUsername("fullFlowUser").orElse(null);
        assertThat(savedUser).isNotNull();

        // Then - Login with the registered user
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("fullFlowUser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.username").value("fullFlowUser"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testDuplicateRegistrationPrevention() throws Exception {
        // Given - First registration
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("duplicateUser");
        request.setEmail("duplicate@test.com");
        request.setPassword("password123");
        request.setFirstName("Duplicate");
        request.setLastName("User");

        // When - Register first time (should succeed)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated());

        // Then - Try to register same username (should fail)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));

        // Verify only one user exists in database
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1);
    }

    @Test
    public void testLoginWithInvalidCredentialsAgainstDatabase() throws Exception {
        // Given - Create user in database
        User user = new User();
        user.setUsername("validUser");
        user.setEmail("valid@test.com");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setFirstName("Valid");
        user.setLastName("User");
        user.setIsActive(true);
        userRepository.save(user);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("validUser");
        loginRequest.setPassword("wrongPassword");

        // When & Then - Login with wrong password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    public void testDisabledUserLoginPrevention() throws Exception {
        // Given - Create disabled user in database
        User user = new User();
        user.setUsername("disabledUser");
        user.setEmail("disabled@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Disabled");
        user.setLastName("User");
        user.setIsActive(false); // User is disabled
        userRepository.save(user);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("disabledUser");
        loginRequest.setPassword("password123");

        // When & Then - Try to login with disabled account
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Account is disabled"));
    }
}