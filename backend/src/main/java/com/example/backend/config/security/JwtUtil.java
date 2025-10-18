package com.example.backend.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key for signing (in a real project put this in application.properties or env variable!)
    private final Key key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    // Generate JWT from username
    public String generateToken(String username) {
        // Token validity: 24 hours
        long expirationMs = 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .setSubject(username)                     // the payload (user identifier)
                .setIssuedAt(new Date())                  // when it was created
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // when it expires
                .signWith(key)                            // signing
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    // Validate token (expiration + signature)
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && validateToken(token);
    }


    // Internal helper to parse JWT
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}

