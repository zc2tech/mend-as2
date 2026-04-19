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

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.database.IDBDriverManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * IP Whitelist Service with in-memory caching for performance
 * Singleton service that checks IP addresses against whitelist rules
 *
 * @author Julian Xu
 */
public class IPWhitelistService {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");

    // Singleton instance
    private static IPWhitelistService instance;

    private final IDBDriverManager dbDriverManager;
    private final IPWhitelistAccessDB accessDB;

    // In-memory cache (refreshed every 60 seconds)
    private volatile Map<String, List<IPWhitelistEntry>> globalCache = new ConcurrentHashMap<>();
    private volatile Map<Integer, List<IPWhitelistEntry>> partnerCache = new ConcurrentHashMap<>();
    private volatile Map<Integer, List<IPWhitelistEntry>> userCache = new ConcurrentHashMap<>();
    private volatile long lastCacheRefresh = 0;
    private static final long CACHE_REFRESH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(60);

    // Scheduled executor for cache refresh
    private final ScheduledExecutorService cacheRefreshExecutor;

    /**
     * Private constructor for singleton
     */
    private IPWhitelistService(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.accessDB = new IPWhitelistAccessDB(dbDriverManager);

        // Start cache refresh thread
        this.cacheRefreshExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ip-whitelist-cache-refresh");
            t.setDaemon(true);
            return t;
        });

        // Initial cache load
        refreshCache();

        // Schedule periodic cache refresh
        cacheRefreshExecutor.scheduleWithFixedDelay(
                this::refreshCache,
                CACHE_REFRESH_INTERVAL_MS,
                CACHE_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );

        LOGGER.info("IP Whitelist Service initialized with cache refresh interval: " +
                (CACHE_REFRESH_INTERVAL_MS / 1000) + " seconds");
    }

    /**
     * Get singleton instance
     */
    public static synchronized IPWhitelistService getInstance(IDBDriverManager dbDriverManager) {
        if (instance == null) {
            instance = new IPWhitelistService(dbDriverManager);
        }
        return instance;
    }

    /**
     * Refresh cache from database
     */
    public synchronized void refreshCache() {
        try {
            long startTime = System.currentTimeMillis();

            // Refresh global cache (by target type)
            Map<String, List<IPWhitelistEntry>> newGlobalCache = new ConcurrentHashMap<>();
            newGlobalCache.put(IPWhitelistEntry.TARGET_AS2,
                    accessDB.getGlobalWhitelist(IPWhitelistEntry.TARGET_AS2));
            newGlobalCache.put(IPWhitelistEntry.TARGET_TRACKER,
                    accessDB.getGlobalWhitelist(IPWhitelistEntry.TARGET_TRACKER));
            newGlobalCache.put(IPWhitelistEntry.TARGET_WEBUI,
                    accessDB.getGlobalWhitelist(IPWhitelistEntry.TARGET_WEBUI));
            newGlobalCache.put(IPWhitelistEntry.TARGET_API,
                    accessDB.getGlobalWhitelist(IPWhitelistEntry.TARGET_API));
            newGlobalCache.put(IPWhitelistEntry.TARGET_ALL,
                    accessDB.getGlobalWhitelist(IPWhitelistEntry.TARGET_ALL));

            // Note: Partner and user caches are loaded on-demand to avoid memory overhead

            // Atomically update cache
            this.globalCache = newGlobalCache;
            this.lastCacheRefresh = System.currentTimeMillis();

            long duration = System.currentTimeMillis() - startTime;
            LOGGER.fine("IP whitelist cache refreshed in " + duration + "ms");

        } catch (Exception e) {
            LOGGER.warning("Failed to refresh IP whitelist cache: " + e.getMessage());
        }
    }

    /**
     * Check if IP is allowed for AS2 endpoint
     *
     * @param ip            Client IP address
     * @param partnerAS2Id  Partner AS2 identifier (from AS2-From header), can be null
     * @return true if allowed, false if blocked
     */
    public boolean isAllowedForAS2(String ip, String partnerAS2Id) {
        // Check if whitelist is enabled
        PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
        if (!"true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_AS2))) {
            return true; // Whitelist disabled, allow all
        }

        String mode = prefs.get(PreferencesAS2.IP_WHITELIST_MODE);

        // Check global whitelist
        if (isAllowedByMode(mode, "GLOBAL")) {
            if (matchesGlobalWhitelist(ip, IPWhitelistEntry.TARGET_AS2)) {
                return true;
            }
        }

        // Check partner-specific whitelist
        if (isAllowedByMode(mode, "PARTNER") && partnerAS2Id != null) {
            Partner partner = getPartnerByAS2Id(partnerAS2Id);
            if (partner != null && matchesPartnerWhitelist(ip, partner.getDBId())) {
                return true;
            }
        }

        // If mode is GLOBAL_ONLY and not matched, block
        if ("GLOBAL_ONLY".equals(mode)) {
            return false;
        }

        // If mode is PARTNER_ONLY and not matched (or no partner), block
        if ("PARTNER_ONLY".equals(mode)) {
            return false;
        }

        // Default: block if whitelist enabled but no match
        return false;
    }

    /**
     * Check if IP is allowed for Tracker endpoint
     */
    public boolean isAllowedForTracker(String ip) {
        PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
        if (!"true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_TRACKER))) {
            return true; // Whitelist disabled, allow all
        }

        // Tracker only uses global whitelist
        return matchesGlobalWhitelist(ip, IPWhitelistEntry.TARGET_TRACKER);
    }

    /**
     * Check if IP is allowed for WebUI
     */
    public boolean isAllowedForWebUI(String ip, int userId) {
        LOGGER.warning("DEBUG: Checking WebUI access for IP: " + ip + ", userId: " + userId);

        PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
        if (!"true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_WEBUI))) {
            LOGGER.warning("DEBUG: WebUI whitelist is disabled, allowing access");
            return true; // Whitelist disabled, allow all
        }

        LOGGER.warning("DEBUG: WebUI whitelist is enabled");

        // Always allow localhost (important for first deployment)
        if (isLocalhost(ip)) {
            LOGGER.warning("DEBUG: Allowing localhost IP for WebUI: " + ip);
            return true;
        }

        String mode = prefs.get(PreferencesAS2.IP_WHITELIST_MODE);
        LOGGER.warning("DEBUG: Whitelist mode: " + mode);

        // Check global whitelist
        if (isAllowedByMode(mode, "GLOBAL")) {
            if (matchesGlobalWhitelist(ip, IPWhitelistEntry.TARGET_WEBUI)) {
                LOGGER.warning("DEBUG: IP " + ip + " matched global whitelist");
                return true;
            }
        }

        // Check user-specific whitelist
        if (isAllowedByMode(mode, "USER") && matchesUserWhitelist(ip, userId)) {
            LOGGER.warning("DEBUG: IP " + ip + " matched user-specific whitelist");
            return true;
        }

        // Default: block
        LOGGER.warning("DEBUG: IP " + ip + " blocked by WebUI whitelist (no match found)");
        return false;
    }

    /**
     * Check if IP is allowed for API
     */
    public boolean isAllowedForAPI(String ip, int userId) {
        PreferencesAS2 prefs = new PreferencesAS2(dbDriverManager);
        if (!"true".equals(prefs.get(PreferencesAS2.IP_WHITELIST_ENABLED_API))) {
            return true; // Whitelist disabled, allow all
        }

        // Always allow localhost (important for first deployment)
        if (isLocalhost(ip)) {
            return true;
        }

        String mode = prefs.get(PreferencesAS2.IP_WHITELIST_MODE);

        // Check global whitelist
        if (isAllowedByMode(mode, "GLOBAL")) {
            if (matchesGlobalWhitelist(ip, IPWhitelistEntry.TARGET_API)) {
                return true;
            }
        }

        // Check user-specific whitelist
        if (isAllowedByMode(mode, "USER") && matchesUserWhitelist(ip, userId)) {
            return true;
        }

        // Default: block
        return false;
    }

    /**
     * Log a blocked attempt
     */
    public void logBlockedAttempt(String ip, String targetType, String attemptedUser,
                                   String attemptedPartner, String userAgent, String requestPath) {
        try {
            IPWhitelistBlockLog log = new IPWhitelistBlockLog();
            log.setBlockedIp(ip);
            log.setTargetType(targetType);
            log.setAttemptedUser(attemptedUser);
            log.setAttemptedPartner(attemptedPartner);
            log.setUserAgent(userAgent);
            log.setRequestPath(requestPath);

            accessDB.logBlockedAttempt(log);

            LOGGER.warning("IP blocked by whitelist: " + ip +
                    " (target=" + targetType +
                    (attemptedUser != null ? ", user=" + attemptedUser : "") +
                    (attemptedPartner != null ? ", partner=" + attemptedPartner : "") +
                    ")");

        } catch (Exception e) {
            LOGGER.warning("Failed to log blocked IP attempt: " + e.getMessage());
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * Check if IP is localhost
     * Supports IPv4 (127.0.0.1) and IPv6 (::1, 0:0:0:0:0:0:0:1) localhost addresses
     */
    private boolean isLocalhost(String ip) {
        if (ip == null) {
            LOGGER.warning("isLocalhost: IP is null");
            return false;
        }

        // Normalize the IP for comparison
        // Remove brackets that are sometimes added to IPv6 addresses (e.g., [::1] -> ::1)
        String normalized = ip.trim().toLowerCase();
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        LOGGER.info("isLocalhost: Checking IP [" + ip + "], normalized: [" + normalized + "]");

        // IPv4 localhost
        if (normalized.equals("127.0.0.1") || normalized.startsWith("127.")) {
            LOGGER.info("IP " + ip + " identified as localhost (IPv4)");
            return true;
        }

        // IPv6 localhost (various forms)
        // ::1 (compressed)
        // 0:0:0:0:0:0:0:1 (expanded)
        // 0000:0000:0000:0000:0000:0000:0000:0001 (full)
        if (normalized.equals("::1") ||
            normalized.equals("0:0:0:0:0:0:0:1") ||
            normalized.equals("0000:0000:0000:0000:0000:0000:0000:0001")) {
            LOGGER.info("IP " + ip + " identified as localhost (IPv6)");
            return true;
        }

        LOGGER.warning("IP [" + ip + "] is NOT localhost - normalized: [" + normalized + "]");
        return false;
    }

    /**
     * Check if mode allows checking a specific whitelist type
     */
    private boolean isAllowedByMode(String mode, String type) {
        if (mode == null) {
            mode = "GLOBAL_AND_SPECIFIC"; // Default
        }

        switch (mode) {
            case "GLOBAL_ONLY":
                return "GLOBAL".equals(type);
            case "PARTNER_ONLY":
                return "PARTNER".equals(type);
            case "USER_ONLY":
                return "USER".equals(type);
            case "GLOBAL_AND_SPECIFIC":
                return true; // Allow all types
            default:
                return true;
        }
    }

    /**
     * Check if IP matches global whitelist for target type
     */
    private boolean matchesGlobalWhitelist(String ip, String targetType) {
        // Check specific target type
        List<IPWhitelistEntry> entries = globalCache.get(targetType);
        if (entries != null && matchesAnyEntry(ip, entries)) {
            return true;
        }

        // Check TARGET_ALL
        entries = globalCache.get(IPWhitelistEntry.TARGET_ALL);
        return entries != null && matchesAnyEntry(ip, entries);
    }

    /**
     * Check if IP matches partner-specific whitelist
     */
    private boolean matchesPartnerWhitelist(String ip, int partnerId) {
        // Load from cache or database
        List<IPWhitelistEntry> entries = partnerCache.computeIfAbsent(partnerId,
                id -> accessDB.getPartnerWhitelist(id));

        return matchesAnyEntry(ip, entries);
    }

    /**
     * Check if IP matches user-specific whitelist
     */
    private boolean matchesUserWhitelist(String ip, int userId) {
        // Load from cache or database
        List<IPWhitelistEntry> entries = userCache.computeIfAbsent(userId,
                id -> accessDB.getUserWhitelist(id));

        return matchesAnyEntry(ip, entries);
    }

    /**
     * Check if IP matches any entry in the list
     */
    private boolean matchesAnyEntry(String ip, List<IPWhitelistEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        for (IPWhitelistEntry entry : entries) {
            if (!entry.isEnabled()) {
                continue; // Skip disabled entries
            }

            if (IPWhitelistMatcher.matches(ip, entry.getIpPattern())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get partner by AS2 identifier
     */
    private Partner getPartnerByAS2Id(String as2Id) {
        try {
            PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
            return partnerAccess.getPartner(as2Id);
        } catch (Exception e) {
            LOGGER.warning("Failed to get partner by AS2 ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Shutdown the service
     */
    public void shutdown() {
        cacheRefreshExecutor.shutdown();
        try {
            if (!cacheRefreshExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cacheRefreshExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cacheRefreshExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
