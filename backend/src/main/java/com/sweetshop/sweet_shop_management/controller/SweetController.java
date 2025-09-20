package com.sweetshop.sweet_shop_management.controller;

import com.sweetshop.sweet_shop_management.dto.*;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.service.SweetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Sweet/Product management operations.
 * Provides endpoints for CRUD operations, search, and inventory management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/sweets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Sweet Management", description = "APIs for managing sweets/products including CRUD operations, search, and inventory management")
@SecurityRequirement(name = "Bearer Authentication")
public class SweetController {

    private final SweetService sweetService;

    @Operation(
        summary = "Create a new sweet",
        description = "Creates a new sweet/product with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sweet created successfully",
                content = @Content(schema = @Schema(implementation = SweetResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @PostMapping
    public ResponseEntity<SweetResponse> createSweet(
            @Parameter(description = "Sweet creation request")
            @Valid @RequestBody SweetCreateRequest request) {
        log.info("Creating new sweet: {}", request.getName());
        
        try {
            Sweet sweet = sweetService.createSweet(request.toSweet());
            SweetResponse response = SweetResponse.fromSweet(sweet);
            
            log.info("Sweet created successfully with ID: {}", sweet.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create sweet: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(
        summary = "Get sweet by ID",
        description = "Retrieves a specific sweet by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sweet found",
                content = @Content(schema = @Schema(implementation = SweetResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Sweet not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SweetResponse> getSweetById(
            @Parameter(description = "Sweet ID", example = "1")
            @PathVariable Long id) {
        log.debug("Fetching sweet with ID: {}", id);
        
        return sweetService.findById(id)
                .map(sweet -> {
                    SweetResponse response = SweetResponse.fromSweet(sweet);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Get all sweets",
        description = "Retrieves all sweets in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sweets retrieved successfully",
                content = @Content(schema = @Schema(implementation = SweetResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping
    public ResponseEntity<List<SweetResponse>> getAllSweets() {
        log.debug("Fetching all sweets");
        
        List<Sweet> sweets = sweetService.getAllSweets();
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get available sweets",
        description = "Retrieves all sweets that are currently available (in stock)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available sweets retrieved successfully",
                content = @Content(schema = @Schema(implementation = SweetResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/available")
    public ResponseEntity<List<SweetResponse>> getAvailableSweets() {
        log.debug("Fetching available sweets");
        
        List<Sweet> sweets = sweetService.getAvailableSweets();
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get sweets by category
     * GET /api/v1/sweets/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SweetResponse>> getSweetsByCategory(@PathVariable Sweet.SweetCategory category) {
        log.debug("Fetching sweets by category: {}", category);
        
        List<Sweet> sweets = sweetService.getSweetsByCategory(category);
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Search sweets by term
     * GET /api/v1/sweets/search?term={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<SweetResponse>> searchSweets(@RequestParam String term) {
        log.debug("Searching sweets with term: {}", term);
        
        List<Sweet> sweets = sweetService.searchSweets(term);
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get sweets by price range
     * GET /api/v1/sweets/price-range?min={minPrice}&max={maxPrice}
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<SweetResponse>> getSweetsByPriceRange(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        log.debug("Fetching sweets by price range: {} - {}", min, max);
        
        List<Sweet> sweets = sweetService.getSweetsByPriceRange(min, max);
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get sweets by brand
     * GET /api/v1/sweets/brand/{brand}
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<SweetResponse>> getSweetsByBrand(@PathVariable String brand) {
        log.debug("Fetching sweets by brand: {}", brand);
        
        List<Sweet> sweets = sweetService.getSweetsByBrand(brand);
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get low stock sweets
     * GET /api/v1/sweets/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<SweetResponse>> getLowStockSweets() {
        log.debug("Fetching low stock sweets");
        
        List<Sweet> sweets = sweetService.getLowStockSweets();
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get out of stock sweets
     * GET /api/v1/sweets/out-of-stock
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<SweetResponse>> getOutOfStockSweets() {
        log.debug("Fetching out of stock sweets");
        
        List<Sweet> sweets = sweetService.getOutOfStockSweets();
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Get in stock sweets
     * GET /api/v1/sweets/in-stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<SweetResponse>> getInStockSweets() {
        log.debug("Fetching in stock sweets");
        
        List<Sweet> sweets = sweetService.getInStockSweets();
        List<SweetResponse> responses = sweets.stream()
                .map(SweetResponse::fromSweet)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    /**
     * Update sweet
     * PUT /api/v1/sweets/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<SweetResponse> updateSweet(
            @PathVariable Long id, @Valid @RequestBody SweetUpdateRequest request) {
        log.info("Updating sweet with ID: {}", id);
        
        try {
            Sweet updatedSweet = sweetService.updateSweet(id, request.toSweet());
            SweetResponse response = SweetResponse.fromSweet(updatedSweet);
            
            log.info("Sweet updated successfully: {}", updatedSweet.getName());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update sweet: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Delete sweet
     * DELETE /api/v1/sweets/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        log.info("Deleting sweet with ID: {}", id);
        
        try {
            sweetService.deleteSweet(id);
            log.info("Sweet deleted successfully");
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete sweet: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Update stock quantity
     * PUT /api/v1/sweets/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<SweetResponse> updateStock(
            @PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        log.info("Updating stock for sweet ID {} to quantity: {}", id, request.getQuantity());
        
        try {
            Sweet updatedSweet = sweetService.updateStock(id, request.getQuantity());
            SweetResponse response = SweetResponse.fromSweet(updatedSweet);
            
            log.info("Stock updated successfully for sweet: {}", updatedSweet.getName());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update stock: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Reduce stock quantity
     * PUT /api/v1/sweets/{id}/stock/reduce/{amount}
     */
    @PutMapping("/{id}/stock/reduce/{amount}")
    public ResponseEntity<Map<String, Object>> reduceStock(
            @PathVariable Long id, @PathVariable Integer amount) {
        log.info("Reducing stock for sweet ID {} by amount: {}", id, amount);
        
        try {
            boolean success = sweetService.reduceStock(id, amount);
            
            if (success) {
                log.info("Stock reduced successfully");
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Stock reduced successfully"
                ));
            } else {
                log.warn("Insufficient stock for reduction");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Insufficient stock"
                ));
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to reduce stock: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Increase stock quantity
     * PUT /api/v1/sweets/{id}/stock/increase/{amount}
     */
    @PutMapping("/{id}/stock/increase/{amount}")
    public ResponseEntity<SweetResponse> increaseStock(
            @PathVariable Long id, @PathVariable Integer amount) {
        log.info("Increasing stock for sweet ID {} by amount: {}", id, amount);
        
        try {
            Sweet updatedSweet = sweetService.increaseStock(id, amount);
            SweetResponse response = SweetResponse.fromSweet(updatedSweet);
            
            log.info("Stock increased successfully for sweet: {}", updatedSweet.getName());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to increase stock: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Toggle sweet availability
     * PUT /api/v1/sweets/{id}/toggle-availability
     */
    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<SweetResponse> toggleAvailability(@PathVariable Long id) {
        log.info("Toggling availability for sweet ID: {}", id);
        
        try {
            Sweet updatedSweet = sweetService.toggleAvailability(id);
            SweetResponse response = SweetResponse.fromSweet(updatedSweet);
            
            log.info("Availability toggled successfully for sweet: {} - Available: {}", 
                    updatedSweet.getName(), updatedSweet.getIsAvailable());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to toggle availability: {}", e.getMessage());
            throw e;
        }
    }
}