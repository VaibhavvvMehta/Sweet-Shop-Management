package com.sweetshop.sweet_shop_management.repository;

import com.sweetshop.sweet_shop_management.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sweet entity operations.
 * Provides CRUD operations and custom queries for sweet/candy management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Repository
public interface SweetRepository extends JpaRepository<Sweet, Long> {
    
    /**
     * Find sweet by name
     * @param name The sweet name to search for
     * @return Optional containing the sweet if found, empty otherwise
     */
    Optional<Sweet> findByName(String name);
    
    /**
     * Check if a sweet exists with the given name
     * @param name The sweet name to check
     * @return true if sweet exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Find all available sweets
     * @return List of available sweets
     */
    List<Sweet> findByIsAvailableTrue();
    
    /**
     * Find all sweets by category
     * @param category The category to search for
     * @return List of sweets in the specified category
     */
    List<Sweet> findByCategory(Sweet.SweetCategory category);
    
    /**
     * Find all available sweets by category
     * @param category The category to search for
     * @return List of available sweets in the specified category
     */
    List<Sweet> findByCategoryAndIsAvailableTrue(Sweet.SweetCategory category);
    
    /**
     * Find sweets in stock (quantity > 0)
     * @return List of sweets with quantity greater than 0
     */
    @Query("SELECT s FROM Sweet s WHERE s.quantity > 0")
    List<Sweet> findInStock();
    
    /**
     * Find sweets with low stock (quantity <= minStockLevel)
     * @return List of sweets with low stock
     */
    @Query("SELECT s FROM Sweet s WHERE s.quantity <= s.minStockLevel")
    List<Sweet> findLowStock();
    
    /**
     * Find sweets out of stock (quantity = 0)
     * @return List of sweets that are out of stock
     */
    @Query("SELECT s FROM Sweet s WHERE s.quantity = 0")
    List<Sweet> findOutOfStock();
    
    /**
     * Find sweets by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of sweets within the price range
     */
    List<Sweet> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find sweets by brand
     * @param brand The brand to search for
     * @return List of sweets from the specified brand
     */
    List<Sweet> findByBrandIgnoreCase(String brand);
    
    /**
     * Search sweets by name or description containing the search term
     * @param searchTerm The term to search for
     * @return List of matching sweets
     */
    @Query("SELECT s FROM Sweet s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Sweet> searchSweets(@Param("searchTerm") String searchTerm);
    
    /**
     * Search available sweets by name or description containing the search term
     * @param searchTerm The term to search for
     * @return List of matching available sweets
     */
    @Query("SELECT s FROM Sweet s WHERE s.isAvailable = true AND (" +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Sweet> searchAvailableSweets(@Param("searchTerm") String searchTerm);
    
    /**
     * Find sweets ordered by name ascending
     * @return List of sweets ordered by name
     */
    List<Sweet> findAllByOrderByNameAsc();
    
    /**
     * Find sweets ordered by price ascending
     * @return List of sweets ordered by price
     */
    List<Sweet> findAllByOrderByPriceAsc();
    
    /**
     * Find sweets ordered by price descending
     * @return List of sweets ordered by price (highest first)
     */
    List<Sweet> findAllByOrderByPriceDesc();
    
    /**
     * Find sweets ordered by quantity descending
     * @return List of sweets ordered by quantity (highest stock first)
     */
    List<Sweet> findAllByOrderByQuantityDesc();
}