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
package de.mendelson.comm.as2.api;

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

/**
 * Database access layer for API request logs
 *
 * @author Julian Xu
 */
public class ApiRequestAccessDB {

    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private final IDBDriverManager dbDriverManager;

    public ApiRequestAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Insert new API request
     */
    public void insertApiRequest(ApiRequestInfo info) {
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO api_request_log"
                    + "(request_id, user_id, remote_addr, user_agent, http_method, "
                    + "request_path, request_headers, request_body, content_type, "
                    + "content_size, response_status, auth_status, auth_user, request_time) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {

                stmt.setString(1, info.getRequestId());
                stmt.setInt(2, info.getUserId());
                stmt.setString(3, info.getRemoteAddr());
                stmt.setString(4, info.getUserAgent());
                stmt.setString(5, info.getHttpMethod());
                stmt.setString(6, info.getRequestPath());
                this.dbDriverManager.setTextParameterAsJavaObject(stmt, 7, info.getRequestHeaders());
                stmt.setBytes(8, info.getRequestBody());
                stmt.setString(9, info.getContentType());
                stmt.setInt(10, info.getContentSize());
                stmt.setInt(11, info.getResponseStatus());
                stmt.setInt(12, info.getAuthStatus());
                stmt.setString(13, info.getAuthUser());
                stmt.setTimestamp(14, new Timestamp(info.getRequestTime().getTime()), calendarUTC);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Get API request by request ID
     */
    public ApiRequestInfo getApiRequestById(String requestId) {
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM api_request_log WHERE request_id=?")) {
                stmt.setString(1, requestId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return this.buildApiRequestFromResultSet(rs);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return null;
    }

    /**
     * Get API requests for a specific user with filters
     */
    public List<ApiRequestInfo> getApiRequests(int userId, Date startDate, Date endDate,
            String methodFilter, String pathFilter) {
        List<ApiRequestInfo> list = new ArrayList<>();

        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            // Exclude request_body and request_headers from list query for performance
            StringBuilder sql = new StringBuilder(
                "SELECT id, request_id, user_id, remote_addr, user_agent, http_method, " +
                "request_path, content_type, content_size, " +
                "response_status, auth_status, auth_user, request_time " +
                "FROM api_request_log ");
            sql.append("WHERE user_id = ? ");
            sql.append("AND request_time >= ? AND request_time <= ? ");

            // Add method filter if provided
            if (methodFilter != null && !methodFilter.trim().isEmpty() && !"ALL".equals(methodFilter)) {
                sql.append("AND http_method = ? ");
            }

            // Add path filter if provided
            if (pathFilter != null && !pathFilter.trim().isEmpty()) {
                sql.append("AND request_path LIKE ? ");
            }

            sql.append("ORDER BY request_time DESC");

            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                stmt.setInt(paramIndex++, userId);
                stmt.setTimestamp(paramIndex++, new Timestamp(startDate.getTime()), calendarUTC);
                stmt.setTimestamp(paramIndex++, new Timestamp(endDate.getTime()), calendarUTC);

                if (methodFilter != null && !methodFilter.trim().isEmpty() && !"ALL".equals(methodFilter)) {
                    stmt.setString(paramIndex++, methodFilter.trim());
                }

                if (pathFilter != null && !pathFilter.trim().isEmpty()) {
                    stmt.setString(paramIndex++, "%" + pathFilter.trim() + "%");
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(this.buildApiRequestFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return list;
    }

    /**
     * Delete old API requests
     */
    public int deleteApiRequestsOlderThan(Date cutoffDate) {
        int deletedCount = 0;
        String transactionName = "ApiRequestAccessDB_delete";

        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockDELETE(txnStmt,
                        new String[]{"api_request_log"});
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM api_request_log WHERE request_time < ?")) {
                    stmt.setTimestamp(1, new Timestamp(cutoffDate.getTime()), calendarUTC);
                    deletedCount = stmt.executeUpdate();
                    this.dbDriverManager.commitTransaction(txnStmt, transactionName);
                    conn.commit();
                } catch (Throwable e) {
                    this.dbDriverManager.rollbackTransaction(txnStmt);
                    throw e;
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            return 0;
        }

        return deletedCount;
    }

    /**
     * Record authentication failure
     */
    public void recordAuthFailure(String remoteAddr, String userAgent, String attemptedUser) {
        String transactionName = "ApiRequestAccessDB_recordFailure";
        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(txnStmt,
                        new String[]{"api_auth_failure"});
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO api_auth_failure"
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
                    "SELECT COUNT(*) as failure_count FROM api_auth_failure "
                    + "WHERE remote_addr = ? AND failure_time >= ?")) {
                stmt.setString(1, remoteAddr);
                stmt.setTimestamp(2, cutoff, calendarUTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt("failure_count");
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
        String transactionName = "ApiRequestAccessDB_cleanupFailures";

        try (Connection conn = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            conn.setAutoCommit(false);
            try (Statement txnStmt = conn.createStatement()) {
                this.dbDriverManager.startTransaction(txnStmt, transactionName);
                this.dbDriverManager.setTableLockDELETE(txnStmt,
                        new String[]{"api_auth_failure"});

                long cutoffMillis = System.currentTimeMillis() - (retentionDays * 24L * 3600L * 1000L);
                Timestamp cutoff = new Timestamp(cutoffMillis);

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM api_auth_failure WHERE failure_time < ?")) {
                    stmt.setTimestamp(1, cutoff, calendarUTC);
                    deletedCount = stmt.executeUpdate();
                    this.dbDriverManager.commitTransaction(txnStmt, transactionName);
                    conn.commit();
                } catch (Throwable e) {
                    this.dbDriverManager.rollbackTransaction(txnStmt);
                    throw e;
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            return 0;
        }

        return deletedCount;
    }

    /**
     * Build ApiRequestInfo from ResultSet
     */
    private ApiRequestInfo buildApiRequestFromResultSet(ResultSet rs) throws Exception {
        ApiRequestInfo info = new ApiRequestInfo();
        info.setId(rs.getInt("id"));
        info.setRequestId(rs.getString("request_id"));
        info.setUserId(rs.getInt("user_id"));
        info.setRemoteAddr(rs.getString("remote_addr"));
        info.setUserAgent(rs.getString("user_agent"));
        info.setHttpMethod(rs.getString("http_method"));
        info.setRequestPath(rs.getString("request_path"));

        // These fields may not be present in list queries (excluded for performance)
        try {
            info.setRequestHeaders(this.dbDriverManager.readTextStoredAsJavaObject(rs, "request_headers"));
        } catch (Exception e) {
            // Column not in SELECT, skip
        }
        try {
            info.setRequestBody(rs.getBytes("request_body"));
        } catch (Exception e) {
            // Column not in SELECT, skip
        }

        info.setContentType(rs.getString("content_type"));
        info.setContentSize(rs.getInt("content_size"));
        info.setResponseStatus(rs.getInt("response_status"));
        info.setAuthStatus(rs.getInt("auth_status"));
        info.setAuthUser(rs.getString("auth_user"));
        info.setRequestTime(rs.getTimestamp("request_time", calendarUTC));
        return info;
    }
}
