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

package de.mendelson.comm.as2.usermanagement;

import de.mendelson.util.database.IDBDriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Database access layer for the new WebUI user management system
 *
 */
public class UserManagementAccessDB {

    private final IDBDriverManager dbDriverManager;
    @SuppressWarnings("unused")
    private final Logger logger;

    public UserManagementAccessDB(IDBDriverManager dbDriverManager, Logger logger) {
        this.dbDriverManager = dbDriverManager;
        this.logger = logger;
    }

    /**
     * Get all users from the database
     */
    public List<WebUIUser> getAllUsers() throws Exception {
        List<WebUIUser> users = new ArrayList<>();
        String query = "SELECT id, username, password_hash, email, full_name, enabled, " +
                      "must_change_password, created_at, updated_at, last_login FROM webui_users ORDER BY username";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                WebUIUser user = new WebUIUser();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setMustChangePassword(rs.getBoolean("must_change_password"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Get a specific user by ID
     */
    public WebUIUser getUser(int userId) throws Exception {
        String query = "SELECT id, username, password_hash, email, full_name, enabled, " +
                      "must_change_password, created_at, updated_at, last_login FROM webui_users WHERE id = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    WebUIUser user = new WebUIUser();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setMustChangePassword(rs.getBoolean("must_change_password"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Get a specific user by username
     */
    public WebUIUser getUserByUsername(String username) throws Exception {
        String query = "SELECT id, username, password_hash, email, full_name, enabled, " +
                      "must_change_password, created_at, updated_at, last_login FROM webui_users WHERE username = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    WebUIUser user = new WebUIUser();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setMustChangePassword(rs.getBoolean("must_change_password"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Create a new user and return the generated ID
     */
    public int createUser(WebUIUser user) throws Exception {
        String query = "INSERT INTO webui_users (username, password_hash, email, full_name, enabled, must_change_password, tracker_auth_basic_enabled, tracker_auth_cert_enabled) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setBoolean(5, user.isEnabled());
            stmt.setBoolean(6, user.isMustChangePassword());
            stmt.setBoolean(7, false);  // tracker_auth_basic_enabled = false (default)
            stmt.setBoolean(8, true);   // tracker_auth_cert_enabled = true (default)
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("Failed to create user - no ID generated");
    }

    /**
     * Update an existing user
     */
    public void updateUser(WebUIUser user) throws Exception {
        String query = "UPDATE webui_users SET email = ?, full_name = ?, enabled = ? WHERE id = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setBoolean(3, user.isEnabled());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a user (cascades to user_roles)
     */
    public void deleteUser(int userId) throws Exception {
        String query = "DELETE FROM webui_users WHERE id = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Change a user's password
     */
    public void changePassword(int userId, String newPasswordHash) throws Exception {
        String query = "UPDATE webui_users SET password_hash = ?, must_change_password = FALSE WHERE id = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Update last login timestamp for a user
     */
    public void updateLastLogin(String username) throws Exception {
        String query = "UPDATE webui_users SET last_login = ? WHERE username = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Get all roles
     */
    public List<Role> getAllRoles() throws Exception {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT id, name, description, created_at FROM webui_roles ORDER BY name";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                role.setCreatedAt(rs.getTimestamp("created_at"));
                roles.add(role);
            }
        }
        return roles;
    }

    /**
     * Get roles assigned to a specific user
     */
    public List<Role> getUserRoles(int userId) throws Exception {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT r.id, r.name, r.description, r.created_at " +
                      "FROM webui_roles r " +
                      "JOIN webui_user_roles ur ON r.id = ur.role_id " +
                      "WHERE ur.user_id = ? ORDER BY r.name";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    role.setCreatedAt(rs.getTimestamp("created_at"));
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    /**
     * Assign a role to a user
     */
    public void assignRoleToUser(int userId, int roleId) throws Exception {
        String query = "INSERT INTO webui_user_roles (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            stmt.executeUpdate();
        }
    }

    /**
     * Remove a role from a user
     */
    public void removeRoleFromUser(int userId, int roleId) throws Exception {
        String query = "DELETE FROM webui_user_roles WHERE user_id = ? AND role_id = ?";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get all permissions
     */
    public List<Permission> getAllPermissions() throws Exception {
        List<Permission> permissions = new ArrayList<>();
        String query = "SELECT id, name, description, category, created_at FROM webui_permissions ORDER BY category, name";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt("id"));
                permission.setName(rs.getString("name"));
                permission.setDescription(rs.getString("description"));
                permission.setCategory(rs.getString("category"));
                permission.setCreatedAt(rs.getTimestamp("created_at"));
                permissions.add(permission);
            }
        }
        return permissions;
    }

    /**
     * Get permissions for a specific role
     */
    public List<Permission> getRolePermissions(int roleId) throws Exception {
        List<Permission> permissions = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.description, p.category, p.created_at " +
                      "FROM webui_permissions p " +
                      "JOIN webui_role_permissions rp ON p.id = rp.permission_id " +
                      "WHERE rp.role_id = ? ORDER BY p.category, p.name";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setCategory(rs.getString("category"));
                    permission.setCreatedAt(rs.getTimestamp("created_at"));
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }

    /**
     * Check if a user has a specific permission
     * This checks all roles assigned to the user
     */
    public boolean userHasPermission(String username, String permissionName) throws Exception {
        String query = "SELECT COUNT(*) as count " +
                      "FROM webui_users u " +
                      "JOIN webui_user_roles ur ON u.id = ur.user_id " +
                      "JOIN webui_role_permissions rp ON ur.role_id = rp.role_id " +
                      "JOIN webui_permissions p ON rp.permission_id = p.id " +
                      "WHERE u.username = ? AND p.name = ? AND u.enabled = TRUE";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, permissionName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Get all permissions for a user (combining all their roles)
     */
    public Set<String> getUserPermissions(String username) throws Exception {
        Set<String> permissions = new HashSet<>();
        String query = "SELECT DISTINCT p.name " +
                      "FROM webui_users u " +
                      "JOIN webui_user_roles ur ON u.id = ur.user_id " +
                      "JOIN webui_role_permissions rp ON ur.role_id = rp.role_id " +
                      "JOIN webui_permissions p ON rp.permission_id = p.id " +
                      "WHERE u.username = ? AND u.enabled = TRUE";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("name"));
                }
            }
        }
        return permissions;
    }

    /**
     * Get all permissions for a user (aggregated from all their roles) as Permission objects
     */
    public List<Permission> getUserPermissionObjects(String username) throws Exception {
        List<Permission> permissions = new ArrayList<>();
        String query = "SELECT DISTINCT p.id, p.name, p.description, p.category " +
                      "FROM webui_users u " +
                      "JOIN webui_user_roles ur ON u.id = ur.user_id " +
                      "JOIN webui_role_permissions rp ON ur.role_id = rp.role_id " +
                      "JOIN webui_permissions p ON rp.permission_id = p.id " +
                      "WHERE u.username = ? AND u.enabled = TRUE " +
                      "ORDER BY p.category, p.name";

        try (Connection conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setCategory(rs.getString("category"));
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }
}
