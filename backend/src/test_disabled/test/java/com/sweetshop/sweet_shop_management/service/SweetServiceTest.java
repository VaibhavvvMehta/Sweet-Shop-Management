package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_sho        Sweet duplicateSweet = new Sweet("Test Gulab         when(sweetRepository.findByName("Test Gulab Jamun")).thenReturn(Optional.of(testSweet));

        // When
        Optional<Sweet> result = sweetService.findByName("Test Gulab Jamun");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testSweet);
        verify(sweetRepository).findByName("Test Gulab Jamun");weet.SweetCategory.MILK_BASED, 
                                         new BigDecimal("25.99"), 100);
        when(sweetRepository.existsByName("Test Gulab Jamun")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sweetService.createSweet(duplicateSweet))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Sweet with name 'Test Gulab Jamun' already exists");
        verify(sweetRepository).existsByName("Test Gulab Jamun");ent.model.Sweet;
import com.sweetshop.sweet_shop_management.repository.SweetRepository;
import com.sweetshop.sweet_shop_management.service.impl.SweetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SweetService implementation.
 * Tests all business logic for sweet/product management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Sweet Service Tests")
class SweetServiceTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetServiceImpl sweetService;

    private Sweet testSweet;
    private Sweet milkBasedSweet;
    private Sweet dryFruitSweet;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet("Test Gulab Jamun", Sweet.SweetCategory.MILK_BASED, 
                             new BigDecimal("25.99"), 100);
        testSweet.setId(1L);
        testSweet.setDescription("Traditional milk-based sweet");
        testSweet.setBrand("Traditional Sweets");
        testSweet.setUnit("250g");

        milkBasedSweet = new Sweet("Rasgulla", Sweet.SweetCategory.MILK_BASED, 
                                  new BigDecimal("20.99"), 50);
        milkBasedSweet.setId(2L);

        dryFruitSweet = new Sweet("Kaju Katli", Sweet.SweetCategory.DRY_FRUIT, 
                              new BigDecimal("450.99"), 25);
        dryFruitSweet.setId(3L);
    }

    @Test
    @DisplayName("Should create sweet successfully")
    void testCreateSweet() {
        // Given
        Sweet newSweet = new Sweet("New Sweet", Sweet.SweetCategory.SYRUP_BASED, 
                                  new BigDecimal("180.99"), 75);
        when(sweetRepository.existsByName("New Sweet")).thenReturn(false);
        when(sweetRepository.save(any(Sweet.class))).thenReturn(newSweet);

        // When
        Sweet result = sweetService.createSweet(newSweet);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Sweet");
        assertThat(result.getCategory()).isEqualTo(Sweet.SweetCategory.HARD_CANDY);
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("4.99"));
        verify(sweetRepository).existsByName("New Sweet");
        verify(sweetRepository).save(newSweet);
    }

    @Test
    @DisplayName("Should throw exception when creating sweet with duplicate name")
    void testCreateSweetWithDuplicateName() {
        // Given
        Sweet duplicateSweet = new Sweet("Test Chocolate", Sweet.SweetCategory.CHOCOLATE, 
                                        new BigDecimal("5.99"), 100);
        when(sweetRepository.existsByName("Test Chocolate")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sweetService.createSweet(duplicateSweet))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sweet with name 'Test Chocolate' already exists");
        verify(sweetRepository).existsByName("Test Chocolate");
        verify(sweetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find sweet by ID")
    void testFindSweetById() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        Optional<Sweet> result = sweetService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Gulab Jamun");
        verify(sweetRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when sweet not found by ID")
    void testFindSweetByIdNotFound() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Sweet> result = sweetService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(sweetRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find sweet by name")
    void testFindSweetByName() {
        // Given
        when(sweetRepository.findByName("Test Chocolate")).thenReturn(Optional.of(testSweet));

        // When
        Optional<Sweet> result = sweetService.findByName("Test Chocolate");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(sweetRepository).findByName("Test Chocolate");
    }

    @Test
    @DisplayName("Should get all sweets")
    void testGetAllSweets() {
        // Given
        List<Sweet> sweets = Arrays.asList(testSweet, chocolateSweet, gummySweet);
        when(sweetRepository.findAll()).thenReturn(sweets);

        // When
        List<Sweet> result = sweetService.getAllSweets();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(testSweet, chocolateSweet, gummySweet);
        verify(sweetRepository).findAll();
    }

    @Test
    @DisplayName("Should get available sweets only")
    void testGetAvailableSweets() {
        // Given
        List<Sweet> availableSweets = Arrays.asList(testSweet, chocolateSweet);
        when(sweetRepository.findByIsAvailableTrue()).thenReturn(availableSweets);

        // When
        List<Sweet> result = sweetService.getAvailableSweets();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testSweet, chocolateSweet);
        verify(sweetRepository).findByIsAvailableTrue();
    }

    @Test
    @DisplayName("Should get sweets by category")
    void testGetSweetsByCategory() {
        // Given
        List<Sweet> chocolateSweets = Arrays.asList(testSweet, chocolateSweet);
        when(sweetRepository.findByCategory(Sweet.SweetCategory.CHOCOLATE)).thenReturn(chocolateSweets);

        // When
        List<Sweet> result = sweetService.getSweetsByCategory(Sweet.SweetCategory.CHOCOLATE);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testSweet, chocolateSweet);
        verify(sweetRepository).findByCategory(Sweet.SweetCategory.CHOCOLATE);
    }

    @Test
    @DisplayName("Should update sweet successfully")
    void testUpdateSweet() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        Sweet updateData = new Sweet();
        updateData.setName("Updated Chocolate");
        updateData.setPrice(new BigDecimal("6.99"));
        updateData.setQuantity(120);

        // When
        Sweet result = sweetService.updateSweet(1L, updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Chocolate");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("6.99"));
        assertThat(result.getQuantity()).isEqualTo(120);
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent sweet")
    void testUpdateNonExistentSweet() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());
        Sweet updateData = new Sweet();

        // When & Then
        assertThatThrownBy(() -> sweetService.updateSweet(999L, updateData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sweet with ID 999 not found");
        verify(sweetRepository).findById(999L);
        verify(sweetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete sweet successfully")
    void testDeleteSweet() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        sweetService.deleteSweet(1L);

        // Then
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).delete(testSweet);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent sweet")
    void testDeleteNonExistentSweet() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sweetService.deleteSweet(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sweet with ID 999 not found");
        verify(sweetRepository).findById(999L);
        verify(sweetRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should search sweets by term")
    void testSearchSweets() {
        // Given
        List<Sweet> searchResults = Arrays.asList(testSweet, chocolateSweet);
        when(sweetRepository.searchSweets("chocolate")).thenReturn(searchResults);

        // When
        List<Sweet> result = sweetService.searchSweets("chocolate");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testSweet, chocolateSweet);
        verify(sweetRepository).searchSweets("chocolate");
    }

    @Test
    @DisplayName("Should get low stock sweets")
    void testGetLowStockSweets() {
        // Given
        List<Sweet> lowStockSweets = Arrays.asList(gummySweet);
        when(sweetRepository.findLowStock()).thenReturn(lowStockSweets);

        // When
        List<Sweet> result = sweetService.getLowStockSweets();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(gummySweet);
        verify(sweetRepository).findLowStock();
    }

    @Test
    @DisplayName("Should update stock quantity")
    void testUpdateStock() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        Sweet result = sweetService.updateStock(1L, 150);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(150);
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    @DisplayName("Should throw exception when updating stock for non-existent sweet")
    void testUpdateStockForNonExistentSweet() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sweetService.updateStock(999L, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sweet with ID 999 not found");
        verify(sweetRepository).findById(999L);
        verify(sweetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reduce stock quantity")
    void testReduceStock() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        boolean result = sweetService.reduceStock(1L, 10);

        // Then
        assertThat(result).isTrue();
        assertThat(testSweet.getQuantity()).isEqualTo(90);
        verify(sweetRepository).findById(1L);
        verify(sweetRepository).save(testSweet);
    }

    @Test
    @DisplayName("Should fail to reduce stock when insufficient quantity")
    void testReduceStockInsufficientQuantity() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        boolean result = sweetService.reduceStock(1L, 150);

        // Then
        assertThat(result).isFalse();
        assertThat(testSweet.getQuantity()).isEqualTo(100); // unchanged
        verify(sweetRepository).findById(1L);
        verify(sweetRepository, never()).save(any());
    }
}