package com.sweetshop.sweet_shop_management.dto;

import com.sweetshop.sweet_shop_management.model.Sweet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for sweet responses.
 * Contains all sweet information for API responses.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweetResponse {

    private Long id;
    private String name;
    private String description;
    private Sweet.SweetCategory category;
    private BigDecimal price;
    private Integer quantity;
    private Integer minStockLevel;
    private String imageUrl;
    private Boolean isAvailable;
    private String unit;
    private String brand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean inStock;
    private boolean lowStock;

    /**
     * Creates a SweetResponse from a Sweet entity
     * @param sweet The sweet entity
     * @return SweetResponse DTO
     */
    public static SweetResponse fromSweet(Sweet sweet) {
        SweetResponse response = new SweetResponse();
        response.setId(sweet.getId());
        response.setName(sweet.getName());
        response.setDescription(sweet.getDescription());
        response.setCategory(sweet.getCategory());
        response.setPrice(sweet.getPrice());
        response.setQuantity(sweet.getQuantity());
        response.setMinStockLevel(sweet.getMinStockLevel());
        response.setImageUrl(sweet.getImageUrl());
        response.setIsAvailable(sweet.getIsAvailable());
        response.setUnit(sweet.getUnit());
        response.setBrand(sweet.getBrand());
        response.setCreatedAt(sweet.getCreatedAt());
        response.setUpdatedAt(sweet.getUpdatedAt());
        response.setInStock(sweet.isInStock());
        response.setLowStock(sweet.isLowStock());
        return response;
    }
}