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

/**
 * OrderItem entity representing individual items within an order.
 * Links orders to specific sweets with quantities and pricing.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "order")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderItem {

    /**
     * Unique identifier for the order item
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The order this item belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    private Order order;

    /**
     * The sweet being ordered
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sweet_id", nullable = false)
    @NotNull(message = "Sweet is required")
    private Sweet sweet;

    /**
     * Quantity of this sweet being ordered
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity cannot exceed 1000")
    private Integer quantity;

    /**
     * Unit price of the sweet at the time of order (for price history)
     */
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    /**
     * Subtotal for this item (quantity * unit price)
     */
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
    private BigDecimal subtotal;

    /**
     * Date and time when the order item was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date and time when the order item was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Special notes or customizations for this item
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    /**
     * Set timestamps and calculate subtotal before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateSubtotal();
    }

    /**
     * Update timestamp and recalculate subtotal before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateSubtotal();
    }

    /**
     * Calculate the subtotal for this order item
     */
    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    /**
     * Set the quantity and recalculate subtotal
     * 
     * @param quantity the new quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    /**
     * Set the unit price and recalculate subtotal
     * 
     * @param unitPrice the new unit price
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    /**
     * Update both quantity and unit price, then recalculate subtotal
     * 
     * @param quantity the new quantity
     * @param unitPrice the new unit price
     */
    public void updateQuantityAndPrice(Integer quantity, BigDecimal unitPrice) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    /**
     * Get the sweet name for convenience
     * 
     * @return the name of the sweet
     */
    public String getSweetName() {
        return sweet != null ? sweet.getName() : null;
    }

    /**
     * Get the sweet category for convenience
     * 
     * @return the category of the sweet
     */
    public Sweet.SweetCategory getSweetCategory() {
        return sweet != null ? sweet.getCategory() : null;
    }

    /**
     * Check if this order item has the same sweet as another order item
     * 
     * @param other the other order item to compare
     * @return true if both items have the same sweet
     */
    public boolean hasSameSweet(OrderItem other) {
        if (other == null || this.sweet == null || other.sweet == null) {
            return false;
        }
        return this.sweet.getId().equals(other.sweet.getId());
    }

    /**
     * Combine this order item with another order item (same sweet)
     * by adding quantities together
     * 
     * @param other the other order item to combine with
     * @throws IllegalArgumentException if the items don't have the same sweet
     */
    public void combineWith(OrderItem other) {
        if (!hasSameSweet(other)) {
            throw new IllegalArgumentException("Cannot combine order items with different sweets");
        }
        
        this.quantity += other.quantity;
        calculateSubtotal();
    }
}