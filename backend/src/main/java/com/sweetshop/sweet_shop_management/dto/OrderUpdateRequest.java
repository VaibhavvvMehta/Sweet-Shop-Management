package com.sweetshop.sweet_shop_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for updating an existing order
 */
@Data
public class OrderUpdateRequest {

    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @Email(message = "Customer email must be valid")
    @Size(max = 150, message = "Customer email must not exceed 150 characters")
    private String customerEmail;

    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}