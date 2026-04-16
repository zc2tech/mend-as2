/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.servlet.rest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Provides JWT token generation and validation for REST API authentication
 * Generates access tokens (15 min) and refresh tokens (7 days)
 *
 */
public class JwtTokenProvider {

    // TODO: Move to secure preferences storage
    private static final String SECRET_KEY = "mendelson-as2-jwt-secret-key-change-this-in-production-32bytes-minimum";
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final SecretKey key;

    public JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate an access token for the given username (normal login, not switched)
     */
    public String generateAccessToken(String username) {
        return generateAccessToken(username, null);
    }

    /**
     * Generate an access token for the given username
     * @param username Username for the token
     * @param switchedByAdmin Username of the admin who performed the switch (null if not switched)
     */
    public String generateAccessToken(String username, String switchedByAdmin) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        var builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "access");

        // Only add switchedByAdmin claim if it's actually a switched session
        if (switchedByAdmin != null && !switchedByAdmin.isEmpty()) {
            builder.claim("switchedByAdmin", switchedByAdmin);
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Generate a refresh token for the given username
     */
    public String generateRefreshToken(String username) {
        return generateRefreshToken(username, null);
    }

    /**
     * Generate a refresh token for the given username
     * @param username Username for the token
     * @param switchedByAdmin Username of the admin who performed the switch (null if not switched)
     */
    public String generateRefreshToken(String username, String switchedByAdmin) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);

        var builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh");

        // Only add switchedByAdmin claim if it's actually a switched session
        if (switchedByAdmin != null && !switchedByAdmin.isEmpty()) {
            builder.claim("switchedByAdmin", switchedByAdmin);
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validate token and return true if valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token was created during admin user switch (impersonation)
     * @return true if this is a switched session
     */
    public boolean isSwitchedByAdmin(String token) {
        String adminUsername = getSwitchedByAdminUsername(token);
        return adminUsername != null && !adminUsername.isEmpty();
    }

    /**
     * Get the username of the admin who performed the user switch
     * @return Admin username if this is a switched session, null otherwise
     */
    public String getSwitchedByAdminUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("switchedByAdmin", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
