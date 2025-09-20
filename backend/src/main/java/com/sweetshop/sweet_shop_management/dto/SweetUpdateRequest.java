package com.sweetshop.sweet_shop_management.dto;

import com.sweetshop.sweet_shop_management.model.Sweet;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating a sweet.
 * All fields are optional for partial updates.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweetUpdateRequest {

    @Size(min = 2, max = 100, message = "Sweet name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Sweet.SweetCategory category;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    private Sweet.PricingType pricingType;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    private Boolean isAvailable;

    /**
     * Converts the DTO to a Sweet entity for update operations
     * @return Sweet entity with only the fields to be updated
     */
    public Sweet toSweet() {
        Sweet sweet = new Sweet();
        sweet.setName(this.name);
        sweet.setDescription(this.description);
        sweet.setCategory(this.category);
        sweet.setPrice(this.price);
        sweet.setPricingType(this.pricingType);
        sweet.setQuantity(this.quantity);
        sweet.setMinStockLevel(this.minStockLevel);
        sweet.setImageUrl(this.imageUrl);
        sweet.setUnit(this.unit);
        sweet.setBrand(this.brand);
        sweet.setIsAvailable(this.isAvailable);
        return sweet;
    }
}