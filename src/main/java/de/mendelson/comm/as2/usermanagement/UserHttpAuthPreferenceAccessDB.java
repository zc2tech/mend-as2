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
 * GNU General Public License for details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.usermanagement;

import de.mendelson.util.database.IDBDriverManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Database access for user HTTP authentication preferences
 */
public class UserHttpAuthPreferenceAccessDB {

    private final IDBDriverManager dbDriverManager;
    private final Logger logger;

    public UserHttpAuthPreferenceAccessDB(IDBDriverManager dbDriverManager, Logger logger) {
        this.dbDriverManager = dbDriverManager;
        this.logger = logger;
    }

    /**
     * Get all HTTP auth preferences for a user, including partner info
     */
    public List<UserHttpAuthPreference> getPreferencesForUser(int userId) {
        List<UserHttpAuthPreference> preferences = new ArrayList<>();
        String sql = "SELECT pref.id, pref.user_id, pref.partner_id, "
                + "p.partnername, p.as2ident, "
                + "pref.use_message_auth, pref.message_username, pref.message_password, "
                + "pref.use_mdn_auth, pref.mdn_username, pref.mdn_password "
                + "FROM user_preference_http_auth pref "
                + "JOIN partner p ON pref.partner_id = p.id "
                + "WHERE pref.user_id = ? "
                + "ORDER BY p.partnername";

        try (Connection configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = configConnection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserHttpAuthPreference pref = new UserHttpAuthPreference();
                    pref.setId(rs.getInt("id"));
                    pref.setUserId(rs.getInt("user_id"));
                    pref.setPartnerId(rs.getInt("partner_id"));
                    pref.setPartnerName(rs.getString("partnername"));
                    pref.setPartnerAs2Id(rs.getString("as2ident"));
                    pref.setUseMessageAuth(rs.getBoolean("use_message_auth"));
                    pref.setMessageUsername(rs.getString("message_username"));
                    pref.setMessagePassword(rs.getString("message_password"));
                    pref.setUseMdnAuth(rs.getBoolean("use_mdn_auth"));
                    pref.setMdnUsername(rs.getString("mdn_username"));
                    pref.setMdnPassword(rs.getString("mdn_password"));
                    preferences.add(pref);
                }
            }
        } catch (Exception e) {
            this.logger.severe("Error loading HTTP auth preferences for user " + userId + ": " + e.getMessage());
        }
        return preferences;
    }

    /**
     * Get HTTP auth preference for a specific user and partner
     */
    public UserHttpAuthPreference getPreference(int userId, int partnerId) {
        String sql = "SELECT pref.id, pref.user_id, pref.partner_id, "
                + "p.partnername, p.as2ident, "
                + "pref.use_message_auth, pref.message_username, pref.message_password, "
                + "pref.use_mdn_auth, pref.mdn_username, pref.mdn_password "
                + "FROM user_preference_http_auth pref "
                + "JOIN partner p ON pref.partner_id = p.id "
                + "WHERE pref.user_id = ? AND pref.partner_id = ?";

        try (Connection configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = configConnection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, partnerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserHttpAuthPreference pref = new UserHttpAuthPreference();
                    pref.setId(rs.getInt("id"));
                    pref.setUserId(rs.getInt("user_id"));
                    pref.setPartnerId(rs.getInt("partner_id"));
                    pref.setPartnerName(rs.getString("partnername"));
                    pref.setPartnerAs2Id(rs.getString("as2ident"));
                    pref.setUseMessageAuth(rs.getBoolean("use_message_auth"));
                    pref.setMessageUsername(rs.getString("message_username"));
                    pref.setMessagePassword(rs.getString("message_password"));
                    pref.setUseMdnAuth(rs.getBoolean("use_mdn_auth"));
                    pref.setMdnUsername(rs.getString("mdn_username"));
                    pref.setMdnPassword(rs.getString("mdn_password"));
                    return pref;
                }
            }
        } catch (Exception e) {
            this.logger.severe("Error loading HTTP auth preference: " + e.getMessage());
        }
        return null;
    }

    /**
     * Save or update HTTP auth preference
     */
    public void savePreference(UserHttpAuthPreference pref) throws SQLException {
        Connection configConnection = null;
        try {
            configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnection.setAutoCommit(false);

            // Check if exists
            String checkSql = "SELECT id FROM user_preference_http_auth WHERE user_id = ? AND partner_id = ?";
            try (PreparedStatement checkStmt = configConnection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, pref.getUserId());
                checkStmt.setInt(2, pref.getPartnerId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Update existing
                        String updateSql = "UPDATE user_preference_http_auth SET "
                                + "use_message_auth=?, message_username=?, message_password=?, "
                                + "use_mdn_auth=?, mdn_username=?, mdn_password=?, "
                                + "updated_at=CURRENT_TIMESTAMP "
                                + "WHERE user_id=? AND partner_id=?";
                        try (PreparedStatement updateStmt = configConnection.prepareStatement(updateSql)) {
                            updateStmt.setBoolean(1, pref.isUseMessageAuth());
                            updateStmt.setString(2, pref.getMessageUsername());
                            updateStmt.setString(3, pref.getMessagePassword());
                            updateStmt.setBoolean(4, pref.isUseMdnAuth());
                            updateStmt.setString(5, pref.getMdnUsername());
                            updateStmt.setString(6, pref.getMdnPassword());
                            updateStmt.setInt(7, pref.getUserId());
                            updateStmt.setInt(8, pref.getPartnerId());
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Insert new
                        String insertSql = "INSERT INTO user_preference_http_auth "
                                + "(user_id, partner_id, use_message_auth, message_username, message_password, "
                                + "use_mdn_auth, mdn_username, mdn_password) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = configConnection.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, pref.getUserId());
                            insertStmt.setInt(2, pref.getPartnerId());
                            insertStmt.setBoolean(3, pref.isUseMessageAuth());
                            insertStmt.setString(4, pref.getMessageUsername());
                            insertStmt.setString(5, pref.getMessagePassword());
                            insertStmt.setBoolean(6, pref.isUseMdnAuth());
                            insertStmt.setString(7, pref.getMdnUsername());
                            insertStmt.setString(8, pref.getMdnPassword());
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
            configConnection.commit();
        } catch (Exception e) {
            if (configConnection != null) {
                try {
                    configConnection.rollback();
                } catch (SQLException ex) {
                    this.logger.severe("Error rolling back: " + ex.getMessage());
                }
            }
            throw new SQLException(e);
        } finally {
            if (configConnection != null) {
                try {
                    configConnection.setAutoCommit(true);
                    configConnection.close();
                } catch (SQLException e) {
                    this.logger.severe("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Delete HTTP auth preference
     */
    public void deletePreference(int userId, int partnerId) throws SQLException {
        String sql = "DELETE FROM user_preference_http_auth WHERE user_id = ? AND partner_id = ?";
        try (Connection configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = configConnection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, partnerId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
