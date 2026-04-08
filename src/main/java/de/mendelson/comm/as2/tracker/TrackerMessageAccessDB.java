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
package de.mendelson.comm.as2.tracker;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Database access layer for tracker messages
 *
 * @author Julian Xu
 */
public class TrackerMessageAccessDB {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private final IDBDriverManager dbDriverManager;

    public TrackerMessageAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Insert new tracker message
     */
    public void insertTrackerMessage(TrackerMessageInfo info) {
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO tracker_message"
                    + "(messageid, tracker_id, remote_addr, user_agent, content_type, "
                    + "content_size, initdateutc, auth_status, auth_user, rawfilename, request_headers) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {

                stmt.setString(1, info.getMessageId());
                stmt.setString(2, info.getTrackerId());
                stmt.setString(3, info.getRemoteAddr());
                stmt.setString(4, info.getUserAgent());
                stmt.setString(5, info.getContentType());
                stmt.setInt(6, info.getContentSize());
                stmt.setTimestamp(7, new Timestamp(info.getInitDate().getTime()), calendarUTC);
                stmt.setInt(8, info.getAuthStatus());
                stmt.setString(9, info.getAuthUser());
                stmt.setString(10, info.getRawFilename());
                this.dbDriverManager.setTextParameterAsJavaObject(stmt, 11, info.getRequestHeaders());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Get tracker message by tracker ID
     */
    public TrackerMessageInfo getTrackerMessage(String trackerId) {
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM tracker_message WHERE tracker_id=?")) {
                stmt.setString(1, trackerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return this.buildTrackerMessageFromResultSet(rs);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return null;
    }

    /**
     * Get tracker messages by date range
     */
    public List<TrackerMessageInfo> getTrackerMessages(Date startDate, Date endDate) {
        List<TrackerMessageInfo> list = new ArrayList<>();
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM tracker_message "
                    + "WHERE initdateutc >= ? AND initdateutc <= ? "
                    + "ORDER BY initdateutc DESC")) {
                stmt.setTimestamp(1, new Timestamp(startDate.getTime()), calendarUTC);
                stmt.setTimestamp(2, new Timestamp(endDate.getTime()), calendarUTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(this.buildTrackerMessageFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return list;
    }

    /**
     * Get tracker messages by date range and auth status
     */
    public List<TrackerMessageInfo> getTrackerMessages(Date startDate, Date endDate,
            boolean includeNone, boolean includeSuccess, boolean includeFailed) {
        List<TrackerMessageInfo> list = new ArrayList<>();

        // Build status filter
        List<String> statusFilters = new ArrayList<>();
        if (includeNone) statusFilters.add("auth_status = " + TrackerMessageInfo.AUTH_STATUS_NONE);
        if (includeSuccess) statusFilters.add("auth_status = " + TrackerMessageInfo.AUTH_STATUS_SUCCESS);
        if (includeFailed) statusFilters.add("auth_status = " + TrackerMessageInfo.AUTH_STATUS_FAILED);

        if (statusFilters.isEmpty()) {
            return list; // No filters selected
        }

        String statusCondition = String.join(" OR ", statusFilters);

        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            String sql = "SELECT * FROM tracker_message "
                    + "WHERE initdateutc >= ? AND initdateutc <= ? "
                    + "AND (" + statusCondition + ") "
                    + "ORDER BY initdateutc DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, new Timestamp(startDate.getTime()), calendarUTC);
                stmt.setTimestamp(2, new Timestamp(endDate.getTime()), calendarUTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(this.buildTrackerMessageFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return list;
    }

    /**
     * Get tracker messages by tracker ID (partial match)
     */
    public List<TrackerMessageInfo> getTrackerMessagesByTrackerId(String trackerIdFilter) {
        List<TrackerMessageInfo> list = new ArrayList<>();
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM tracker_message "
                    + "WHERE tracker_id LIKE ? "
                    + "ORDER BY initdateutc DESC")) {
                stmt.setString(1, "%" + trackerIdFilter + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(this.buildTrackerMessageFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return list;
    }

    /**
     * Delete old tracker messages
     */
    public int deleteTrackerMessagesOlderThan(Date cutoffDate) {
        int deletedCount = 0;
        String transactionName = "TrackerMessageAccessDB_delete";
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockDELETE(txnStmt,
                        new String[]{"tracker_message"});
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM tracker_message WHERE initdateutc < ?")) {
                    stmt.setTimestamp(1, new Timestamp(cutoffDate.getTime()), calendarUTC);
                    deletedCount = stmt.executeUpdate();
                    this.dbDriverManager.commitTransaction(txnStmt, transactionName);
                } catch (Throwable e) {
                    this.dbDriverManager.rollbackTransaction(txnStmt);
                    throw e;
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return deletedCount;
    }

    /**
     * Record authentication failure
     */
    public void recordAuthFailure(String remoteAddr, String userAgent, String attemptedUser) {
        String transactionName = "TrackerMessageAccessDB_recordFailure";
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(txnStmt,
                        new String[]{"tracker_auth_failure"});
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tracker_auth_failure"
                        + "(remote_addr, failure_time, user_agent, attempted_user) "
                        + "VALUES(?,?,?,?)")) {
                    stmt.setString(1, remoteAddr);
                    stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()), calendarUTC);
                    stmt.setString(3, userAgent);
                    stmt.setString(4, attemptedUser);
                    stmt.executeUpdate();
                    this.dbDriverManager.commitTransaction(txnStmt, transactionName);
                } catch (Throwable e) {
                    this.dbDriverManager.rollbackTransaction(txnStmt);
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Count recent auth failures for an IP address
     */
    public int countRecentFailures(String remoteAddr, int windowHours) {
        int count = 0;
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            // Calculate cutoff time
            long cutoffMillis = System.currentTimeMillis() - (windowHours * 3600L * 1000L);
            Timestamp cutoff = new Timestamp(cutoffMillis);

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM tracker_auth_failure "
                    + "WHERE remote_addr = ? AND failure_time > ?")) {
                stmt.setString(1, remoteAddr);
                stmt.setTimestamp(2, cutoff, calendarUTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return count;
    }

    /**
     * Cleanup old auth failure records
     */
    public int cleanupOldAuthFailures(int retentionDays) {
        int deletedCount = 0;
        String transactionName = "TrackerMessageAccessDB_cleanupFailures";
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockDELETE(txnStmt,
                        new String[]{"tracker_auth_failure"});

                long cutoffMillis = System.currentTimeMillis() - (retentionDays * 24L * 3600L * 1000L);
                Timestamp cutoff = new Timestamp(cutoffMillis);

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM tracker_auth_failure WHERE failure_time < ?")) {
                    stmt.setTimestamp(1, cutoff, calendarUTC);
                    deletedCount = stmt.executeUpdate();
                    this.dbDriverManager.commitTransaction(txnStmt, transactionName);
                } catch (Throwable e) {
                    this.dbDriverManager.rollbackTransaction(txnStmt);
                    throw e;
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return deletedCount;
    }

    /**
     * Build TrackerMessageInfo from ResultSet
     */
    private TrackerMessageInfo buildTrackerMessageFromResultSet(ResultSet rs) throws Exception {
        TrackerMessageInfo info = new TrackerMessageInfo();
        info.setId(rs.getInt("id"));
        info.setMessageId(rs.getString("messageid"));
        info.setTrackerId(rs.getString("tracker_id"));
        info.setRemoteAddr(rs.getString("remote_addr"));
        info.setUserAgent(rs.getString("user_agent"));
        info.setContentType(rs.getString("content_type"));
        info.setContentSize(rs.getInt("content_size"));
        info.setInitDate(rs.getTimestamp("initdateutc", calendarUTC));
        info.setAuthStatus(rs.getInt("auth_status"));
        info.setAuthUser(rs.getString("auth_user"));
        info.setRawFilename(rs.getString("rawfilename"));
        info.setRequestHeaders(this.dbDriverManager.readTextStoredAsJavaObject(rs, "request_headers"));
        return info;
    }
}
