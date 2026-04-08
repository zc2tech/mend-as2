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
package de.mendelson.comm.as2.security;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Rate limiter for login attempts to prevent brute force attacks
 * Works for both SwingUI and WebUI authentication
 *
 * @author Julian Xu
 */
public class LoginRateLimiter {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private static final Calendar CALENDAR_UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    // Map of IP address to block expiry time
    private static final ConcurrentHashMap<String, Long> blockedIPs = new ConcurrentHashMap<>();

    // Source constants
    public static final String SOURCE_SWING_UI = "SwingUI";
    public static final String SOURCE_WEB_UI = "WebUI";

    /**
     * Check if an IP address is currently blocked
     *
     * @param remoteAddr IP address
     * @return true if blocked
     */
    public static boolean isBlocked(String remoteAddr) {
        if (remoteAddr == null) {
            return false;
        }

        Long blockedUntil = blockedIPs.get(remoteAddr);
        if (blockedUntil == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > blockedUntil) {
            // Block expired, remove it
            blockedIPs.remove(remoteAddr);
            return false;
        }

        return true;
    }

    /**
     * Check recent failures and block IP if threshold exceeded
     *
     * @param remoteAddr IP address
     * @param connection Database connection
     * @param prefs Preferences for rate limit settings
     * @return true if IP should be/is now blocked
     */
    public static boolean checkAndBlock(String remoteAddr, Connection connection,
            PreferencesAS2 prefs) {
        if (remoteAddr == null) {
            return false;
        }

        // Check if rate limiting is enabled
        if (!"true".equals(prefs.get(PreferencesAS2.LOGIN_RATE_LIMIT_ENABLED))) {
            return false;
        }

        // Check if already blocked
        if (isBlocked(remoteAddr)) {
            return true;
        }

        // Get rate limit settings
        int maxFailures = Integer.parseInt(prefs.get(PreferencesAS2.LOGIN_RATE_LIMIT_FAILURES));
        int windowHours = Integer.parseInt(prefs.get(PreferencesAS2.LOGIN_RATE_LIMIT_WINDOW_HOURS));
        int blockMinutes = Integer.parseInt(prefs.get(PreferencesAS2.LOGIN_RATE_LIMIT_BLOCK_MINUTES));

        // Count recent failures from database
        int recentFailures = countRecentFailures(remoteAddr, windowHours, connection);

        // Check if threshold exceeded
        if (recentFailures >= maxFailures) {
            // Calculate block expiry time
            long blockUntil = System.currentTimeMillis() + (blockMinutes * 60L * 1000L);
            blockedIPs.put(remoteAddr, blockUntil);
            LOGGER.warning("IP blocked due to login rate limit: " + remoteAddr +
                    " (" + recentFailures + " failures in " + windowHours + " hours)");
            return true;
        }

        return false;
    }

    /**
     * Record a failed login attempt
     *
     * @param remoteAddr IP address
     * @param attemptedUser Username attempted
     * @param source Source of login attempt (SwingUI or WebUI)
     * @param userAgent User agent string (can be null for SwingUI)
     * @param connection Database connection
     */
    public static void recordAuthFailure(String remoteAddr, String attemptedUser,
            String source, String userAgent, Connection connection) {
        if (remoteAddr == null) {
            return;
        }

        String sql = "INSERT INTO login_auth_failure (remote_addr, failure_time, attempted_user, source, user_agent) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, remoteAddr);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            statement.setTimestamp(2, timestamp, CALENDAR_UTC);
            statement.setString(3, attemptedUser);
            statement.setString(4, source);
            statement.setString(5, userAgent);
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.warning("Failed to record login auth failure: " + e.getMessage());
        }
    }

    /**
     * Count recent failures for an IP address
     *
     * @param remoteAddr IP address
     * @param windowHours Time window in hours
     * @param connection Database connection
     * @return Number of failures in the time window
     */
    public static int countRecentFailures(String remoteAddr, int windowHours, Connection connection) {
        if (remoteAddr == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM login_auth_failure "
                + "WHERE remote_addr = ? AND failure_time > ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, remoteAddr);

            // Calculate cutoff time
            Calendar calendar = (Calendar) CALENDAR_UTC.clone();
            calendar.add(Calendar.HOUR, -windowHours);
            Timestamp cutoff = new Timestamp(calendar.getTimeInMillis());
            statement.setTimestamp(2, cutoff, CALENDAR_UTC);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to count recent login failures: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get remaining block time in seconds
     *
     * @param remoteAddr IP address
     * @return Seconds remaining, or 0 if not blocked
     */
    public static long getBlockRemainingSeconds(String remoteAddr) {
        if (remoteAddr == null) {
            return 0;
        }

        Long blockedUntil = blockedIPs.get(remoteAddr);
        if (blockedUntil == null) {
            return 0;
        }

        long remaining = blockedUntil - System.currentTimeMillis();
        if (remaining <= 0) {
            blockedIPs.remove(remoteAddr);
            return 0;
        }

        return remaining / 1000L;
    }

    /**
     * Manually unblock an IP address (admin override)
     *
     * @param remoteAddr IP address
     */
    public static void unblockIP(String remoteAddr) {
        if (remoteAddr != null) {
            blockedIPs.remove(remoteAddr);
            LOGGER.info("IP manually unblocked: " + remoteAddr);
        }
    }

    /**
     * Clear all blocks (useful for testing or admin override)
     */
    public static void clearAllBlocks() {
        blockedIPs.clear();
        LOGGER.info("All login blocks cleared");
    }

    /**
     * Get count of currently blocked IPs
     *
     * @return Number of blocked IPs
     */
    public static int getBlockedCount() {
        // Clean expired entries first
        long currentTime = System.currentTimeMillis();
        blockedIPs.entrySet().removeIf(entry -> entry.getValue() < currentTime);
        return blockedIPs.size();
    }

    /**
     * Clean up old auth failure records from database
     *
     * @param retentionDays Number of days to retain records
     * @param connection Database connection
     * @return Number of records deleted
     */
    public static int cleanupOldAuthFailures(int retentionDays, Connection connection) {
        String sql = "DELETE FROM login_auth_failure WHERE failure_time < ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            Calendar calendar = (Calendar) CALENDAR_UTC.clone();
            calendar.add(Calendar.DAY_OF_YEAR, -retentionDays);
            Timestamp cutoff = new Timestamp(calendar.getTimeInMillis());
            statement.setTimestamp(1, cutoff, CALENDAR_UTC);

            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                LOGGER.info("Cleaned up " + deleted + " old login auth failure records");
            }
            return deleted;
        } catch (Exception e) {
            LOGGER.warning("Failed to cleanup old login auth failures: " + e.getMessage());
            return 0;
        }
    }
}
