package com.sweetshop.sweet_shop_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Sweet Shop Management System.
 * Provides comprehensive API documentation with security schemes.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * Configure OpenAPI documentation
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI sweetShopOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    /**
     * API information configuration
     * 
     * @return API info
     */
    private Info getApiInfo() {
        return new Info()
                .title("Sweet Shop Management System API")
                .description("A comprehensive REST API for managing a sweet shop with features for product management, " +
                           "order processing, user authentication, and inventory tracking. " +
                           "Built with Spring Boot, Spring Security, and JPA.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Sweet Shop Management Team")
                        .email("support@sweetshop.com")
                        .url("https://sweetshop.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Server configuration for API documentation
     * 
     * @return List of servers
     */
    private List<Server> getServers() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort + contextPath)
                .description("Local Development Server");

        Server productionServer = new Server()
                .url("https://api.sweetshop.com")
                .description("Production Server");

        return List.of(localServer, productionServer);
    }

    /**
     * JWT Bearer token security scheme
     * 
     * @return Security scheme for JWT
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token for API authentication. " +
                           "Obtain this token by logging in through the /api/v1/auth/login endpoint. " +
                           "Include it in the Authorization header as 'Bearer {token}'.");
    }
}