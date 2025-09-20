package com.sweetshop.sweet_shop_management.dto;

import com.sweetshop.sweet_shop_management.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response
 */
@Data
public class OrderResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String notes;
    private Integer totalQuantity;
    private List<OrderItemResponse> items;

    /**
     * DTO for order item response
     */
    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long sweetId;
        private String sweetName;
        private String sweetCategory;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private String notes;
        private LocalDateTime createdAt;
    }
}