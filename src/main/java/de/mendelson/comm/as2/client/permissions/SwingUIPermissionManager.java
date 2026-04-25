package de.mendelson.comm.as2.client.permissions;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.comm.as2.usermanagement.clientserver.UserPermissionsRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserPermissionsResponse;
import de.mendelson.util.clientserver.BaseClient;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Manages user permissions in SwingUI
 * Loads permissions from server on login and caches them for fast checks
 * Supports permission refresh for user switching scenarios
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class SwingUIPermissionManager {

    private Set<String> userPermissions = new HashSet<>();
    private final BaseClient baseClient;
    private String username;  // Made non-final to support user switching
    private boolean loaded = false;

    /**
     * Create a permission manager for SwingUI
     *
     * @param baseClient The client connection to the server
     * @param username The username to manage permissions for
     */
    public SwingUIPermissionManager(BaseClient baseClient, String username) {
        this.baseClient = baseClient;
        this.username = username;
    }

    /**
     * Load permissions from the server
     * Call this after login or when initializing the GUI
     *
     * @throws Exception If permission loading fails
     */
    public void loadPermissions() throws Exception {
        UserPermissionsRequest request = new UserPermissionsRequest();
        ClientServerResponse response = this.baseClient.sendSync(request, BaseClient.TIMEOUT_SYNC_RECEIVE);

        if (response instanceof UserPermissionsResponse) {
            UserPermissionsResponse permResponse = (UserPermissionsResponse) response;
            this.userPermissions = permResponse.getPermissions();
            if (this.userPermissions == null) {
                this.userPermissions = new HashSet<>();
            }
            this.loaded = true;
        } else {
            throw new Exception("Failed to load permissions: unexpected response type");
        }
    }

    /**
     * Refresh permissions from the server
     * Call this after user switching or when permissions may have changed
     *
     * @throws Exception If permission refresh fails
     */
    public void refreshPermissions() throws Exception {
        // Update username from BaseClient (for user switching)
        this.username = this.baseClient.getUsername();
        this.loaded = false;
        this.userPermissions.clear();
        this.loadPermissions();
    }

    /**
     * Check if the user has a specific permission
     *
     * @param permission The permission name (e.g., "PARTNER_READ")
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        // Super user 'admin' bypasses all permission checks
        if ("admin".equals(this.username)) {
            return true;
        }

        if (!this.loaded) {
            // Not loaded yet - deny by default
            return false;
        }
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        return this.userPermissions.contains(permission);
    }

    /**
     * Check if the user has ANY of the specified permissions (OR logic)
     *
     * @param permissions Permission names to check
     * @return true if the user has at least one of the permissions
     */
    public boolean hasAnyPermission(String... permissions) {
        if (!this.loaded) {
            return false;
        }
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the user has ALL of the specified permissions (AND logic)
     *
     * @param permissions Permission names to check
     * @return true if the user has all of the permissions
     */
    public boolean hasAllPermissions(String... permissions) {
        if (!this.loaded) {
            return false;
        }
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all permission names currently held by the user
     * Returns an unmodifiable view for debugging purposes
     *
     * @return Set of permission names
     */
    public Set<String> getAllPermissions() {
        return Collections.unmodifiableSet(this.userPermissions);
    }

    /**
     * Check if permissions have been loaded
     *
     * @return true if permissions are loaded
     */
    public boolean isLoaded() {
        return this.loaded;
    }
}
