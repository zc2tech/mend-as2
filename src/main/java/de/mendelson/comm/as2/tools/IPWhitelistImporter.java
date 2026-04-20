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

package de.mendelson.comm.as2.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mendelson.comm.as2.AS2Properties;
import de.mendelson.comm.as2.database.DBDriverManagerMySQL;
import de.mendelson.comm.as2.database.DBDriverManagerPostgreSQL;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command-line utility to import IP CIDR entries from JSON file to Global
 * Whitelist
 *
 * Usage:
 * java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter
 * <json_file> [target_type]
 *
 * Examples:
 * java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter
 * public_ip_cidr.json
 * java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter
 * public_ip_cidr.json AS2
 *
 * Target types: AS2, TRACKER, WEBUI, API, ALL (default: ALL)
 */
public class IPWhitelistImporter {

    private static final String[] VALID_TARGET_TYPES = { "AS2", "TRACKER", "WEBUI", "API", "ALL" };

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String jsonFile = args[0];
        String targetType = args.length > 1 ? args[1].toUpperCase() : "ALL";

        // Validate target type
        if (!Arrays.asList(VALID_TARGET_TYPES).contains(targetType)) {
            System.err.println("ERROR: Invalid target_type. Must be AS2, TRACKER, WEBUI, API, or ALL");
            System.exit(1);
        }

        // Validate JSON file exists
        File file = new File(jsonFile);
        if (!file.exists()) {
            System.err.println("ERROR: JSON file not found: " + jsonFile);
            System.exit(1);
        }

        System.out.println("IP Whitelist Import Utility");
        System.out.println("============================");
        System.out.println("JSON File: " + file.getAbsolutePath());
        System.out.println("Target Type: " + targetType);
        System.out.println();

        try {
            // Get database driver manager
            AS2Properties config = AS2Properties.getInstance();
            String dbType = config.getDatabaseType();

            System.out.println("Database Type: " + dbType);

            // Explicitly load JDBC driver (needed for standalone execution)
            try {
                if ("mysql".equals(dbType)) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } else {
                    Class.forName("org.postgresql.Driver");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("ERROR: JDBC driver not found: " + e.getMessage());
                System.err.println("Make sure the database driver is in the classpath.");
                System.exit(1);
            }

            IDBDriverManager dbDriverManager = null;
            if ("mysql".equals(dbType)) {
                dbDriverManager = DBDriverManagerMySQL.instance();
            } else {
                dbDriverManager = DBDriverManagerPostgreSQL.instance();
            }

            // Display database connection info
            String dbHost = config.getString(
                "mysql".equals(dbType) ? "mysql.host" : "postgresql.host",
                "mysql".equals(dbType) ? "MYSQL_HOST" : "POSTGRES_HOST",
                "localhost"
            );
            String dbPort = config.getString(
                "mysql".equals(dbType) ? "mysql.port" : "postgresql.port",
                "mysql".equals(dbType) ? "MYSQL_PORT" : "POSTGRES_PORT",
                "mysql".equals(dbType) ? "3306" : "5432"
            );
            String dbName = config.getString(
                "mysql".equals(dbType) ? "mysql.db.config" : "postgresql.db.config",
                "mysql".equals(dbType) ? "MYSQL_DB_CONFIG" : "POSTGRES_DB_CONFIG",
                "as2_db_config"
            );
            String dbUser = config.getString(
                "mysql".equals(dbType) ? "mysql.user" : "postgresql.user",
                "mysql".equals(dbType) ? "MYSQL_USER" : "POSTGRES_USER",
                "as2user"
            );

            System.out.println("Database: " + dbHost + ":" + dbPort + "/" + dbName);
            System.out.println("Username: " + dbUser);
            System.out.println();

            // Parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);

            if (!rootNode.isArray()) {
                System.err.println("ERROR: JSON file must contain an array of entries");
                System.exit(1);
            }

            int totalEntries = rootNode.size();
            System.out.println("Found " + totalEntries + " CIDR entries in JSON file");
            System.out.println();

            // Determine target types to import
            List<String> targetTypes = new ArrayList<>();
            if ("ALL".equals(targetType)) {
                targetTypes.addAll(Arrays.asList("AS2", "TRACKER", "WEBUI", "API"));
            } else {
                targetTypes.add(targetType);
            }

            System.out.println("Will import for: " + String.join(", ", targetTypes));
            System.out.println();

            // Confirmation
            System.out.print("Continue? (yes/no): ");
            String confirm = System.console() != null ? System.console().readLine() : "yes";
            if (!"yes".equalsIgnoreCase(confirm.trim())) {
                System.out.println("Import cancelled");
                System.exit(0);
            }

            System.out.println();

            // Import for each target type
            long startTime = System.currentTimeMillis();

            for (String target : targetTypes) {
                System.out.println("Importing for target type: " + target);
                int imported = importForTarget(dbDriverManager, rootNode, target);
                System.out.println("  Imported/Updated: " + imported + " entries");
                System.out.println();
            }

            long endTime = System.currentTimeMillis();
            double durationSeconds = (endTime - startTime) / 1000.0;

            System.out.println("Import complete!");
            System.out.println("Total time: " + String.format("%.2f", durationSeconds) + " seconds");
            System.out.println("Average: " + String.format("%.0f", totalEntries / durationSeconds) + " entries/second");
            System.out.println();
            System.out.println("You can verify the import by:");
            System.out.println("  - Open WebUI → System → IP Whitelist → Global tab");
            System.out.println(
                    "  - Or run SQL: SELECT COUNT(*) FROM ip_whitelist_global WHERE created_by='import-tool';");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Import entries for a specific target type using batch processing
     */
    private static int importForTarget(IDBDriverManager dbDriverManager, JsonNode entries, String targetType)
            throws Exception {

        String sql = "INSERT INTO ip_whitelist_global (ip_pattern, description, target_type, enabled, created_by) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE description=VALUES(description)"; // MySQL syntax

        // Try PostgreSQL syntax if MySQL fails
        String sqlPostgres = "INSERT INTO ip_whitelist_global (ip_pattern, description, target_type, enabled, created_by) "
                +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (ip_pattern, target_type) DO UPDATE SET description=EXCLUDED.description";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            conn.setAutoCommit(false); // Use transaction for better performance

            // Try MySQL syntax first
            try {
                pstmt = conn.prepareStatement(sql);
            } catch (SQLException e) {
                // If MySQL syntax fails, try PostgreSQL
                pstmt = conn.prepareStatement(sqlPostgres);
            }

            int batchSize = 100; // Process in batches
            int count = 0;
            int imported = 0;
            int totalEntries = entries.size();

            for (JsonNode entry : entries) {
                String cidr = entry.get("cidr").asText();
                String country = entry.has("country") ? entry.get("country").asText() : "Unknown";
                String location = entry.has("location") ? entry.get("location").asText() : "Unknown";
                String building = entry.has("building") ? entry.get("building").asText() : "";

                // Convert IP range to wildcard pattern if needed
                String ipPattern = convertToWildcardPattern(cidr);

                String description = country + " - " + location +
                        (building.isEmpty() ? "" : " (" + building + ")");

                pstmt.setString(1, ipPattern);
                pstmt.setString(2, description);
                pstmt.setString(3, targetType);
                pstmt.setBoolean(4, true);
                pstmt.setString(5, "import-tool");
                pstmt.addBatch();

                count++;

                // Execute batch every 100 entries
                if (count % batchSize == 0) {
                    int[] results = pstmt.executeBatch();
                    imported += results.length;
                    conn.commit();

                    // Show progress every 50 entries
                    if (count % 50 == 0) {
                        System.out.println("  Progress: " + count + "/" + totalEntries +
                                " (" + String.format("%.1f%%", (count * 100.0 / totalEntries)) + ")");
                    }
                }
            }

            // Execute remaining batch
            if (count % batchSize != 0) {
                int[] results = pstmt.executeBatch();
                imported += results.length;
                conn.commit();
            }

            // Show final progress if not already shown
            if (count % 50 != 0) {
                System.out.println("  Progress: " + count + "/" + totalEntries + " (100.0%)");
            }

            return imported;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Ignore
                }
            }
            throw e;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("IP Whitelist Import Utility");
        System.out.println();
        System.out.println("Usage:");
        System.out.println(
                "  java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter <json_file> [target_type]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  json_file    Path to JSON file with CIDR entries");
        System.out.println("  target_type  AS2|TRACKER|WEBUI|API|ALL (default: ALL)");
        System.out.println();
        System.out.println("Examples:");
        System.out
                .println("  java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter public_ip_cidr.json");
        System.out.println(
                "  java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter public_ip_cidr.json AS2");
    }

    /**
     * Convert CIDR notation or IP range to wildcard pattern
     *
     * Examples:
     * - "106.120.93.35 - 106.120.93.36" -> "106.120.93.*"
     * - "12.3.5.5 - 12.3.6.7" -> "12.3.*"
     * - "10.0.0.1 - 10.255.255.254" -> "10.*"
     * - "192.168.1.0/24" -> "192.168.1.0/24" (unchanged - keep CIDR notation)
     * - "192.168.1.100" -> "192.168.1.100" (unchanged)
     */
    private static String convertToWildcardPattern(String cidr) {
        if (cidr == null || cidr.trim().isEmpty()) {
            return cidr;
        }

        cidr = cidr.trim();

        // Case 1: IP range (contains " - ") - convert to wildcard
        if (cidr.contains(" - ")) {
            return convertRangeToWildcard(cidr);
        }

        // Case 2: CIDR notation or single IP - keep as-is
        return cidr;
    }

    /**
     * Convert IP range to wildcard pattern
     * Example: "106.120.93.35 - 106.120.93.36" -> "106.120.93.*"
     */
    private static String convertRangeToWildcard(String range) {
        try {
            String[] parts = range.split(" - ");
            if (parts.length != 2) {
                System.err.println("WARNING: Invalid IP range format: " + range);
                return range;
            }

            String startIP = parts[0].trim();
            String endIP = parts[1].trim();

            String[] startOctets = startIP.split("\\.");
            String[] endOctets = endIP.split("\\.");

            if (startOctets.length != 4 || endOctets.length != 4) {
                System.err.println("WARNING: Invalid IP format in range: " + range);
                return range;
            }

            // Find the common prefix octets
            StringBuilder pattern = new StringBuilder();
            int commonOctets = 0;

            for (int i = 0; i < 4; i++) {
                if (startOctets[i].equals(endOctets[i])) {
                    if (commonOctets > 0) {
                        pattern.append(".");
                    }
                    pattern.append(startOctets[i]);
                    commonOctets++;
                } else {
                    break;
                }
            }

            // Add wildcard for remaining octets
            if (commonOctets < 4) {
                if (commonOctets > 0) {
                    pattern.append(".");
                }
                pattern.append("*");
            }

            return pattern.toString();

        } catch (Exception e) {
            System.err.println("WARNING: Failed to convert range to wildcard: " + range + " - " + e.getMessage());
            return range;
        }
    }
}
