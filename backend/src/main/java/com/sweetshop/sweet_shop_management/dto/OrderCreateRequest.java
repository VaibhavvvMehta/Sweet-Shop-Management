package com.sweetshop.sweet_shop_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for creating a new order
 */
@Data
public class OrderCreateRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 150, message = "Customer email must not exceed 150 characters")
    private String customerEmail;

    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemCreateRequest> items = new ArrayList<>();

    /**
     * DTO for creating order items within an order
     */
    @Data
    public static class OrderItemCreateRequest {

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
}