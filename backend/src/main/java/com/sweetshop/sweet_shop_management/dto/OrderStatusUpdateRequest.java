package com.sweetshop.sweet_shop_management.dto;

import com.sweetshop.sweet_shop_management.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating order status
 */
@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Order.OrderStatus status;

    private String notes;
}