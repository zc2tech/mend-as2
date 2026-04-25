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

package de.mendelson.comm.as2.usermanagement;

import de.mendelson.comm.as2.database.DBDriverManagerMySQL;
import de.mendelson.comm.as2.database.DBDriverManagerPostgreSQL;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.PBKDF2;
import java.io.Console;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command-line tool to reset the admin user's password
 * Use this when admin user is locked out and has forgotten their password
 *
 * Usage:
 *   java -cp mend-as2.jar de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
 *
 * Or with Maven:
 *   mvn exec:java -Dexec.mainClass="de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool"
 *
 * @author Julian Xu
 * @version 1.0
 */
public class AdminPasswordResetTool {

    private static final Logger logger = Logger.getLogger(AdminPasswordResetTool.class.getName());

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  AS2 Admin Password Reset Tool");
        System.out.println("==============================================");
        System.out.println();

        // Check if server is running
        System.out.println("WARNING: Make sure the AS2 server is NOT running!");
        System.out.println("This tool must be used when the server is stopped.");
        System.out.println();

        System.out.print("Do you want to continue? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Operation cancelled.");
            return;
        }

        System.out.println();

        // Get new password
        String newPassword = null;
        Console console = System.console();

        if (console != null) {
            // Use Console for masked password input
            char[] passwordChars = console.readPassword("Enter new password for admin user: ");
            if (passwordChars == null || passwordChars.length == 0) {
                System.err.println("ERROR: Password cannot be empty");
                return;
            }
            newPassword = new String(passwordChars);

            char[] confirmChars = console.readPassword("Confirm new password: ");
            String confirmPassword = confirmChars != null ? new String(confirmChars) : "";

            if (!newPassword.equals(confirmPassword)) {
                System.err.println("ERROR: Passwords do not match");
                return;
            }
        } else {
            // Fallback for environments without Console (IDE debugging)
            System.out.println("Note: Password will be visible (no Console available)");
            System.out.print("Enter new password for admin user: ");
            newPassword = scanner.nextLine().trim();

            if (newPassword.isEmpty()) {
                System.err.println("ERROR: Password cannot be empty");
                return;
            }

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.err.println("ERROR: Passwords do not match");
                return;
            }
        }

        // Validate password strength
        if (newPassword.length() < 8) {
            System.err.println("ERROR: Password must be at least 8 characters long");
            return;
        }

        System.out.println();
        System.out.println("Resetting admin password...");

        // Reset the password
        IDBDriverManager dbDriverManager = null;
        try {
            // Initialize database driver manager (standalone, no AS2Server dependency)
            dbDriverManager = getDBDriverManager();

            // Check if admin user exists
            if (!adminUserExists(dbDriverManager)) {
                System.err.println("ERROR: Admin user not found in database!");
                System.err.println("This tool can only reset the password for an existing 'admin' user.");
                return;
            }

            // Hash the new password
            String passwordHash = PBKDF2.generateStrongPasswordHash(newPassword);

            // Update password in database
            updateAdminPassword(dbDriverManager, passwordHash);

            System.out.println();
            System.out.println("SUCCESS: Admin password has been reset!");
            System.out.println();
            System.out.println("You can now login with:");
            System.out.println("  Username: admin");
            System.out.println("  Password: (the password you just set)");
            System.out.println();
            System.out.println("IMPORTANT: You will be required to change this password on first login.");
            System.out.println();

        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR: Failed to reset admin password");
            System.err.println("Reason: " + e.getMessage());
            logger.log(Level.SEVERE, "Error resetting admin password", e);
            e.printStackTrace();
        }
        // Note: DBDriverManager doesn't have a shutdown method in standalone mode
    }

    /**
     * Get the database driver manager based on configuration
     * Standalone version - doesn't depend on AS2Server
     */
    private static IDBDriverManager getDBDriverManager() throws Exception {
        String dbType = getDatabaseType();

        System.out.println("Detected database type: " + dbType);

        if ("mysql".equals(dbType)) {
            try {
                return DBDriverManagerMySQL.instance();
            } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
                throw new Exception("MySQL database driver not available. " +
                    "Make sure MySQL JDBC driver is in classpath. " +
                    "Original error: " + e.getMessage(), e);
            }
        } else {
            // Default to PostgreSQL (includes H2 which uses PostgreSQL driver manager)
            try {
                return DBDriverManagerPostgreSQL.instance();
            } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
                throw new Exception("PostgreSQL/H2 database driver not available. " +
                    "Make sure PostgreSQL JDBC driver is in classpath. " +
                    "Original error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Get database type from configuration
     * Priority: ENV > System Property > File > Default (postgresql)
     */
    private static String getDatabaseType() {
        // 1. Environment variable
        String envValue = System.getenv("AS2_DATABASE_TYPE");
        if (envValue != null && !envValue.isEmpty()) {
            return normalizeDbType(envValue);
        }

        // 2. System property
        String sysProp = System.getProperty("as2.database.type");
        if (sysProp != null && !sysProp.isEmpty()) {
            return normalizeDbType(sysProp);
        }

        // 3. Properties file (as2.properties)
        try {
            Properties props = new Properties();
            // Try to load from current directory or config directory
            String[] possiblePaths = {
                "as2.properties",
                "config/as2.properties",
                "../config/as2.properties"
            };

            for (String path : possiblePaths) {
                try (FileInputStream fis = new FileInputStream(path)) {
                    props.load(fis);
                    String propValue = props.getProperty("as2.database.type");
                    if (propValue != null && !propValue.isEmpty()) {
                        return normalizeDbType(propValue);
                    }
                } catch (Exception e) {
                    // Try next path
                }
            }
        } catch (Exception e) {
            // Ignore - will use default
        }

        // 4. Default to PostgreSQL (H2 also uses PostgreSQL driver)
        return "postgresql";
    }

    /**
     * Normalize database type string
     */
    private static String normalizeDbType(String dbType) {
        if (dbType == null) {
            return "postgresql";
        }
        String normalized = dbType.toLowerCase().trim();
        // H2 uses PostgreSQL driver manager
        if (normalized.equals("h2")) {
            return "postgresql";
        }
        return normalized;
    }

    /**
     * Check if admin user exists in database
     */
    private static boolean adminUserExists(IDBDriverManager dbDriverManager) throws Exception {
        String query = "SELECT id FROM webui_users WHERE username = 'admin'";

        try (Connection conn = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }

    /**
     * Update admin user's password in database
     */
    private static void updateAdminPassword(IDBDriverManager dbDriverManager, String passwordHash) throws Exception {
        String updateQuery = "UPDATE webui_users " +
                           "SET password_hash = ?, " +
                           "must_change_password = TRUE, " +
                           "updated_at = ? " +
                           "WHERE username = 'admin'";

        try (Connection conn = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, passwordHash);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("Failed to update admin password - no rows affected");
            }
        }
    }
}
