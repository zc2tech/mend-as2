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

package de.mendelson.comm.as2.tracker.auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access layer for user tracker authentication credentials.
 * Pattern based on PartnerAccessDB inbound auth methods.
 */
public class UserTrackerAuthDB {

    /**
     * Load all tracker auth credentials for a user.
     *
     * @param userId The user's database ID
     * @param configConnection Database connection to config DB
     * @return List of credentials (may be empty)
     */
    public List<UserTrackerAuthCredential> loadCredentials(int userId, Connection configConnection)
            throws SQLException {
        List<UserTrackerAuthCredential> credentials = new ArrayList<>();

        String sql = "SELECT id, auth_type, username, password, cert_fingerprint, cert_alias, enabled "
                + "FROM user_tracker_auth_credentials WHERE user_id=? ORDER BY id";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    UserTrackerAuthCredential credential = new UserTrackerAuthCredential();
                    credential.setDbId(result.getInt("id"));
                    credential.setAuthType(result.getInt("auth_type"));
                    credential.setUsername(result.getString("username"));
                    credential.setPassword(result.getString("password"));
                    credential.setCertFingerprint(result.getString("cert_fingerprint"));
                    credential.setCertAlias(result.getString("cert_alias"));
                    credential.setEnabled(result.getBoolean("enabled"));
                    credentials.add(credential);
                }
            }
        }

        return credentials;
    }

    /**
     * Load master toggle states for a user.
     *
     * @param userId The user's database ID
     * @param configConnection Database connection to config DB
     * @return boolean[2]: [0]=basicAuthEnabled, [1]=certAuthEnabled
     */
    public boolean[] loadMasterToggles(int userId, Connection configConnection)
            throws SQLException {
        String sql = "SELECT tracker_auth_basic_enabled, tracker_auth_cert_enabled "
                + "FROM webui_users WHERE id=?";

        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new boolean[]{
                        result.getBoolean("tracker_auth_basic_enabled"),
                        result.getBoolean("tracker_auth_cert_enabled")
                    };
                }
            }
        }

        // Default: both disabled
        return new boolean[]{false, false};
    }

    /**
     * Save all tracker auth credentials for a user. Deletes existing
     * credentials and inserts new ones (replace strategy).
     *
     * @param userId The user's database ID
     * @param credentials List of credentials to save (empty entries filtered
     * out)
     * @param basicAuthEnabled Master toggle for basic auth
     * @param certAuthEnabled Master toggle for cert auth
     * @param configConnection Database connection (must NOT be in auto-commit
     * mode)
     */
    public void saveCredentials(int userId, List<UserTrackerAuthCredential> credentials,
            boolean basicAuthEnabled, boolean certAuthEnabled,
            Connection configConnection) throws SQLException {

        // 1. Update master toggles in webui_users table
        String updateToggles = "UPDATE webui_users SET "
                + "tracker_auth_basic_enabled=?, tracker_auth_cert_enabled=? "
                + "WHERE id=?";
        try (PreparedStatement statement = configConnection.prepareStatement(updateToggles)) {
            statement.setBoolean(1, basicAuthEnabled);
            statement.setBoolean(2, certAuthEnabled);
            statement.setInt(3, userId);
            statement.executeUpdate();
        }

        // 2. Delete all existing credentials
        deleteCredentials(userId, configConnection);

        // 3. Filter out empty credentials
        List<UserTrackerAuthCredential> validCredentials = new ArrayList<>();
        for (UserTrackerAuthCredential credential : credentials) {
            if (!credential.isEmpty()) {
                validCredentials.add(credential);
            }
        }

        // 4. Insert new credentials (batch)
        if (!validCredentials.isEmpty()) {
            String insertSql = "INSERT INTO user_tracker_auth_credentials"
                    + "(user_id, auth_type, username, password, cert_fingerprint, cert_alias, enabled) "
                    + "VALUES(?,?,?,?,?,?,?)";

            try (PreparedStatement statement = configConnection.prepareStatement(insertSql)) {
                for (UserTrackerAuthCredential credential : validCredentials) {
                    statement.setInt(1, userId);
                    statement.setInt(2, credential.getAuthType());
                    statement.setString(3, credential.getUsername());
                    statement.setString(4, credential.getPassword());
                    statement.setString(5, credential.getCertFingerprint());
                    statement.setString(6, credential.getCertAlias());
                    statement.setBoolean(7, credential.isEnabled());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }

    /**
     * Delete all tracker auth credentials for a user.
     *
     * @param userId The user's database ID
     * @param configConnection Database connection
     */
    public void deleteCredentials(int userId, Connection configConnection) throws SQLException {
        String sql = "DELETE FROM user_tracker_auth_credentials WHERE user_id=?";
        try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Check if any authentication is required for a user.
     *
     * @param userId The user's database ID
     * @param configConnection Database connection
     * @return true if basic OR cert auth is enabled
     */
    public boolean isAuthRequired(int userId, Connection configConnection) throws SQLException {
        boolean[] toggles = loadMasterToggles(userId, configConnection);
        return toggles[0] || toggles[1];  // Basic OR Cert enabled
    }
}
