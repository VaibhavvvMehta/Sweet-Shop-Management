package com.sweetshop.sweet_shop_management.integration.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.sweetshop.sweet_shop_management.service.DataInitializationService;

/**
 * Test configuration for integration tests.
 * Excludes DataInitializationService to avoid conflicts with test setup.
 */
@SpringBootApplication
@EntityScan("com.sweetshop.sweet_shop_management.model")
@EnableJpaRepositories("com.sweetshop.sweet_shop_management.repository")
@ComponentScan(
    basePackages = "com.sweetshop.sweet_shop_management",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = DataInitializationService.class
    )
)
public class IntegrationTestConfig {
}