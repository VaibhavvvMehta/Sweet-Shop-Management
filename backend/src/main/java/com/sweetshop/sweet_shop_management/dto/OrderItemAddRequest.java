package com.sweetshop.sweet_shop_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for adding an item to an existing order
 */
@Data
public class OrderItemAddRequest {

    @NotNull(message = "Sweet ID is required")
    @Positive(message = "Sweet ID must be positive")
    private Long sweetId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity cannot exceed 1000")
    private Integer quantity;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}