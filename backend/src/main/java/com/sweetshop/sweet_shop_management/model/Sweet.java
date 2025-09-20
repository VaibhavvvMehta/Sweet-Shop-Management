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
 * Sweet entity representing sweets/candies in the shop inventory.
 * Contains product information, pricing, and stock management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Entity
@Table(name = "sweets")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Sweet {
    
    /**
     * Primary key for the sweet entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    /**
     * Name of the sweet product
     * Must be unique and not blank
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Sweet name is required")
    @Size(min = 2, max = 100, message = "Sweet name must be between 2 and 100 characters")
    private String name;
    
    /**
     * Detailed description of the sweet
     */
    @Column(length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Category of the sweet (e.g., Milk-Based, Dry Fruit, Syrup-Based)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SweetCategory category;
    
    /**
     * Price per unit of the sweet
     * Must be positive
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;
    
    /**
     * Pricing type - whether price is per item or per kilogram
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false, length = 10)
    @NotNull(message = "Pricing type is required")
    private PricingType pricingType = PricingType.PER_ITEM;
    
    /**
     * Current quantity in stock
     * Cannot be negative
     */
    @Column(nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    /**
     * Minimum stock level for reorder alerts
     */
    @Column(name = "min_stock_level")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel = 10;
    
    /**
     * URL or path to the sweet's image
     */
    @Column(name = "image_url")
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;
    
    /**
     * Whether the sweet is currently available for purchase
     */
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    /**
     * Weight or size of the sweet (e.g., "100g", "1 piece")
     */
    @Column(length = 50)
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;
    
    /**
     * Brand or manufacturer of the sweet
     */
    @Column(length = 100)
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;
    
    /**
     * Timestamp when the sweet was added to inventory
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the sweet information was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Constructor with essential fields
     * @param name Name of the sweet
     * @param category Category of the sweet
     * @param price Price per unit
     * @param quantity Initial quantity in stock
     */
    public Sweet(String name, SweetCategory category, BigDecimal price, Integer quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.pricingType = PricingType.PER_ITEM; // Default to per item
        this.quantity = quantity;
        this.isAvailable = true;
        this.minStockLevel = 10;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with all main fields
     * @param name Name of the sweet
     * @param description Description of the sweet
     * @param category Category of the sweet
     * @param price Price per unit
     * @param pricingType Pricing type (per item or per kg)
     * @param quantity Initial quantity in stock
     * @param unit Unit description
     * @param brand Brand name
     */
    public Sweet(String name, String description, SweetCategory category, 
                BigDecimal price, PricingType pricingType, Integer quantity, String unit, String brand) {
        this(name, category, price, quantity);
        this.description = description;
        this.pricingType = pricingType;
        this.unit = unit;
        this.brand = brand;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
        if (this.minStockLevel == null) {
            this.minStockLevel = 10;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the sweet is in stock
     * @return true if quantity > 0, false otherwise
     */
    public boolean isInStock() {
        return this.quantity != null && this.quantity > 0;
    }
    
    /**
     * Checks if the sweet stock is below minimum level
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock() {
        return this.quantity != null && this.minStockLevel != null && 
               this.quantity <= this.minStockLevel;
    }
    
    /**
     * Reduces the quantity by the specified amount
     * @param amount Amount to reduce
     * @return true if successful, false if insufficient stock
     */
    public boolean reduceQuantity(int amount) {
        if (this.quantity != null && this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Increases the quantity by the specified amount
     * @param amount Amount to add
     */
    public void increaseQuantity(int amount) {
        if (this.quantity == null) {
            this.quantity = amount;
        } else {
            this.quantity += amount;
        }
    }
    
    /**
     * Enum defining pricing types for sweets
     */
    public enum PricingType {
        /**
         * Price is per individual item/piece
         */
        PER_ITEM("Per Item"),
        
        /**
         * Price is per kilogram
         */
        PER_KG("Per Kg");
        
        private final String displayName;
        
        PricingType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enum defining the available sweet categories for Indian Sweet Shop
     */
    public enum SweetCategory {
        /**
         * Milk-based sweets like Gulab Jamun, Rasgulla
         */
        MILK_BASED("Milk-Based Sweets"),
        
        /**
         * Dry fruit and nut sweets like Badam Barfi, Kaju Katli
         */
        DRY_FRUIT("Dry Fruit Sweets"),
        
        /**
         * Syrup-based sweets like Jalebi, Imarti
         */
        SYRUP_BASED("Syrup-Based Sweets"),
        
        /**
         * Flour-based sweets like Besan Laddu, Motichoor Laddu
         */
        FLOUR_BASED("Flour-Based Sweets"),
        
        /**
         * Rice and grain-based sweets like Kheer, Payasam
         */
        GRAIN_BASED("Grain-Based Sweets"),
        
        /**
         * Coconut-based sweets like Coconut Barfi, Modak
         */
        COCONUT_BASED("Coconut-Based Sweets"),
        
        /**
         * Festival special sweets
         */
        FESTIVAL_SPECIAL("Festival Special"),
        
        /**
         * Bengali sweets like Mishti Doi, Sandesh
         */
        BENGALI("Bengali Sweets"),
        
        /**
         * South Indian sweets like Mysore Pak, Halwa
         */
        SOUTH_INDIAN("South Indian Sweets"),
        
        /**
         * Sugar-free and diet sweets
         */
        SUGAR_FREE("Sugar-Free"),
        
        /**
         * Other traditional sweets
         */
        OTHER("Other");
        
        private final String displayName;
        
        SweetCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}