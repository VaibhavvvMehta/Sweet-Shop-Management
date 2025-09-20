package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.model.Sweet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Sweet entity operations.
 * Defines business logic for sweet/product management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
public interface SweetService {

    /**
     * Create a new sweet
     * @param sweet The sweet to create
     * @return The created sweet
     * @throws IllegalArgumentException if sweet with same name already exists
     */
    Sweet createSweet(Sweet sweet);

    /**
     * Find sweet by ID
     * @param id The sweet ID
     * @return Optional containing the sweet if found
     */
    Optional<Sweet> findById(Long id);

    /**
     * Find sweet by name
     * @param name The sweet name
     * @return Optional containing the sweet if found
     */
    Optional<Sweet> findByName(String name);

    /**
     * Get all sweets
     * @return List of all sweets
     */
    List<Sweet> getAllSweets();

    /**
     * Get all available sweets
     * @return List of available sweets
     */
    List<Sweet> getAvailableSweets();

    /**
     * Get sweets by category
     * @param category The sweet category
     * @return List of sweets in the category
     */
    List<Sweet> getSweetsByCategory(Sweet.SweetCategory category);

    /**
     * Update sweet information
     * @param id The sweet ID
     * @param updateData The data to update
     * @return The updated sweet
     * @throws IllegalArgumentException if sweet not found
     */
    Sweet updateSweet(Long id, Sweet updateData);

    /**
     * Delete sweet by ID
     * @param id The sweet ID
     * @throws IllegalArgumentException if sweet not found
     */
    void deleteSweet(Long id);

    /**
     * Search sweets by name, description, or brand
     * @param searchTerm The search term
     * @return List of matching sweets
     */
    List<Sweet> searchSweets(String searchTerm);

    /**
     * Get sweets with low stock
     * @return List of low stock sweets
     */
    List<Sweet> getLowStockSweets();

    /**
     * Get sweets out of stock
     * @return List of out of stock sweets
     */
    List<Sweet> getOutOfStockSweets();

    /**
     * Get sweets in stock
     * @return List of in stock sweets
     */
    List<Sweet> getInStockSweets();

    /**
     * Get sweets by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of sweets in price range
     */
    List<Sweet> getSweetsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Get sweets by brand
     * @param brand The brand name
     * @return List of sweets from the brand
     */
    List<Sweet> getSweetsByBrand(String brand);

    /**
     * Update stock quantity for a sweet
     * @param id The sweet ID
     * @param quantity New quantity
     * @return The updated sweet
     * @throws IllegalArgumentException if sweet not found
     */
    Sweet updateStock(Long id, Integer quantity);

    /**
     * Reduce stock quantity for a sweet
     * @param id The sweet ID
     * @param amount Amount to reduce
     * @return true if successful, false if insufficient stock
     * @throws IllegalArgumentException if sweet not found
     */
    boolean reduceStock(Long id, Integer amount);

    /**
     * Increase stock quantity for a sweet
     * @param id The sweet ID
     * @param amount Amount to add
     * @return The updated sweet
     * @throws IllegalArgumentException if sweet not found
     */
    Sweet increaseStock(Long id, Integer amount);

    /**
     * Toggle availability of a sweet
     * @param id The sweet ID
     * @return The updated sweet
     * @throws IllegalArgumentException if sweet not found
     */
    Sweet toggleAvailability(Long id);
}