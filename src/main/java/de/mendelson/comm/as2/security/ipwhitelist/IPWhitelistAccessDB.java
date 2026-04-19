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
package de.mendelson.comm.as2.security.ipwhitelist;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database access layer for IP whitelist functionality
 *
 * @author Julian Xu
 */
public class IPWhitelistAccessDB {

    private final IDBDriverManager dbDriverManager;

    public IPWhitelistAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    // ========== Global Whitelist ==========

    /**
     * Get all global whitelist entries
     */
    public List<IPWhitelistEntry> getGlobalWhitelist() {
        return getGlobalWhitelist(null);
    }

    /**
     * Get global whitelist entries for a specific target type
     *
     * @param targetType Target type (AS2, TRACKER, WEBUI, API, ALL) or null for all
     */
    public List<IPWhitelistEntry> getGlobalWhitelist(String targetType) {
        List<IPWhitelistEntry> entries = new ArrayList<>();

        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "SELECT id, ip_pattern, description, target_type, enabled, created_at, created_by " +
                    "FROM ip_whitelist_global";

            if (targetType != null) {
                sql += " WHERE target_type = ?";
            }

            sql += " ORDER BY id";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                if (targetType != null) {
                    statement.setString(1, targetType);
                }

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        IPWhitelistEntry entry = new IPWhitelistEntry();
                        entry.setId(result.getInt("id"));
                        entry.setIpPattern(result.getString("ip_pattern"));
                        entry.setDescription(result.getString("description"));
                        entry.setTargetType(result.getString("target_type"));
                        entry.setEnabled(result.getBoolean("enabled"));
                        entry.setCreatedAt(result.getTimestamp("created_at"));
                        entry.setCreatedBy(result.getString("created_by"));
                        entries.add(entry);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }

        return entries;
    }

    /**
     * Add global whitelist entry
     */
    public void addGlobalWhitelist(IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "INSERT INTO ip_whitelist_global " +
                    "(ip_pattern, description, target_type, enabled, created_by) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement = configConnection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, entry.getIpPattern());
                statement.setString(2, entry.getDescription());
                statement.setString(3, entry.getTargetType());
                statement.setBoolean(4, entry.isEnabled());
                statement.setString(5, entry.getCreatedBy());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getInt(1));
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Update global whitelist entry
     */
    public void updateGlobalWhitelist(IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "UPDATE ip_whitelist_global SET " +
                    "ip_pattern = ?, description = ?, target_type = ?, enabled = ? " +
                    "WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setString(1, entry.getIpPattern());
                statement.setString(2, entry.getDescription());
                statement.setString(3, entry.getTargetType());
                statement.setBoolean(4, entry.isEnabled());
                statement.setInt(5, entry.getId());
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Delete global whitelist entry
     */
    public void deleteGlobalWhitelist(int id) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "DELETE FROM ip_whitelist_global WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    // ========== Partner Whitelist ==========

    /**
     * Get whitelist entries for a specific partner
     */
    public List<IPWhitelistEntry> getPartnerWhitelist(int partnerId) {
        List<IPWhitelistEntry> entries = new ArrayList<>();

        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "SELECT id, partner_id, ip_pattern, description, enabled, created_at " +
                    "FROM ip_whitelist_partner WHERE partner_id = ? ORDER BY id";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, partnerId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        IPWhitelistEntry entry = new IPWhitelistEntry();
                        entry.setId(result.getInt("id"));
                        entry.setPartnerId(result.getInt("partner_id"));
                        entry.setIpPattern(result.getString("ip_pattern"));
                        entry.setDescription(result.getString("description"));
                        entry.setEnabled(result.getBoolean("enabled"));
                        entry.setCreatedAt(result.getTimestamp("created_at"));
                        entries.add(entry);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }

        return entries;
    }

    /**
     * Add partner whitelist entry
     */
    public void addPartnerWhitelist(int partnerId, IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "INSERT INTO ip_whitelist_partner " +
                    "(partner_id, ip_pattern, description, enabled) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = configConnection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, partnerId);
                statement.setString(2, entry.getIpPattern());
                statement.setString(3, entry.getDescription());
                statement.setBoolean(4, entry.isEnabled());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getInt(1));
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Delete partner whitelist entry
     */
    public void deletePartnerWhitelist(int id) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "DELETE FROM ip_whitelist_partner WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Update partner whitelist entry
     */
    public void updatePartnerWhitelist(IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "UPDATE ip_whitelist_partner SET " +
                        "ip_pattern = ?, description = ?, enabled = ?, updated_at = ? " +
                        "WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setString(1, entry.getIpPattern());
                statement.setString(2, entry.getDescription());
                statement.setBoolean(3, entry.isEnabled());
                statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setInt(5, entry.getId());
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    // ========== User Whitelist ==========

    /**
     * Get whitelist entries for a specific user
     */
    public List<IPWhitelistEntry> getUserWhitelist(int userId) {
        List<IPWhitelistEntry> entries = new ArrayList<>();

        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "SELECT id, user_id, ip_pattern, description, enabled, created_at " +
                    "FROM ip_whitelist_user WHERE user_id = ? ORDER BY id";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, userId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        IPWhitelistEntry entry = new IPWhitelistEntry();
                        entry.setId(result.getInt("id"));
                        entry.setUserId(result.getInt("user_id"));
                        entry.setIpPattern(result.getString("ip_pattern"));
                        entry.setDescription(result.getString("description"));
                        entry.setEnabled(result.getBoolean("enabled"));
                        entry.setCreatedAt(result.getTimestamp("created_at"));
                        entries.add(entry);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }

        return entries;
    }

    /**
     * Add user whitelist entry
     */
    public void addUserWhitelist(int userId, IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "INSERT INTO ip_whitelist_user " +
                    "(user_id, ip_pattern, description, enabled) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = configConnection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, userId);
                statement.setString(2, entry.getIpPattern());
                statement.setString(3, entry.getDescription());
                statement.setBoolean(4, entry.isEnabled());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getInt(1));
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Delete user whitelist entry
     */
    public void deleteUserWhitelist(int id) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "DELETE FROM ip_whitelist_user WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Update user whitelist entry
     */
    public void updateUserWhitelist(IPWhitelistEntry entry) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "UPDATE ip_whitelist_user SET " +
                        "ip_pattern = ?, description = ?, enabled = ?, updated_at = ? " +
                        "WHERE id = ?";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setString(1, entry.getIpPattern());
                statement.setString(2, entry.getDescription());
                statement.setBoolean(3, entry.isEnabled());
                statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setInt(5, entry.getId());
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    // ========== Block Log ==========

    /**
     * Log a blocked IP attempt
     */
    public void logBlockedAttempt(IPWhitelistBlockLog log) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "INSERT INTO ip_whitelist_block_log " +
                    "(blocked_ip, target_type, attempted_user, attempted_partner, user_agent, request_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setString(1, log.getBlockedIp());
                statement.setString(2, log.getTargetType());
                statement.setString(3, log.getAttemptedUser());
                statement.setString(4, log.getAttemptedPartner());
                statement.setString(5, log.getUserAgent());
                statement.setString(6, log.getRequestPath());
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Get block log entries
     */
    public List<IPWhitelistBlockLog> getBlockLog(Date startDate, Date endDate, String targetType) {
        List<IPWhitelistBlockLog> logs = new ArrayList<>();

        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            StringBuilder sql = new StringBuilder(
                    "SELECT id, blocked_ip, target_type, attempted_user, attempted_partner, " +
                            "block_time, user_agent, request_path " +
                            "FROM ip_whitelist_block_log WHERE 1=1");

            if (startDate != null) {
                sql.append(" AND block_time >= ?");
            }
            if (endDate != null) {
                sql.append(" AND block_time <= ?");
            }
            if (targetType != null) {
                sql.append(" AND target_type = ?");
            }

            sql.append(" ORDER BY block_time DESC LIMIT 1000");

            try (PreparedStatement statement = configConnection.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (startDate != null) {
                    statement.setTimestamp(paramIndex++, new Timestamp(startDate.getTime()));
                }
                if (endDate != null) {
                    statement.setTimestamp(paramIndex++, new Timestamp(endDate.getTime()));
                }
                if (targetType != null) {
                    statement.setString(paramIndex++, targetType);
                }

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        IPWhitelistBlockLog log = new IPWhitelistBlockLog();
                        log.setId(result.getInt("id"));
                        log.setBlockedIp(result.getString("blocked_ip"));
                        log.setTargetType(result.getString("target_type"));
                        log.setAttemptedUser(result.getString("attempted_user"));
                        log.setAttemptedPartner(result.getString("attempted_partner"));
                        log.setBlockTime(result.getTimestamp("block_time"));
                        log.setUserAgent(result.getString("user_agent"));
                        log.setRequestPath(result.getString("request_path"));
                        logs.add(log);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }

        return logs;
    }

    /**
     * Cleanup old block log entries
     */
    public void cleanupOldBlockLogs(int retentionDays) {
        try (Connection configConnection = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_CONFIG)) {

            String sql = "DELETE FROM ip_whitelist_block_log " +
                    "WHERE block_time < DATE_SUB(NOW(), INTERVAL ? DAY)";

            try (PreparedStatement statement = configConnection.prepareStatement(sql)) {
                statement.setInt(1, retentionDays);
                int deletedRows = statement.executeUpdate();

                if (deletedRows > 0) {
                    System.out.println("IP whitelist: Cleaned up " + deletedRows +
                            " old block log entries (older than " + retentionDays + " days)");
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }
}
