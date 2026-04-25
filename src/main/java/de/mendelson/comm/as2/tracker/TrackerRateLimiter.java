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

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter for tracker endpoint to prevent brute force attacks
 *
 * @author Julian Xu
 */
public class TrackerRateLimiter {

    // Map of IP address to block expiry time
    private static final ConcurrentHashMap<String, Long> blockedIPs = new ConcurrentHashMap<>();

    /**
     * Check if an IP address is localhost
     *
     * @param remoteAddr IP address
     * @return true if localhost
     */
    private static boolean isLocalhost(String remoteAddr) {
        if (remoteAddr == null) {
            return false;
        }
        // Check for IPv4 localhost
        if (remoteAddr.equals("127.0.0.1") || remoteAddr.startsWith("127.")) {
            return true;
        }
        // Check for IPv6 localhost
        if (remoteAddr.equals("::1") || remoteAddr.contains("0:0:0:0:0:0:0:1")) {
            return true;
        }
        // Check for localhost hostname
        if (remoteAddr.equalsIgnoreCase("localhost")) {
            return true;
        }
        return false;
    }

    /**
     * Check if an IP address is currently blocked
     *
     * @param remoteAddr IP address
     * @return true if blocked
     */
    public static boolean isBlocked(String remoteAddr) {
        // Never block localhost
        if (isLocalhost(remoteAddr)) {
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
     * @param dao Database access
     * @param prefs Preferences for rate limit settings
     * @return true if IP should be/is now blocked
     */
    public static boolean checkAndBlock(String remoteAddr, TrackerMessageAccessDB dao,
            PreferencesAS2 prefs) {
        // Never block localhost
        if (isLocalhost(remoteAddr)) {
            return false;
        }

        // Check if already blocked
        if (isBlocked(remoteAddr)) {
            return true;
        }

        // Get rate limit settings
        int maxFailures = Integer.parseInt(prefs.get(PreferencesAS2.TRACKER_RATE_LIMIT_FAILURES));
        int windowHours = Integer.parseInt(prefs.get(PreferencesAS2.TRACKER_RATE_LIMIT_WINDOW_HOURS));
        int blockMinutes = Integer.parseInt(prefs.get(PreferencesAS2.TRACKER_RATE_LIMIT_BLOCK_MINUTES));

        // Count recent failures from database
        int recentFailures = dao.countRecentFailures(remoteAddr, windowHours);

        // Check if threshold exceeded
        if (recentFailures >= maxFailures) {
            // Calculate block expiry time
            long blockUntil = System.currentTimeMillis() + (blockMinutes * 60L * 1000L);
            blockedIPs.put(remoteAddr, blockUntil);
            return true;
        }

        return false;
    }

    /**
     * Manually block an IP address
     *
     * @param remoteAddr IP address
     * @param blockMinutes Duration in minutes
     */
    public static void blockIP(String remoteAddr, int blockMinutes) {
        long blockUntil = System.currentTimeMillis() + (blockMinutes * 60L * 1000L);
        blockedIPs.put(remoteAddr, blockUntil);
    }

    /**
     * Manually unblock an IP address
     *
     * @param remoteAddr IP address
     */
    public static void unblockIP(String remoteAddr) {
        blockedIPs.remove(remoteAddr);
    }

    /**
     * Get remaining block time in seconds
     *
     * @param remoteAddr IP address
     * @return Seconds remaining, or 0 if not blocked
     */
    public static long getBlockRemainingSeconds(String remoteAddr) {
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
     * Clear all blocks (useful for testing or admin override)
     */
    public static void clearAllBlocks() {
        blockedIPs.clear();
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
}
