package com.sweetshop.sweet_shop_management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class for token generation, validation, and parsing.
 * Handles JWT token lifecycle operations for authentication.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    // JWT secret key for signing tokens
    @Value("${app.jwt.secret:mySecretKey12345678901234567890123456789012345678901234567890}")
    private String jwtSecret;

    // JWT expiration time in hours
    @Value("${app.jwt.expiration:24}")
    private int jwtExpirationHours;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for a user
     * @param username Username for token
     * @param email User email
     * @param roles User roles
     * @return Generated JWT token
     */
    public String generateToken(String username, String email, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", roles);
        return createToken(claims, username);
    }

    /**
     * Create JWT token with claims and subject
     * @param claims Token claims
     * @param subject Token subject (username)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token
     * @param token JWT token
     * @return Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract email from JWT token
     * @param token JWT token
     * @return Email
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> (String) claims.get("email"));
    }

    /**
     * Extract roles from JWT token
     * @param token JWT token
     * @return Roles string
     */
    public String extractRoles(String token) {
        return extractClaim(token, claims -> (String) claims.get("roles"));
    }

    /**
     * Extract expiration date from JWT token
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @return Extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * @param token JWT token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if JWT token is expired
     * @param token JWT token
     * @return true if expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token against user details
     * @param token JWT token
     * @param userDetails User details
     * @return true if valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token
     * @param token JWT token
     * @return true if valid
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}