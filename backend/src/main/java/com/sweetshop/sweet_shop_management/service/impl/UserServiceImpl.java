package com.sweetshop.sweet_shop_management.service.impl;

import com.sweetshop.sweet_shop_management.model.User;
import com.sweetshop.sweet_shop_management.repository.UserRepository;
import com.sweetshop.sweet_shop_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService for user management operations.
 * Provides user profile management and user-related queries.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public List<User> getAllActiveUsers() {
        log.debug("Getting all active users");
        return userRepository.findByIsActiveTrue();
    }
    
    @Override
    public List<User> searchUsers(String searchTerm) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm);
    }
}