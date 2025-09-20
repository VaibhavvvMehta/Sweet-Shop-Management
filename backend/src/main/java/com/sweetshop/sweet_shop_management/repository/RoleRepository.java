package com.sweetshop.sweet_shop_management.repository;

import com.sweetshop.sweet_shop_management.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 * Provides CRUD operations and custom queries for role management.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     * @param name The role name to search for
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findByName(Role.RoleName name);
    
    /**
     * Check if a role exists with the given name
     * @param name The role name to check
     * @return true if role exists, false otherwise
     */
    boolean existsByName(Role.RoleName name);
}