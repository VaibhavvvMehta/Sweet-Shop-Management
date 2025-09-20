package com.sweetshop.sweet_shop_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing users in the Sweet Shop Management System.
 * Contains user authentication and profile information.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"roles"})
public class User {
    
    /**
     * Primary key for the user entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    /**
     * Unique username for user authentication
     * Must be between 3 and 50 characters
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * User's email address
     * Must be unique and valid email format
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * Hashed password for user authentication
     * Stored as BCrypt hash
     */
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * User's first name
     */
    @Column(name = "first_name")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    /**
     * User's last name
     */
    @Column(name = "last_name")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    /**
     * Phone number of the user
     */
    @Column(name = "phone_number")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    /**
     * Whether the user account is active
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    /**
     * Timestamp when the user account was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user account was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Many-to-many relationship with Role entity
     * Roles assigned to this user
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    /**
     * Constructor with username, email, and password
     * @param username The username
     * @param email The email address
     * @param password The password (will be hashed)
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with basic user information
     * @param username The username
     * @param email The email address
     * @param password The password (will be hashed)
     * @param firstName The first name
     * @param lastName The last name
     */
    public User(String username, String email, String password, String firstName, String lastName) {
        this(username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Adds a role to this user
     * @param role The role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }
    
    /**
     * Removes a role from this user
     * @param role The role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
    
    /**
     * Gets the user's full name
     * @return Concatenated first and last name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
}