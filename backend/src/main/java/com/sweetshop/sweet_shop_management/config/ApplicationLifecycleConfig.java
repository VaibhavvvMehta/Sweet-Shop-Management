package com.sweetshop.sweet_shop_management.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration to handle application lifecycle and prevent premature shutdown
 */
@Configuration
@Slf4j
public class ApplicationLifecycleConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Sweet Shop Management Application is ready and running!");
        log.info("Backend is now available at http://localhost:8080");
        log.info("Application will continue running until manually stopped.");
    }
}