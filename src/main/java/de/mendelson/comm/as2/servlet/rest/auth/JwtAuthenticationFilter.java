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
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JAX-RS filter that validates JWT tokens from HttpOnly cookies
 * and checks user permissions for protected operations
 * Allows unauthenticated access to /auth endpoints
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private static final String ACCESS_TOKEN_COOKIE = "as2_access_token";
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Allow authentication endpoints without token
        if (path.startsWith("auth/")) {
            return;
        }

        // Extract token from cookie
        Cookie cookie = requestContext.getCookies().get(ACCESS_TOKEN_COOKIE);
        if (cookie == null) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Missing authentication token\"}")
                            .build()
            );
            return;
        }

        String token = cookie.getValue();

        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Invalid or expired token\"}")
                            .build()
            );
            return;
        }

        // Get username from token
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Check permissions for protected operations
        String requiredPermission = getRequiredPermission(method, path);
        if (requiredPermission != null) {
            if (!checkUserPermission(username, requiredPermission)) {
                LOGGER.log(Level.WARNING, "User {0} attempted to access {1} {2} without permission {3}",
                        new Object[]{username, method, path, requiredPermission});
                requestContext.abortWith(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("{\"error\":\"Insufficient permissions\"}")
                                .build()
                );
                return;
            }
        }

        // Set security context
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> username;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true; // Role checking done via permissions
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "JWT";
            }
        });
    }

    /**
     * Check if user has required permission
     */
    private boolean checkUserPermission(String username, String permissionName) {
        try {
            AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
            if (processing == null) {
                LOGGER.log(Level.WARNING, "AS2ServerProcessing not available for permission check");
                return false;
            }

            UserManagementAccessDB userMgmt = new UserManagementAccessDB(
                    processing.getDBDriverManager(), LOGGER);
            return userMgmt.userHasPermission(username, permissionName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking user permission", e);
            return false;
        }
    }

    /**
     * Map HTTP method and path to required permission
     * Returns null if no permission check is needed (allows access)
     */
    private String getRequiredPermission(String method, String path) {
        // Allow all authenticated users to get their own user info and permissions
        if (path.equals("users/current") || path.equals("users/current/permissions")) {
            return null; // No special permission required - just authentication
        }

        // Allow authenticated users to change their own password
        if (path.matches("users/\\d+/password") && "POST".equals(method)) {
            return null; // No special permission required - just authentication
        }

        // User management always requires USER_MANAGE permission
        if (path.startsWith("users/")) {
            return "USER_MANAGE";
        }

        // Partner operations
        if (path.startsWith("partners")) {
            if ("GET".equals(method)) {
                return "PARTNER_READ";
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return "PARTNER_WRITE";
            }
        }

        // Certificate operations
        if (path.startsWith("certificates")) {
            if ("GET".equals(method)) {
                return "CERT_READ";
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return "CERT_WRITE";
            }
        }

        // Message operations
        if (path.startsWith("messages")) {
            if ("GET".equals(method)) {
                return "MESSAGE_READ";
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return "MESSAGE_WRITE";
            }
        }

        // System operations
        if (path.startsWith("system")) {
            // Allow all authenticated users to access basic system info
            if (path.equals("system/info") && "GET".equals(method)) {
                return null; // No special permission required - just authentication
            }
            // Allow all authenticated users to check tracker config (needed for UI visibility logic)
            if (path.equals("system/tracker/config") && "GET".equals(method)) {
                return null; // No special permission required - just authentication
            }
            if ("GET".equals(method)) {
                return "SYSTEM_READ";
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return "SYSTEM_WRITE";
            }
        }

        // Preferences, statistics, CEM, notifications - require system permissions
        if (path.startsWith("preferences") || path.startsWith("statistics") ||
            path.startsWith("cem") || path.startsWith("notifications")) {
            if ("GET".equals(method)) {
                return "SYSTEM_READ";
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return "SYSTEM_WRITE";
            }
        }

        // Default: no specific permission required (authenticated users can access)
        return null;
    }
}
