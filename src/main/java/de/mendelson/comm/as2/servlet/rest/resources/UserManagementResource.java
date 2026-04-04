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

package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;
import de.mendelson.comm.as2.usermanagement.*;
import de.mendelson.util.clientserver.user.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST resource for user management operations
 *
 */
@Path("/users")
public class UserManagementResource {

    private static final Logger LOGGER = Logger.getLogger(UserManagementResource.class.getName());

    private UserManagementAccessDB getUserManagementAccess() {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return null;
        }
        return new UserManagementAccessDB(processing.getDBDriverManager(), LOGGER);
    }

    /**
     * GET /api/v1/users - List all users
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            List<WebUIUser> users = userMgmt.getAllUsers();

            // Remove password hashes from response for security
            for (WebUIUser user : users) {
                user.setPasswordHash(null);
            }

            return Response.ok(users).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/users/{id} - Get user details
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") int userId) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            WebUIUser user = userMgmt.getUser(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            // Remove password hash from response
            user.setPasswordHash(null);

            return Response.ok(user).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * POST /api/v1/users - Create new user
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(Map<String, Object> userData) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            // Validate required fields
            String username = (String) userData.get("username");
            Boolean generatePassword = (Boolean) userData.get("generatePassword");
            String password = (String) userData.get("password");

            if (username == null || username.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}").build();
            }

            // If generatePassword is true, generate a random password
            String generatedPassword = null;
            if (generatePassword != null && generatePassword) {
                generatedPassword = de.mendelson.comm.as2.usermanagement.PasswordGenerator.generatePassword();
                password = generatedPassword;
            }

            if (password == null || password.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Password is required\"}").build();
            }

            // Check if username already exists
            WebUIUser existingUser = userMgmt.getUserByUsername(username);
            if (existingUser != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"Username already exists\"}").build();
            }

            // Create user object
            WebUIUser user = new WebUIUser();
            user.setUsername(username);

            // Hash password using existing User.cryptPassword method
            String passwordHash = User.cryptPassword(password.toCharArray());
            user.setPasswordHash(passwordHash);

            user.setEmail((String) userData.get("email"));
            user.setFullName((String) userData.get("fullName"));

            Boolean enabled = (Boolean) userData.get("enabled");
            user.setEnabled(enabled != null ? enabled : true);

            // All new users must change password on first login
            user.setMustChangePassword(true);

            // Create user in database
            int userId = userMgmt.createUser(user);
            user.setId(userId);

            // Assign roles if provided
            @SuppressWarnings("unchecked")
            List<Integer> roleIds = (List<Integer>) userData.get("roleIds");
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    userMgmt.assignRoleToUser(userId, roleId);
                }
            }

            // If password was generated and user has email, send it
            if (generatedPassword != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                try {
                    AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
                    if (processing != null) {
                        // Get notification configuration
                        de.mendelson.util.systemevents.notification.NotificationAccessDB notificationAccess =
                            new de.mendelson.util.systemevents.notification.NotificationAccessDBImplAS2(
                                processing.getDBDriverManager());
                        de.mendelson.util.systemevents.notification.NotificationData notificationData =
                            notificationAccess.getNotificationData();

                        // Only try to send email if notification is properly configured
                        // Check if mail server is configured (not null and not empty)
                        if (notificationData != null
                            && notificationData.getMailServer() != null
                            && !notificationData.getMailServer().trim().isEmpty()) {
                            // Construct server URL - this should match the actual server deployment
                            String serverUrl = "http://localhost:8080/as2";

                            // Send the email
                            de.mendelson.comm.as2.usermanagement.UserNotificationMailer.sendUserCreationEmail(
                                user, generatedPassword, notificationData, serverUrl);
                            LOGGER.log(Level.INFO, "Password email sent successfully to user {0}", username);
                        } else {
                            LOGGER.log(Level.WARNING, "Email notification not configured, password not sent to user {0}", username);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to send password email to user " + username + ": " + e.getMessage(), e);
                    // Don't fail user creation if email fails - password is returned in response
                }
            }

            // Remove password hash from response
            user.setPasswordHash(null);

            Map<String, Object> response = new HashMap<>();
            response.put("id", userId);
            response.put("message", "User created successfully");
            response.put("user", user);

            // Include generated password in response for display (user should save it)
            if (generatedPassword != null) {
                response.put("generatedPassword", generatedPassword);
            }

            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * PUT /api/v1/users/{id} - Update user
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int userId, Map<String, Object> userData) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            WebUIUser user = userMgmt.getUser(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            // Update fields
            if (userData.containsKey("email")) {
                user.setEmail((String) userData.get("email"));
            }
            if (userData.containsKey("fullName")) {
                user.setFullName((String) userData.get("fullName"));
            }
            if (userData.containsKey("enabled")) {
                user.setEnabled((Boolean) userData.get("enabled"));
            }

            userMgmt.updateUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");

            return Response.ok(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * DELETE /api/v1/users/{id} - Delete user
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") int userId) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            WebUIUser user = userMgmt.getUser(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            userMgmt.deleteUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User deleted successfully");

            return Response.ok(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * POST /api/v1/users/{id}/password - Change password
     */
    @POST
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("id") int userId, Map<String, String> passwordData) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            String newPassword = passwordData.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"New password is required\"}").build();
            }

            WebUIUser user = userMgmt.getUser(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            // Hash new password
            String passwordHash = User.cryptPassword(newPassword.toCharArray());
            userMgmt.changePassword(userId, passwordHash);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password changed successfully");

            return Response.ok(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/users/{id}/roles - Get user roles
     */
    @GET
    @Path("/{id}/roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRoles(@PathParam("id") int userId) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            List<Role> roles = userMgmt.getUserRoles(userId);
            return Response.ok(roles).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting user roles", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * POST /api/v1/users/{id}/roles - Assign role to user
     */
    @POST
    @Path("/{id}/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignRoleToUser(@PathParam("id") int userId, Map<String, Integer> roleData) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            Integer roleId = roleData.get("roleId");
            if (roleId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Role ID is required\"}").build();
            }

            userMgmt.assignRoleToUser(userId, roleId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role assigned successfully");

            return Response.ok(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error assigning role to user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * DELETE /api/v1/users/{id}/roles/{roleId} - Remove role from user
     */
    @DELETE
    @Path("/{id}/roles/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeRoleFromUser(@PathParam("id") int userId, @PathParam("roleId") int roleId) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            userMgmt.removeRoleFromUser(userId, roleId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role removed successfully");

            return Response.ok(response).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing role from user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/roles - List all roles
     */
    @GET
    @Path("/roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRoles() {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            List<Role> roles = userMgmt.getAllRoles();
            return Response.ok(roles).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting roles", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/roles/{id}/permissions - Get role permissions
     */
    @GET
    @Path("/roles/{id}/permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRolePermissions(@PathParam("id") int roleId) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            List<Permission> permissions = userMgmt.getRolePermissions(roleId);
            return Response.ok(permissions).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting role permissions", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/permissions - List all permissions
     */
    @GET
    @Path("/permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPermissions() {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            List<Permission> permissions = userMgmt.getAllPermissions();
            return Response.ok(permissions).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting permissions", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET /api/v1/users/current/permissions - Get current user's permissions
     */
    @GET
    @Path("/current/permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUserPermissions(@jakarta.ws.rs.core.Context jakarta.ws.rs.core.SecurityContext securityContext) {
        try {
            UserManagementAccessDB userMgmt = getUserManagementAccess();
            if (userMgmt == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\":\"Server not available\"}").build();
            }

            // Get username from security context (set by JwtAuthenticationFilter)
            String username = securityContext.getUserPrincipal().getName();

            // Get user by username
            WebUIUser user = userMgmt.getUserByUsername(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}").build();
            }

            // Get all permissions for this user (aggregated from all their roles)
            List<Permission> permissions = userMgmt.getUserPermissionObjects(username);

            return Response.ok(permissions).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting current user permissions", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
