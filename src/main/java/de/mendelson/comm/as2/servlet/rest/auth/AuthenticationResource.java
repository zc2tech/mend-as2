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

import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.security.PBKDF2;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.logging.Logger;

/**
 * REST resource for authentication operations
 * Handles login, logout, and token refresh
 *
 */
@Path("/auth")
public class AuthenticationResource {

    private static final String ACCESS_TOKEN_COOKIE = "as2_access_token";
    private static final String REFRESH_TOKEN_COOKIE = "as2_refresh_token";
    private static final int ACCESS_TOKEN_MAX_AGE = 15 * 60; // 15 minutes in seconds
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    public AuthenticationResource() {
        System.out.println("AuthenticationResource: Initialized - /auth endpoint should be available");
    }

    /**
     * Get UserManagementAccessDB instance
     */
    private UserManagementAccessDB getUserManagementAccess() {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return null;
        }
        return new UserManagementAccessDB(processing.getDBDriverManager(), logger);
    }

    /**
     * Login with username and password
     * Returns JWT tokens in HttpOnly cookies
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Authentication service not available"))
                        .build();
            }

            // Validate credentials - get user from new user management system
            WebUIUser user = userMgmt.getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Check if user is enabled
            if (!user.isEnabled()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("User account is disabled"))
                        .build();
            }

            // Check password
            boolean passwordValid = PBKDF2.validatePassword(
                    loginRequest.getPassword(),
                    user.getPasswordHash()
            );

            if (!passwordValid) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Update last login timestamp
            try {
                userMgmt.updateLastLogin(loginRequest.getUsername());
            } catch (Exception e) {
                logger.warning("Failed to update last login time: " + e.getMessage());
                // Don't fail login if timestamp update fails
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(loginRequest.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername());

            // Set tokens in HttpOnly cookies
            NewCookie accessCookie = new NewCookie(
                    ACCESS_TOKEN_COOKIE,
                    accessToken,
                    "/",
                    null,
                    null,
                    ACCESS_TOKEN_MAX_AGE,
                    false, // secure (set to true in production with HTTPS)
                    true   // httpOnly
            );

            NewCookie refreshCookie = new NewCookie(
                    REFRESH_TOKEN_COOKIE,
                    refreshToken,
                    "/",
                    null,
                    null,
                    REFRESH_TOKEN_MAX_AGE,
                    false, // secure
                    true   // httpOnly
            );

            return Response.ok(new LoginResponse(user.getId(), loginRequest.getUsername(), user.isMustChangePassword()))
                    .cookie(accessCookie, refreshCookie)
                    .build();

        } catch (Exception e) {
            logger.warning("Login failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Authentication error"))
                    .build();
        }
    }

    /**
     * Logout - clear authentication cookies
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        NewCookie accessCookie = new NewCookie(
                ACCESS_TOKEN_COOKIE,
                "",
                "/",
                null,
                null,
                0,
                false,
                true
        );

        NewCookie refreshCookie = new NewCookie(
                REFRESH_TOKEN_COOKIE,
                "",
                "/",
                null,
                null,
                0,
                false,
                true
        );

        return Response.ok(new MessageResponse("Logged out successfully"))
                .cookie(accessCookie, refreshCookie)
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh(@Context HttpHeaders headers) {
        try {
            Cookie refreshCookie = headers.getCookies().get(REFRESH_TOKEN_COOKIE);
            if (refreshCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Missing refresh token"))
                        .build();
            }

            String refreshToken = refreshCookie.getValue();

            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid refresh token"))
                        .build();
            }

            // Generate new access token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);

            NewCookie accessCookie = new NewCookie(
                    ACCESS_TOKEN_COOKIE,
                    newAccessToken,
                    "/",
                    null,
                    null,
                    ACCESS_TOKEN_MAX_AGE,
                    false,
                    true
            );

            return Response.ok(new MessageResponse("Token refreshed"))
                    .cookie(accessCookie)
                    .build();

        } catch (Exception e) {
            logger.warning("Token refresh failed: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Token refresh failed"))
                    .build();
        }
    }

    // DTOs

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private int id;
        private String username;
        private boolean mustChangePassword;

        public LoginResponse(int id, String username, boolean mustChangePassword) {
            this.id = id;
            this.username = username;
            this.mustChangePassword = mustChangePassword;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isMustChangePassword() {
            return mustChangePassword;
        }

        public void setMustChangePassword(boolean mustChangePassword) {
            this.mustChangePassword = mustChangePassword;
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
