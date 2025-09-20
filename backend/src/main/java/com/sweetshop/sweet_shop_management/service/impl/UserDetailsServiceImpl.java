package com.sweetshop.sweet_shop_management.service.impl;

import com.sweetshop.sweet_shop_management.model.User;
import com.sweetshop.sweet_shop_management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * This service loads user details from the database for authentication purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details by username for Spring Security authentication.
     *
     * @param username The username (email in our case)
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException If user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    logger.error("User not found with username or email: {}", username);
                    return new UsernameNotFoundException("User not found with username or email: " + username);
                });

        logger.debug("Found user: {} with {} roles", user.getUsername(), user.getRoles().size());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getIsActive())
                .authorities(getAuthorities(user))
                .build();
    }

    /**
     * Converts user roles to Spring Security authorities.
     *
     * @param user The user entity
     * @return Collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = "ROLE_" + role.getName().name();
                    logger.debug("Adding authority: {}", roleName);
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());
        
        logger.debug("User {} has authorities: {}", user.getUsername(), authorities);
        return authorities;
    }
}