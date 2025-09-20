package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.model.Role;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.model.User;
import com.sweetshop.sweet_shop_management.repository.RoleRepository;
import com.sweetshop.sweet_shop_management.repository.SweetRepository;
import com.sweetshop.sweet_shop_management.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for initializing default data in the database.
 * Creates default roles and demo users if they don't exist.
 * Disabled in integration test profile to avoid conflicts.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!integration")  // Exclude from integration tests
public class DataInitializationService {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SweetRepository sweetRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Initializes default data after application startup
     * Creates USER and ADMIN roles, demo users, and sample Indian sweets if they don't exist
     */
    @PostConstruct
    public void initializeData() {
        log.info("Initializing default data...");
        createDefaultRoles();
        createDemoUsers();
        createSampleIndianSweets();
        log.info("Default data initialization completed.");
    }
    
    /**
     * Creates default roles (USER and ADMIN) if they don't exist
     */
    private void createDefaultRoles() {
        // Create USER role if it doesn't exist
        if (!roleRepository.existsByName(Role.RoleName.USER)) {
            Role userRole = new Role(Role.RoleName.USER, "Standard user with basic permissions");
            roleRepository.save(userRole);
            log.info("Created default USER role");
        }
        
        // Create ADMIN role if it doesn't exist
        if (!roleRepository.existsByName(Role.RoleName.ADMIN)) {
            Role adminRole = new Role(Role.RoleName.ADMIN, "Administrator with full system access");
            roleRepository.save(adminRole);
            log.info("Created default ADMIN role");
        }
    }
    
    /**
     * Creates demo users for testing the application
     * Admin: admin@sweetshop.com / password
     * User: user@sweetshop.com / password
     */
    @Transactional
    private void createDemoUsers() {
        log.info("Creating demo users...");
        
        // Create demo admin user
        if (!userRepository.existsByEmail("admin@sweetshop.com")) {
            log.info("Creating demo admin user...");
            User adminUser = new User("admin", "admin@sweetshop.com", passwordEncoder.encode("password"), "Admin", "User");
            
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            // Add role directly to the collection to avoid lazy loading issue
            adminUser.getRoles().add(adminRole);
            
            userRepository.save(adminUser);
            log.info("Created demo admin user: admin@sweetshop.com");
        } else {
            log.info("Demo admin user already exists");
        }
        
        // Create demo regular user
        if (!userRepository.existsByEmail("user@sweetshop.com")) {
            log.info("Creating demo regular user...");
            User demoUser = new User("user", "user@sweetshop.com", passwordEncoder.encode("password"), "Demo", "User");
            
            Role userRole = roleRepository.findByName(Role.RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            
            // Add role directly to the collection to avoid lazy loading issue
            demoUser.getRoles().add(userRole);
            
            userRepository.save(demoUser);
            log.info("Created demo regular user: user@sweetshop.com");
        } else {
            log.info("Demo regular user already exists");
        }
        
        log.info("Demo users creation completed.");
    }

    /**
     * Creates sample Indian sweets for the shop if they don't exist
     * Includes popular Indian sweets with prices in rupees
     */
    @Transactional
    private void createSampleIndianSweets() {
        log.info("Creating sample Indian sweets...");
        
        if (sweetRepository.count() == 0) {
            // Milk-based sweets
            createSweet("Gulab Jamun", "Soft milk dumplings soaked in rose-flavored syrup", 
                Sweet.SweetCategory.MILK_BASED, new BigDecimal("25.00"), 100, "per piece", "Maharaj Sweets");
            
            createSweet("Rasgulla", "Spongy cottage cheese balls in sugar syrup", 
                Sweet.SweetCategory.MILK_BASED, new BigDecimal("20.00"), 150, "per piece", "Bengal Sweets");
            
            createSweet("Ras Malai", "Flattened cottage cheese dumplings in thick milk", 
                Sweet.SweetCategory.MILK_BASED, new BigDecimal("35.00"), 80, "per piece", "Haldiram's");
            
            // Dry fruit sweets
            createSweet("Kaju Katli", "Diamond-shaped cashew fudge with silver coating", 
                Sweet.SweetCategory.DRY_FRUIT, new BigDecimal("450.00"), 50, "250g", "Bikano");
            
            createSweet("Badam Barfi", "Rich almond fudge square", 
                Sweet.SweetCategory.DRY_FRUIT, new BigDecimal("400.00"), 60, "250g", "Haldiram's");
            
            createSweet("Dry Fruit Laddu", "Round sweet balls made with mixed dry fruits", 
                Sweet.SweetCategory.DRY_FRUIT, new BigDecimal("30.00"), 120, "per piece", "Maharaj Sweets");
            
            // Syrup-based sweets
            createSweet("Jalebi", "Crispy spiral shaped sweet soaked in syrup", 
                Sweet.SweetCategory.SYRUP_BASED, new BigDecimal("180.00"), 80, "250g", "Local Sweet Shop");
            
            createSweet("Imarti", "Flower-shaped lentil sweet in orange syrup", 
                Sweet.SweetCategory.SYRUP_BASED, new BigDecimal("200.00"), 70, "250g", "Bikano");
            
            // Flour-based sweets
            createSweet("Besan Laddu", "Traditional round sweet made from gram flour", 
                Sweet.SweetCategory.FLOUR_BASED, new BigDecimal("15.00"), 200, "per piece", "Maharaj Sweets");
            
            createSweet("Motichoor Laddu", "Fine gram flour balls sweet", 
                Sweet.SweetCategory.FLOUR_BASED, new BigDecimal("18.00"), 180, "per piece", "Haldiram's");
            
            createSweet("Boondi Laddu", "Sweet made from small fried gram flour balls", 
                Sweet.SweetCategory.FLOUR_BASED, new BigDecimal("16.00"), 150, "per piece", "Bikano");
            
            // Bengali sweets
            createSweet("Sandesh", "Delicate Bengali sweet made from fresh cottage cheese", 
                Sweet.SweetCategory.BENGALI, new BigDecimal("22.00"), 100, "per piece", "KC Das");
            
            createSweet("Mishti Doi", "Sweet yogurt dessert from Bengal", 
                Sweet.SweetCategory.BENGALI, new BigDecimal("45.00"), 60, "per cup", "Bengal Sweets");
            
            // South Indian sweets
            createSweet("Mysore Pak", "Buttery sweet from Karnataka", 
                Sweet.SweetCategory.SOUTH_INDIAN, new BigDecimal("250.00"), 80, "250g", "Nandini");
            
            createSweet("Coconut Barfi", "Sweet coconut fudge squares", 
                Sweet.SweetCategory.COCONUT_BASED, new BigDecimal("220.00"), 90, "250g", "Local Sweet Shop");
            
            log.info("Sample Indian sweets created successfully!");
        } else {
            log.info("Sweets already exist in database");
        }
    }

    /**
     * Helper method to create a sweet
     */
    private void createSweet(String name, String description, Sweet.SweetCategory category, 
                           BigDecimal price, Integer quantity, String unit, String brand) {
        Sweet sweet = new Sweet();
        sweet.setName(name);
        sweet.setDescription(description);
        sweet.setCategory(category);
        sweet.setPrice(price);
        sweet.setQuantity(quantity);
        sweet.setMinStockLevel(10);
        sweet.setUnit(unit);
        sweet.setBrand(brand);
        sweet.setIsAvailable(true);
        sweet.setCreatedAt(LocalDateTime.now());
        sweet.setUpdatedAt(LocalDateTime.now());
        
        sweetRepository.save(sweet);
        log.debug("Created sweet: {} - ₹{}", name, price);
    }
}