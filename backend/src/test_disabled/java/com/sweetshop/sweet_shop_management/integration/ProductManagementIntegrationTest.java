package com.sweetshop.sweet_shop_management.integration;

import com.fasterxml.        // Get the create                .andExpect(jsonPath("$.        // Get products by category
        mockMvc.perform(get("/api/v1/sweets/category/{category}", "MILK_BASED"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$[0].category").value("MILK_BASED"));").value("Updated Test Gulab Jamun"))
                .andExpect(jsonPath("$.price").value(28.99));product
        mockMvc.perform(get("/api/v1/sweets/{id}", sweetId))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.name").value("Integration Test Gulab Jamun"));

        // Update the product
        SweetUpdateRequest updateRequest = new SweetUpdateRequest();
        updateRequest.setName("Updated Test Gulab Jamun");
        updateRequest.setDescription("Updated traditional sweet description");
        updateRequest.setPrice(new BigDecimal("28.99"));tabind.ObjectMapper;
import com.sweetshop.sweet_shop_management.dto.SweetCreateRequest;
import com.sweetshop.sweet_shop_management.dto.SweetUpdateRequest;
import com.sweetshop.sweet_shop_management.dto.StockUpdateRequest;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.repository.SweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductManagementIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SweetRepository sweetRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        sweetRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCompleteProductLifecycle() throws Exception {
        // Create a new product
        SweetCreateRequest createRequest = new SweetCreateRequest();
        createRequest.setName("Integration Test Gulab Jamun");
        createRequest.setDescription("Traditional milk-based sweet for testing");
        createRequest.setCategory(Sweet.SweetCategory.MILK_BASED);
        createRequest.setPrice(new BigDecimal("25.99"));
        createRequest.setQuantity(100);

        String createResponse = mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Gulab Jamun"))
                .andExpect(jsonPath("$.price").value(25.99))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID from the response
        Long sweetId = objectMapper.readValue(createResponse, 
            com.sweetshop.sweet_shop_management.dto.SweetResponse.class).getId();

        // Get the created product
        mockMvc.perform(get("/api/v1/sweets/{id}", sweetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Chocolate"));

        // Update the product
        SweetUpdateRequest updateRequest = new SweetUpdateRequest();
        updateRequest.setName("Updated Test Chocolate");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("17.99"));

        mockMvc.perform(put("/api/v1/sweets/{id}", sweetId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Test Chocolate"))
                .andExpect(jsonPath("$.price").value(17.99));

        // Update stock
        StockUpdateRequest stockRequest = new StockUpdateRequest();
        stockRequest.setQuantity(150);

        mockMvc.perform(put("/api/v1/sweets/{id}/stock", sweetId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(150));

        // Reduce stock
        mockMvc.perform(put("/api/v1/sweets/{id}/stock/reduce/{amount}", sweetId, 50)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100));

        // Increase stock
        mockMvc.perform(put("/api/v1/sweets/{id}/stock/increase/{amount}", sweetId, 25)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(125));

        // Toggle availability
        mockMvc.perform(put("/api/v1/sweets/{id}/toggle-availability", sweetId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        // Search for the product
        mockMvc.perform(get("/api/v1/sweets/search")
                .param("term", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Updated Test Gulab Jamun"));

        // Get products by category
        mockMvc.perform(get("/api/v1/sweets/category/{category}", "CHOCOLATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("CHOCOLATE"));

        // Delete the product
        mockMvc.perform(delete("/api/v1/sweets/{id}", sweetId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify product is deleted
        mockMvc.perform(get("/api/v1/sweets/{id}", sweetId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testStockManagement() throws Exception {
        // Create a product with low stock
        SweetCreateRequest createRequest = new SweetCreateRequest();
        createRequest.setName("Low Stock Sweet");
        createRequest.setDescription("Sweet for testing stock management");
        createRequest.setCategory(Sweet.SweetCategory.FLOUR_BASED);
        createRequest.setPrice(new BigDecimal("5.99"));
        createRequest.setQuantity(5); // Low stock

        String response = mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long sweetId = objectMapper.readValue(response, 
            com.sweetshop.sweet_shop_management.dto.SweetResponse.class).getId();

        // Check low stock endpoint
        mockMvc.perform(get("/api/v1/sweets/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Low Stock Sweet"));

        // Reduce stock to zero
        mockMvc.perform(put("/api/v1/sweets/{id}/stock/reduce/{amount}", sweetId, 5)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(0));

        // Check out of stock endpoint
        mockMvc.perform(get("/api/v1/sweets/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Low Stock Sweet"));

        // Try to reduce stock below zero (should fail)
        mockMvc.perform(put("/api/v1/sweets/{id}/stock/reduce/{amount}", sweetId, 1)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testValidationErrors() throws Exception {
        // Test creating product with invalid data
        SweetCreateRequest invalidRequest = new SweetCreateRequest();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setPrice(new BigDecimal("-1")); // Negative price
        // Missing required fields

        mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAvailabilityFiltering() throws Exception {
        // Create available product
        SweetCreateRequest availableProduct = new SweetCreateRequest();
        availableProduct.setName("Available Sweet");
        availableProduct.setDescription("Available for purchase");
        availableProduct.setCategory(Sweet.SweetCategory.DRY_FRUIT);
        availableProduct.setPrice(new BigDecimal("8.99"));
        availableProduct.setQuantity(50);

        String response1 = mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availableProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long availableId = objectMapper.readValue(response1, 
            com.sweetshop.sweet_shop_management.dto.SweetResponse.class).getId();

        // Create unavailable product
        SweetCreateRequest unavailableProduct = new SweetCreateRequest();
        unavailableProduct.setName("Unavailable Sweet");
        unavailableProduct.setDescription("Not available for purchase");
        unavailableProduct.setCategory(Sweet.SweetCategory.SYRUP_BASED);
        unavailableProduct.setPrice(new BigDecimal("12.99"));
        unavailableProduct.setQuantity(30);

        String response2 = mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unavailableProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long unavailableId = objectMapper.readValue(response2, 
            com.sweetshop.sweet_shop_management.dto.SweetResponse.class).getId();

        // Make second product unavailable
        mockMvc.perform(put("/api/v1/sweets/{id}/toggle-availability", unavailableId)
                .with(csrf()))
                .andExpect(status().isOk());

        // Test available products endpoint
        mockMvc.perform(get("/api/v1/sweets/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Available Sweet"));

        // Test all products endpoint (should include both)
        mockMvc.perform(get("/api/v1/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}