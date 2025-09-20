package com.sweetshop.sweet_shop_management.service.impl;

import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.repository.SweetRepository;
import com.sweetshop.sweet_shop_management.service.SweetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of SweetService interface.
 * Provides business logic for sweet/product management operations.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SweetServiceImpl implements SweetService {

    private final SweetRepository sweetRepository;

    @Override
    public Sweet createSweet(Sweet sweet) {
        log.info("Creating new sweet with name: {}", sweet.getName());
        
        if (sweetRepository.existsByName(sweet.getName())) {
            log.warn("Attempted to create sweet with duplicate name: {}", sweet.getName());
            throw new IllegalArgumentException("Sweet with name '" + sweet.getName() + "' already exists");
        }

        Sweet savedSweet = sweetRepository.save(sweet);
        log.info("Sweet created successfully with ID: {}", savedSweet.getId());
        return savedSweet;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sweet> findById(Long id) {
        log.debug("Finding sweet by ID: {}", id);
        return sweetRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sweet> findByName(String name) {
        log.debug("Finding sweet by name: {}", name);
        return sweetRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getAllSweets() {
        log.debug("Retrieving all sweets");
        return sweetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getAvailableSweets() {
        log.debug("Retrieving available sweets");
        return sweetRepository.findByIsAvailableTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getSweetsByCategory(Sweet.SweetCategory category) {
        log.debug("Retrieving sweets by category: {}", category);
        return sweetRepository.findByCategory(category);
    }

    @Override
    public Sweet updateSweet(Long id, Sweet updateData) {
        log.info("Updating sweet with ID: {}", id);
        
        Sweet existingSweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for update", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        // Update fields if provided in updateData
        if (updateData.getName() != null && !updateData.getName().trim().isEmpty()) {
            existingSweet.setName(updateData.getName().trim());
        }
        if (updateData.getDescription() != null) {
            existingSweet.setDescription(updateData.getDescription().trim());
        }
        if (updateData.getCategory() != null) {
            existingSweet.setCategory(updateData.getCategory());
        }
        if (updateData.getPrice() != null) {
            existingSweet.setPrice(updateData.getPrice());
        }
        if (updateData.getPricingType() != null) {
            existingSweet.setPricingType(updateData.getPricingType());
        }
        if (updateData.getQuantity() != null) {
            existingSweet.setQuantity(updateData.getQuantity());
        }
        if (updateData.getMinStockLevel() != null) {
            existingSweet.setMinStockLevel(updateData.getMinStockLevel());
        }
        if (updateData.getImageUrl() != null) {
            existingSweet.setImageUrl(updateData.getImageUrl().trim());
        }
        if (updateData.getIsAvailable() != null) {
            existingSweet.setIsAvailable(updateData.getIsAvailable());
        }
        if (updateData.getUnit() != null) {
            existingSweet.setUnit(updateData.getUnit().trim());
        }
        if (updateData.getBrand() != null) {
            existingSweet.setBrand(updateData.getBrand().trim());
        }

        Sweet updatedSweet = sweetRepository.save(existingSweet);
        log.info("Sweet updated successfully: {}", updatedSweet.getName());
        return updatedSweet;
    }

    @Override
    public void deleteSweet(Long id) {
        log.info("Deleting sweet with ID: {}", id);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for deletion", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        sweetRepository.delete(sweet);
        log.info("Sweet deleted successfully: {}", sweet.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> searchSweets(String searchTerm) {
        log.debug("Searching sweets with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllSweets();
        }
        return sweetRepository.searchSweets(searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getLowStockSweets() {
        log.debug("Retrieving low stock sweets");
        return sweetRepository.findLowStock();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getOutOfStockSweets() {
        log.debug("Retrieving out of stock sweets");
        return sweetRepository.findOutOfStock();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getInStockSweets() {
        log.debug("Retrieving in stock sweets");
        return sweetRepository.findInStock();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getSweetsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Retrieving sweets by price range: {} - {}", minPrice, maxPrice);
        return sweetRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sweet> getSweetsByBrand(String brand) {
        log.debug("Retrieving sweets by brand: {}", brand);
        return sweetRepository.findByBrandIgnoreCase(brand);
    }

    @Override
    public Sweet updateStock(Long id, Integer quantity) {
        log.info("Updating stock for sweet ID {} to quantity: {}", id, quantity);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for stock update", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        sweet.setQuantity(quantity);
        Sweet updatedSweet = sweetRepository.save(sweet);
        log.info("Stock updated successfully for sweet: {}", updatedSweet.getName());
        return updatedSweet;
    }

    @Override
    public boolean reduceStock(Long id, Integer amount) {
        log.info("Reducing stock for sweet ID {} by amount: {}", id, amount);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for stock reduction", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        if (sweet.reduceQuantity(amount)) {
            sweetRepository.save(sweet);
            log.info("Stock reduced successfully for sweet: {}", sweet.getName());
            return true;
        } else {
            log.warn("Insufficient stock for sweet: {}. Requested: {}, Available: {}", 
                    sweet.getName(), amount, sweet.getQuantity());
            return false;
        }
    }

    @Override
    public Sweet increaseStock(Long id, Integer amount) {
        log.info("Increasing stock for sweet ID {} by amount: {}", id, amount);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for stock increase", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        sweet.increaseQuantity(amount);
        Sweet updatedSweet = sweetRepository.save(sweet);
        log.info("Stock increased successfully for sweet: {}", updatedSweet.getName());
        return updatedSweet;
    }

    @Override
    public Sweet toggleAvailability(Long id) {
        log.info("Toggling availability for sweet ID: {}", id);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sweet with ID {} not found for availability toggle", id);
                    return new IllegalArgumentException("Sweet with ID " + id + " not found");
                });

        sweet.setIsAvailable(!sweet.getIsAvailable());
        Sweet updatedSweet = sweetRepository.save(sweet);
        log.info("Availability toggled successfully for sweet: {} - Available: {}", 
                updatedSweet.getName(), updatedSweet.getIsAvailable());
        return updatedSweet;
    }
}