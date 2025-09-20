package com.sweetshop.sweet_shop_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing customer orders in the sweet shop.
 * Contains order information, customer details, and order items.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "orderItems")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    /**
     * Unique identifier for the order
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Customer's name
     */
    @Column(name = "customer_name", nullable = false, length = 100)
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    /**
     * Customer's email address
     */
    @Column(name = "customer_email", nullable = false, length = 150)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 150, message = "Customer email must not exceed 150 characters")
    private String customerEmail;

    /**
     * Customer's phone number
     */
    @Column(name = "customer_phone", length = 20)
    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    /**
     * Delivery or pickup address
     */
    @Column(name = "delivery_address", columnDefinition = "TEXT")
    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;

    /**
     * Current status of the order
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Order status is required")
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Total amount for the order (calculated from order items)
     */
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be greater than or equal to 0")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Date and time when the order was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date and time when the order was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Date and time when the order was completed
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Special instructions or notes for the order
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    /**
     * List of items in this order
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Set timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == OrderStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    /**
     * Add an order item to this order
     * 
     * @param orderItem the order item to add
     */
    public void addOrderItem(OrderItem orderItem) {
        if (orderItem != null) {
            orderItems.add(orderItem);
            orderItem.setOrder(this);
            recalculateTotal();
        }
    }

    /**
     * Remove an order item from this order
     * 
     * @param orderItem the order item to remove
     */
    public void removeOrderItem(OrderItem orderItem) {
        if (orderItem != null) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
            recalculateTotal();
        }
    }

    /**
     * Recalculate the total amount based on order items
     */
    public void recalculateTotal() {
        if (orderItems == null || orderItems.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
        } else {
            this.totalAmount = orderItems.stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    /**
     * Check if the order can be cancelled
     * 
     * @return true if the order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * Check if the order can be modified
     * 
     * @return true if the order can be modified
     */
    public boolean canBeModified() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Get the total number of items in the order
     * 
     * @return total quantity of items
     */
    public Integer getTotalQuantity() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Enum representing the possible states of an order
     */
    public enum OrderStatus {
        /**
         * Order has been created but not yet confirmed
         */
        PENDING("Pending"),
        
        /**
         * Order has been confirmed and is being prepared
         */
        CONFIRMED("Confirmed"),
        
        /**
         * Order is being prepared/processed
         */
        PREPARING("Preparing"),
        
        /**
         * Order is ready for pickup or delivery
         */
        READY("Ready"),
        
        /**
         * Order is out for delivery
         */
        OUT_FOR_DELIVERY("Out for Delivery"),
        
        /**
         * Order has been delivered to customer
         */
        DELIVERED("Delivered"),
        
        /**
         * Order has been completed
         */
        COMPLETED("Completed"),
        
        /**
         * Order has been cancelled
         */
        CANCELLED("Cancelled");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Get the display name for this status
         * 
         * @return the display name
         */
        public String getDisplayName() {
            return displayName;
        }
    }
}