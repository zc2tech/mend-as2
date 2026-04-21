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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.security.LoginRateLimiter;
import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistService;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.UserManagementAccessDB;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.PBKDF2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.sql.Connection;
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
    }

    /**
     * Login with username and password
     * Returns JWT tokens in HttpOnly cookies
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest, @Context HttpServletRequest httpRequest) {
        String remoteAddr = IPWhitelistService.normalizeIP(httpRequest.getRemoteAddr());
        Connection connection = null;

        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Authentication service not available"))
                        .build();
            }

            // Get database connection for rate limiting
            connection = processing.getDBDriverManager().getConnectionWithoutErrorHandling(
                    IDBDriverManager.DB_RUNTIME);
            PreferencesAS2 prefs = new PreferencesAS2(processing.getDBDriverManager());

            // Check if IP is blocked due to rate limiting
            if (LoginRateLimiter.isBlocked(remoteAddr)) {
                long remainingSeconds = LoginRateLimiter.getBlockRemainingSeconds(remoteAddr);
                logger.warning("Blocked WebUI login attempt from " + remoteAddr +
                              " - " + remainingSeconds + "s remaining");
                return Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(new ErrorResponse("Too many failed login attempts. Access temporarily blocked for " +
                                remainingSeconds + " seconds."))
                        .build();
            }

            // Check IP whitelist for WebUI access BEFORE authentication
            // This prevents unauthorized IPs from even attempting login
            boolean webUIWhitelistEnabled = "true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI));
            if (webUIWhitelistEnabled) {
                IPWhitelistService whitelistService = IPWhitelistService.getInstance(processing.getDBDriverManager());

                // Check against global whitelist only at this stage (before we know the user)
                // User-specific whitelist will be checked after authentication
                if (!whitelistService.isAllowedForWebUI(remoteAddr, -1)) {  // -1 = system-wide check only
                    // Log blocked attempt
                    whitelistService.logBlockedAttempt(
                        remoteAddr,
                        "WEBUI",
                        loginRequest.getUsername(),  // attempted username
                        null,  // no partner for WebUI
                        httpRequest.getHeader("User-Agent"),
                        "/auth/login"
                    );

                    logger.warning("IP " + remoteAddr + " blocked by WebUI whitelist for login attempt (user: " +
                                  loginRequest.getUsername() + ")");

                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponse("Access denied"))
                            .build();
                }
            }

            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(), logger);

            // Validate credentials - get user from new user management system
            WebUIUser user = userMgmt.getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, loginRequest.getUsername(),
                        LoginRateLimiter.SOURCE_WEB_UI, httpRequest.getHeader("User-Agent"), connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Check if user is enabled
            if (!user.isEnabled()) {
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, loginRequest.getUsername(),
                        LoginRateLimiter.SOURCE_WEB_UI, httpRequest.getHeader("User-Agent"), connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

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
                // Record failed attempt
                LoginRateLimiter.recordAuthFailure(remoteAddr, loginRequest.getUsername(),
                        LoginRateLimiter.SOURCE_WEB_UI, httpRequest.getHeader("User-Agent"), connection);

                // Check if should block after this failure
                LoginRateLimiter.checkAndBlock(remoteAddr, connection, prefs);

                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Successful login - update last login timestamp
            try {
                userMgmt.updateLastLogin(loginRequest.getUsername());
            } catch (Exception e) {
                logger.warning("Failed to update last login time: " + e.getMessage());
                // Don't fail login if timestamp update fails
            }

            logger.info("Successful WebUI login: user=" + loginRequest.getUsername() +
                       ", ip=" + remoteAddr);

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(loginRequest.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername());

            // Set tokens in HttpOnly cookies
            NewCookie accessCookie = new NewCookie.Builder(ACCESS_TOKEN_COOKIE)
                    .value(accessToken)
                    .path("/")
                    .maxAge(ACCESS_TOKEN_MAX_AGE)
                    .secure(false) // set to true in production with HTTPS
                    .httpOnly(true)
                    .build();

            NewCookie refreshCookie = new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
                    .value(refreshToken)
                    .path("/")
                    .maxAge(REFRESH_TOKEN_MAX_AGE)
                    .secure(false)
                    .httpOnly(true)
                    .build();

            return Response.ok(new LoginResponse(user.getId(), loginRequest.getUsername(), user.isMustChangePassword()))
                    .cookie(accessCookie, refreshCookie)
                    .build();

        } catch (Exception e) {
            logger.warning("Login failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Authentication error"))
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.warning("Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Logout - clear authentication cookies
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        NewCookie accessCookie = new NewCookie.Builder(ACCESS_TOKEN_COOKIE)
                .value("")
                .path("/")
                .maxAge(0)
                .secure(false)
                .httpOnly(true)
                .build();

        NewCookie refreshCookie = new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
                .value("")
                .path("/")
                .maxAge(0)
                .secure(false)
                .httpOnly(true)
                .build();

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

            NewCookie accessCookie = new NewCookie.Builder(ACCESS_TOKEN_COOKIE)
                    .value(newAccessToken)
                    .path("/")
                    .maxAge(ACCESS_TOKEN_MAX_AGE)
                    .secure(false)
                    .httpOnly(true)
                    .build();

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

    /**
     * Switch user - allows admins to impersonate another user
     * Only users with USER_MANAGE permission can switch users
     * Users with switchedByAdmin=true in their token can switch back to admin
     */
    @POST
    @Path("/switch-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response switchUser(SwitchUserRequest request, @Context HttpHeaders headers, @Context SecurityContext securityContext) {
        try {
            // Get current user from access token
            Cookie accessCookie = headers.getCookies().get(ACCESS_TOKEN_COOKIE);
            if (accessCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Not authenticated"))
                        .build();
            }

            String accessToken = accessCookie.getValue();
            if (!jwtTokenProvider.validateToken(accessToken)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid token"))
                        .build();
            }

            String currentUsername = jwtTokenProvider.getUsernameFromToken(accessToken);
            String originalAdminUsername = jwtTokenProvider.getSwitchedByAdminUsername(accessToken);
            boolean isSwitchedByAdmin = (originalAdminUsername != null && !originalAdminUsername.isEmpty());

            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorResponse("Service not available"))
                        .build();
            }

            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(), logger);

            // Validate target user exists
            String targetUsername = request.getTargetUsername();
            if (targetUsername == null || targetUsername.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Target username is required"))
                        .build();
            }

            WebUIUser targetUser = userMgmt.getUserByUsername(targetUsername);
            if (targetUser == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Target user not found"))
                        .build();
            }

            if (!targetUser.isEnabled()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Target user is disabled"))
                        .build();
            }

            // Security check: Determine if this is a "switch back" or a "switch to" operation
            boolean isSwitchingBack = isSwitchedByAdmin; // If current token has switchedByAdmin=true, user is switching back

            if (isSwitchingBack) {
                // Switching back: CRITICAL SECURITY CHECK
                // 1. Validate target user IS the original admin who did the switch
                if (!targetUsername.equals(originalAdminUsername)) {
                    logger.warning("SECURITY: User " + currentUsername + " (switched by " + originalAdminUsername +
                                 ") attempted to switch back to different user " + targetUsername);
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponse("Can only switch back to the original admin user: " + originalAdminUsername))
                            .build();
                }

                // 2. Validate target user still has USER_MANAGE permission (is still an admin)
                java.util.Set<String> targetUserPermissions = userMgmt.getUserPermissions(targetUsername);
                if (targetUserPermissions == null || !targetUserPermissions.contains("USER_MANAGE")) {
                    logger.warning("User " + currentUsername + " attempted to switch back to " + targetUsername +
                                 " who no longer has admin permissions");
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponse("Target user no longer has administrator permissions"))
                            .build();
                }

                // Generate normal tokens (no switchedByAdmin claim) for the admin user
                String newAccessToken = jwtTokenProvider.generateAccessToken(targetUsername, null);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(targetUsername, null);

                NewCookie accessCookie1 = new NewCookie.Builder(ACCESS_TOKEN_COOKIE)
                        .value(newAccessToken)
                        .path("/")
                        .maxAge(ACCESS_TOKEN_MAX_AGE)
                        .secure(false)
                        .httpOnly(true)
                        .build();

                NewCookie refreshCookie = new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
                        .value(newRefreshToken)
                        .path("/")
                        .maxAge(REFRESH_TOKEN_MAX_AGE)
                        .secure(false)
                        .httpOnly(true)
                        .build();

                logger.info("User " + currentUsername + " switched back to admin " + targetUsername);

                return Response.ok(new LoginResponse(targetUser.getId(), targetUser.getUsername(), false))
                        .cookie(accessCookie1, refreshCookie)
                        .build();
            } else {
                // Switching to another user: Check if current user has USER_MANAGE permission
                java.util.Set<String> currentUserPermissions = userMgmt.getUserPermissions(currentUsername);
                if (currentUserPermissions == null || !currentUserPermissions.contains("USER_MANAGE")) {
                    logger.warning("User " + currentUsername + " attempted to switch users without USER_MANAGE permission");
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponse("You do not have permission to switch users"))
                            .build();
                }

                // Generate new tokens for target user with switchedByAdmin claim containing admin username
                String newAccessToken = jwtTokenProvider.generateAccessToken(targetUsername, currentUsername);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(targetUsername, currentUsername);

                NewCookie accessCookie1 = new NewCookie.Builder(ACCESS_TOKEN_COOKIE)
                        .value(newAccessToken)
                        .path("/")
                        .maxAge(ACCESS_TOKEN_MAX_AGE)
                        .secure(false)
                        .httpOnly(true)
                        .build();

                NewCookie refreshCookie = new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
                        .value(newRefreshToken)
                        .path("/")
                        .maxAge(REFRESH_TOKEN_MAX_AGE)
                        .secure(false)
                        .httpOnly(true)
                        .build();

                logger.info("Admin " + currentUsername + " switched to user " + targetUsername);

                return Response.ok(new LoginResponse(targetUser.getId(), targetUser.getUsername(), false))
                        .cookie(accessCookie1, refreshCookie)
                        .build();
            }

        } catch (Exception e) {
            logger.warning("Switch user failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Switch user failed: " + e.getMessage()))
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

    public static class SwitchUserRequest {
        private String targetUsername;

        public SwitchUserRequest() {
        }

        public String getTargetUsername() {
            return targetUsername;
        }

        public void setTargetUsername(String targetUsername) {
            this.targetUsername = targetUsername;
        }
    }
}
