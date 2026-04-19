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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Utility class for matching IP addresses against whitelist patterns.
 * Supports:
 * - Exact match: "192.168.1.100"
 * - CIDR notation: "10.0.0.0/24", "2001:db8::/32"
 * - Wildcard: "172.16.*", "192.168.1.*"
 * - IPv4 and IPv6
 *
 * @author Julian Xu
 */
public class IPWhitelistMatcher {

    /**
     * Check if an IP address matches a whitelist pattern
     *
     * @param ip      IP address to check (e.g., "192.168.1.100")
     * @param pattern Pattern to match against (e.g., "192.168.1.*" or "10.0.0.0/24")
     * @return true if IP matches pattern
     */
    public static boolean matches(String ip, String pattern) {
        if (ip == null || pattern == null) {
            return false;
        }

        ip = ip.trim();
        pattern = pattern.trim();

        // Exact match
        if (ip.equals(pattern)) {
            return true;
        }

        // CIDR notation
        if (pattern.contains("/")) {
            return matchesCIDR(ip, pattern);
        }

        // Wildcard match
        if (pattern.contains("*")) {
            return matchesWildcard(ip, pattern);
        }

        return false;
    }

    /**
     * Check if IP matches CIDR notation (e.g., "10.0.0.0/24")
     */
    private static boolean matchesCIDR(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            InetAddress ipAddr = InetAddress.getByName(ip);
            InetAddress networkAddr = InetAddress.getByName(networkAddress);

            byte[] ipBytes = ipAddr.getAddress();
            byte[] networkBytes = networkAddr.getAddress();

            // IP version mismatch
            if (ipBytes.length != networkBytes.length) {
                return false;
            }

            // Calculate network mask
            int bits = ipBytes.length * 8;
            if (prefixLength < 0 || prefixLength > bits) {
                return false;
            }

            // Compare bytes
            int fullBytes = prefixLength / 8;
            int remainingBits = prefixLength % 8;

            // Compare full bytes
            for (int i = 0; i < fullBytes; i++) {
                if (ipBytes[i] != networkBytes[i]) {
                    return false;
                }
            }

            // Compare remaining bits
            if (remainingBits > 0) {
                int mask = 0xFF << (8 - remainingBits);
                if ((ipBytes[fullBytes] & mask) != (networkBytes[fullBytes] & mask)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // Invalid IP or CIDR format
            return false;
        }
    }

    /**
     * Check if IP matches wildcard pattern (e.g., "192.168.1.*")
     */
    private static boolean matchesWildcard(String ip, String pattern) {
        // Convert wildcard pattern to regex
        // Escape dots and replace * with regex pattern
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", "\\d+");

        // Add anchors to ensure full string match
        regex = "^" + regex + "$";

        try {
            return Pattern.matches(regex, ip);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate if a pattern is valid
     *
     * @param pattern Pattern to validate
     * @return true if pattern is valid
     */
    public static boolean isValidPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return false;
        }

        pattern = pattern.trim();

        // Try exact IP address
        try {
            InetAddress.getByName(pattern);
            return true;
        } catch (UnknownHostException e) {
            // Not a valid IP, continue checking
        }

        // Try CIDR notation
        if (pattern.contains("/")) {
            return isValidCIDR(pattern);
        }

        // Try wildcard
        if (pattern.contains("*")) {
            return isValidWildcard(pattern);
        }

        return false;
    }

    /**
     * Validate CIDR notation
     */
    private static boolean isValidCIDR(String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            InetAddress addr = InetAddress.getByName(parts[0]);
            int prefixLength = Integer.parseInt(parts[1]);
            int maxPrefix = addr.getAddress().length * 8;

            return prefixLength >= 0 && prefixLength <= maxPrefix;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate wildcard pattern
     */
    private static boolean isValidWildcard(String pattern) {
        // Wildcard can only be used in place of octets
        // Valid: "192.168.*", "192.168.1.*", "*.*.1.100"
        // Invalid: "192.16*.1.1", "192.168.1.10*"

        String[] parts = pattern.split("\\.");

        // IPv4 should have 4 parts
        if (parts.length != 4) {
            // Could be IPv6 with colons, but we'll keep it simple for now
            return false;
        }

        for (String part : parts) {
            if (part.equals("*")) {
                continue;
            }

            // Part should be a valid octet (0-255)
            try {
                int octet = Integer.parseInt(part);
                if (octet < 0 || octet > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a human-readable description of a pattern
     */
    public static String getPatternDescription(String pattern) {
        if (pattern == null) {
            return "Invalid pattern";
        }

        pattern = pattern.trim();

        // Exact IP
        try {
            InetAddress addr = InetAddress.getByName(pattern);
            if (addr.getAddress().length == 4) {
                return "Single IPv4 address: " + pattern;
            } else {
                return "Single IPv6 address: " + pattern;
            }
        } catch (UnknownHostException e) {
            // Not exact IP
        }

        // CIDR
        if (pattern.contains("/")) {
            String[] parts = pattern.split("/");
            if (parts.length == 2) {
                try {
                    InetAddress addr = InetAddress.getByName(parts[0]);
                    int prefix = Integer.parseInt(parts[1]);
                    int maxHosts = (int) Math.pow(2, (addr.getAddress().length * 8) - prefix);
                    return "IP range (CIDR): " + pattern + " (" + maxHosts + " addresses)";
                } catch (Exception e) {
                    // Invalid CIDR
                }
            }
        }

        // Wildcard
        if (pattern.contains("*")) {
            int wildcardCount = pattern.length() - pattern.replace("*", "").length();
            int possibleHosts = (int) Math.pow(256, wildcardCount);
            return "IP range (wildcard): " + pattern + " (~" + possibleHosts + " addresses)";
        }

        return "Unknown pattern: " + pattern;
    }
}
