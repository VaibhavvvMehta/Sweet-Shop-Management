package com.sweetshop.sweet_shop_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetshop.sweet_shop_management.config.TestSecurityConfig;
import com.    @Test
    @DisplayName("Should get sweets by category")
    void testGetSweetsByCategory() throws Exception {
        // Given
        List<Sweet> milkBasedSweets = Arrays.asList(testSweet);
        when(sweetService.getSweetsByCategory(Sweet.SweetCategory.MILK_BASED))
                .thenReturn(milkBasedSweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/category/MILK_BASED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].category").value("MILK_BASED"));

        verify(sweetService).getSweetsByCategory(Sweet.SweetCategory.MILK_BASED);
    }hop_management.dto.SweetCreateRequest;
import com.sweetshop.sweet_shop_management.dto.SweetResponse;
import com.sweetshop.sweet_shop_management.dto.SweetUpdateRequest;
import com.sweetshop.sweet_shop_management.dto.StockUpdateRequest;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.service.SweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SweetController.
 * Tests REST API endpoints for sweet/product management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@WebMvcTest(SweetController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("Sweet Controller Tests")
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SweetService sweetService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sweet testSweet;
    private SweetCreateRequest createRequest;
    private SweetUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet("Test Gulab Jamun", Sweet.SweetCategory.MILK_BASED, 
                             new BigDecimal("25.99"), 100);
        testSweet.setId(1L);
        testSweet.setDescription("Delicious traditional Indian sweet");
        testSweet.setBrand("Traditional Sweets");
        testSweet.setUnit("250g");

        createRequest = new SweetCreateRequest();
        createRequest.setName("New Sweet");
        createRequest.setCategory(Sweet.SweetCategory.DRY_FRUIT);
        createRequest.setPrice(new BigDecimal("450.99"));
        createRequest.setQuantity(75);
        createRequest.setDescription("Premium dry fruit sweet");
        createRequest.setBrand("Dry Fruit Delights");

        updateRequest = new SweetUpdateRequest();
        updateRequest.setName("Updated Sweet");
        updateRequest.setPrice(new BigDecimal("6.99"));
        updateRequest.setQuantity(120);
    }

    @Test
    @DisplayName("Should create sweet successfully")
    @WithMockUser
    void testCreateSweet() throws Exception {
        // Given
        Sweet createdSweet = createRequest.toSweet();
        createdSweet.setId(2L);
        when(sweetService.createSweet(any(Sweet.class))).thenReturn(createdSweet);

        // When & Then
        mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("New Sweet"))
                .andExpect(jsonPath("$.category").value("DRY_FRUIT"))
                .andExpect(jsonPath("$.price").value(3.99))
                .andExpect(jsonPath("$.quantity").value(75));

        verify(sweetService).createSweet(any(Sweet.class));
    }

    @Test
    @DisplayName("Should return validation error for invalid create request")
    @WithMockUser
    void testCreateSweetValidationError() throws Exception {
        // Given
        SweetCreateRequest invalidRequest = new SweetCreateRequest();
        invalidRequest.setName(""); // Invalid: empty name
        invalidRequest.setPrice(new BigDecimal("-1")); // Invalid: negative price

        // When & Then
        mockMvc.perform(post("/api/v1/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(sweetService, never()).createSweet(any());
    }

    @Test
    @DisplayName("Should get sweet by ID")
    void testGetSweetById() throws Exception {
        // Given
        when(sweetService.findById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Gulab Jamun"))
                .andExpect(jsonPath("$.category").value("MILK_BASED"))
                .andExpect(jsonPath("$.price").value(5.99));

        verify(sweetService).findById(1L);
    }

    @Test
    @DisplayName("Should return 404 when sweet not found")
    void testGetSweetByIdNotFound() throws Exception {
        // Given
        when(sweetService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/999"))
                .andExpect(status().isNotFound());

        verify(sweetService).findById(999L);
    }

    @Test
    @DisplayName("Should get all sweets")
    void testGetAllSweets() throws Exception {
        // Given
        Sweet sweet2 = new Sweet("Kaju Katli", Sweet.SweetCategory.DRY_FRUIT, 
                                new BigDecimal("450.99"), 50);
        sweet2.setId(2L);
        List<Sweet> sweets = Arrays.asList(testSweet, sweet2);
        when(sweetService.getAllSweets()).thenReturn(sweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Gulab Jamun"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpected(jsonPath("$[1].name").value("Kaju Katli"));

        verify(sweetService).getAllSweets();
    }

    @Test
    @DisplayName("Should get available sweets only")
    void testGetAvailableSweets() throws Exception {
        // Given
        List<Sweet> availableSweets = Arrays.asList(testSweet);
        when(sweetService.getAvailableSweets()).thenReturn(availableSweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Gulab Jamun"));

        verify(sweetService).getAvailableSweets();
    }

    @Test
    @DisplayName("Should get sweets by category")
    void testGetSweetsByCategory() throws Exception {
        // Given
        List<Sweet> chocolateSweets = Arrays.asList(testSweet);
        when(sweetService.getSweetsByCategory(Sweet.SweetCategory.CHOCOLATE))
                .thenReturn(chocolateSweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/category/CHOCOLATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].category").value("CHOCOLATE"));

        verify(sweetService).getSweetsByCategory(Sweet.SweetCategory.CHOCOLATE);
    }

    @Test
    @DisplayName("Should search sweets")
    void testSearchSweets() throws Exception {
        // Given
        List<Sweet> searchResults = Arrays.asList(testSweet);
        when(sweetService.searchSweets("gulab")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/search")
                .param("term", "gulab"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Gulab Jamun"));

        verify(sweetService).searchSweets("gulab");
    }

    @Test
    @DisplayName("Should update sweet")
    void testUpdateSweet() throws Exception {
        // Given
        Sweet updatedSweet = new Sweet("Updated Sweet", Sweet.SweetCategory.MILK_BASED, 
                                      new BigDecimal("35.99"), 120);
        updatedSweet.setId(1L);
        when(sweetService.updateSweet(eq(1L), any(Sweet.class))).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Sweet"))
                .andExpect(jsonPath("$.price").value(6.99))
                .andExpect(jsonPath("$.quantity").value(120));

        verify(sweetService).updateSweet(eq(1L), any(Sweet.class));
    }

    @Test
    @DisplayName("Should delete sweet")
    void testDeleteSweet() throws Exception {
        // Given
        doNothing().when(sweetService).deleteSweet(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/sweets/1"))
                .andExpect(status().isNoContent());

        verify(sweetService).deleteSweet(1L);
    }

    @Test
    @DisplayName("Should update stock")
    @WithMockUser
    void testUpdateStock() throws Exception {
        // Given
        StockUpdateRequest stockRequest = new StockUpdateRequest(150, "Restocking");
        Sweet updatedSweet = new Sweet(testSweet.getName(), testSweet.getCategory(), 
                                      testSweet.getPrice(), 150);
        updatedSweet.setId(1L);
        when(sweetService.updateStock(1L, 150)).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1/stock")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(150));

        verify(sweetService).updateStock(1L, 150);
    }

    @Test
    @DisplayName("Should reduce stock")
    void testReduceStock() throws Exception {
        // Given
        when(sweetService.reduceStock(1L, 10)).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1/stock/reduce/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock reduced successfully"));

        verify(sweetService).reduceStock(1L, 10);
    }

    @Test
    @DisplayName("Should fail to reduce stock when insufficient")
    void testReduceStockInsufficientQuantity() throws Exception {
        // Given
        when(sweetService.reduceStock(1L, 150)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1/stock/reduce/150"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient stock"));

        verify(sweetService).reduceStock(1L, 150);
    }

    @Test
    @DisplayName("Should increase stock")
    void testIncreaseStock() throws Exception {
        // Given
        Sweet updatedSweet = new Sweet(testSweet.getName(), testSweet.getCategory(), 
                                      testSweet.getPrice(), 120);
        updatedSweet.setId(1L);
        when(sweetService.increaseStock(1L, 20)).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1/stock/increase/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(120));

        verify(sweetService).increaseStock(1L, 20);
    }

    @Test
    @DisplayName("Should toggle availability")
    void testToggleAvailability() throws Exception {
        // Given
        Sweet toggledSweet = new Sweet(testSweet.getName(), testSweet.getCategory(), 
                                      testSweet.getPrice(), testSweet.getQuantity());
        toggledSweet.setId(1L);
        toggledSweet.setIsAvailable(false);
        when(sweetService.toggleAvailability(1L)).thenReturn(toggledSweet);

        // When & Then
        mockMvc.perform(put("/api/v1/sweets/1/toggle-availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isAvailable").value(false));

        verify(sweetService).toggleAvailability(1L);
    }

    @Test
    @DisplayName("Should get low stock sweets")
    void testGetLowStockSweets() throws Exception {
        // Given
        Sweet lowStockSweet = new Sweet("Low Stock Sweet", Sweet.SweetCategory.FLOUR_BASED, 
                                       new BigDecimal("35.99"), 5);
        lowStockSweet.setId(3L);
        lowStockSweet.setMinStockLevel(10);
        List<Sweet> lowStockSweets = Arrays.asList(lowStockSweet);
        when(sweetService.getLowStockSweets()).thenReturn(lowStockSweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].lowStock").value(true));

        verify(sweetService).getLowStockSweets();
    }

    @Test
    @DisplayName("Should get out of stock sweets")
    void testGetOutOfStockSweets() throws Exception {
        // Given
        Sweet outOfStockSweet = new Sweet("Out of Stock Sweet", Sweet.SweetCategory.SYRUP_BASED, 
                                         new BigDecimal("180.99"), 0);
        outOfStockSweet.setId(4L);
        List<Sweet> outOfStockSweets = Arrays.asList(outOfStockSweet);
        when(sweetService.getOutOfStockSweets()).thenReturn(outOfStockSweets);

        // When & Then
        mockMvc.perform(get("/api/v1/sweets/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4L))
                .andExpect(jsonPath("$[0].inStock").value(false));

        verify(sweetService).getOutOfStockSweets();
    }
}