/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.usermanagement;

/**
 * Shared username validation utility for both WebUI and SwingUI.
 * Ensures consistent validation rules across all interfaces.
 *
 * Username requirements:
 * - Length: 3-50 characters
 * - Characters: alphanumeric (a-z, A-Z, 0-9), underscore (_), hyphen (-), dot (.)
 * - Must start with alphanumeric character
 * - Must end with alphanumeric character
 * - Cannot contain consecutive special characters (e.g., "..", "--", "__")
 * - Cannot be reserved names (admin, root, system, etc.)
 * - Protected against SQL injection, path traversal, and URL encoding attacks
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class UsernameValidator {

    private static final String[] RESERVED_NAMES = {
        "admin", "root", "system", "administrator", "superuser", "webmaster",
        "api", "null", "undefined", "test", "demo", "guest", "public",
        "server", "database", "backup", "config", "default"
    };

    private static final String[] SQL_KEYWORDS = {
        "select", "insert", "update", "delete", "drop", "union", "exec", "script"
    };

    /**
     * Validates username format for security and URL safety.
     *
     * @param username The username to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateUsername(String username) {
        if (username == null) {
            return "Username is required";
        }

        String trimmed = username.trim();

        // Length check
        if (trimmed.length() < 3) {
            return "Username must be at least 3 characters long";
        }
        if (trimmed.length() > 50) {
            return "Username must not exceed 50 characters";
        }

        // Character set validation (alphanumeric, underscore, hyphen, dot)
        if (!trimmed.matches("^[a-zA-Z0-9._-]+$")) {
            return "Username can only contain letters, numbers, underscore (_), hyphen (-), and dot (.)";
        }

        // Must start and end with alphanumeric
        if (!trimmed.matches("^[a-zA-Z0-9].*[a-zA-Z0-9]$")) {
            return "Username must start and end with a letter or number";
        }

        // No consecutive special characters
        if (trimmed.matches(".*[._-]{2,}.*")) {
            return "Username cannot contain consecutive special characters (e.g., '..',  '--', '__')";
        }

        // Reserved names check (case-insensitive)
        String lowerUsername = trimmed.toLowerCase();
        for (String reserved : RESERVED_NAMES) {
            if (lowerUsername.equals(reserved)) {
                return "Username '" + trimmed + "' is reserved and cannot be used";
            }
        }

        // SQL injection protection - basic check for SQL keywords
        for (String keyword : SQL_KEYWORDS) {
            if (lowerUsername.contains(keyword)) {
                return "Username contains invalid keyword: " + keyword;
            }
        }

        // Path traversal protection
        if (trimmed.contains("..") || trimmed.contains("./") || trimmed.contains(".\\")) {
            return "Username cannot contain path traversal patterns";
        }

        // URL encoding check - no percent signs
        if (trimmed.contains("%")) {
            return "Username cannot contain URL-encoded characters";
        }

        return null; // Valid username
    }

    /**
     * Check if username is valid (for boolean checks)
     *
     * @param username The username to validate
     * @return true if valid, false if invalid
     */
    public static boolean isValid(String username) {
        return validateUsername(username) == null;
    }

    /**
     * Get validation requirements as a formatted string for display
     *
     * @return String describing username requirements
     */
    public static String getRequirements() {
        return "Username requirements:\n" +
               "- Length: 3-50 characters\n" +
               "- Allowed characters: letters, numbers, underscore (_), hyphen (-), dot (.)\n" +
               "- Must start and end with a letter or number\n" +
               "- Cannot contain consecutive special characters (e.g., '..',  '--', '__')\n" +
               "- Cannot be reserved names (admin, root, system, etc.)";
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UsernameValidator() {
    }
}
