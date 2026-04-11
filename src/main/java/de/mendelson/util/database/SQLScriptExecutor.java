package de.mendelson.util.database;

import de.mendelson.IProductVersion;
import de.mendelson.util.ConsoleProgressBar;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Class that can execute SQL scripts or execute predefined commands which are
 * assigned to scripts
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class SQLScriptExecutor {

    // Legacy paths - kept for backward compatibility, but point to PostgreSQL
    public static final String SCRIPT_RESOURCE_CONFIG = "/sqlscript/postgres/config/";
    public static final String SCRIPT_RESOURCE_RUNTIME = "/sqlscript/postgres/runtime/";

    // Database-specific paths
    public static final String SCRIPT_RESOURCE_CONFIG_POSTGRES = "/sqlscript/postgres/config/";
    public static final String SCRIPT_RESOURCE_RUNTIME_POSTGRES = "/sqlscript/postgres/runtime/";
    public static final String SCRIPT_RESOURCE_CONFIG_MYSQL = "/sqlscript/mysql/config/";
    public static final String SCRIPT_RESOURCE_RUNTIME_MYSQL = "/sqlscript/mysql/runtime/";

    private ISQLQueryModifier queryModifier = null;
    private final static boolean DISPLAY_QUERY = false;

    /**
     * Creates new SQLScriptExecutor
     */
    public SQLScriptExecutor() {
    }

    public void setQueryModifier(ISQLQueryModifier queryModifier) {
        this.queryModifier = queryModifier;
    }

    /**
     * creates a new database
     *
     * @param connection connection to the database
     * @param RESOURCE resource type as defined in the class
     */
    public void create(Connection connection, final String RESOURCE, int dbVersion,
            IProductVersion productVersion) throws Exception {
        this.executeScript(connection, RESOURCE + "CREATE.sql");
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO version(actualversion,updatedate,updatecomment)VALUES(?,?,?)")) {
            statement.setInt(1, dbVersion);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()), 
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            statement.setString(3, productVersion.getFullName() + ": initial installation");
            statement.executeUpdate();
        }
    }

    /**
     * Checks if the resource exist
     *
     * @param resource Resource to check for existence
     */
    public boolean resourceExists(String resource) {
        try (InputStream is = SQLScriptExecutor.class.getResourceAsStream(resource)) {
            return (is != null);
        } catch (Exception e) {
            return (false);
        }
    }

    /**
     * Executes a SQL script to make changes to the database
     *
     * @param resource FULL Resource of the sql script, e.g.
     * "/sqlscript/config/script.sql"
     * @param connection connection to the database
     * @return true if everything worked fine
     */
    public void executeScript(Connection connection, String resource) throws Exception {
        try (InputStream is = SQLScriptExecutor.class.getResourceAsStream(resource)) {
            if (is == null) {
                String text = "SQLScriptExecutor: Resource " + resource + " not found";
                throw new Exception(text);
            }
            this.executeScript(connection, is);
        }
    }

    /**
     * Executes a SQL script to make changes to the database
     *
     * @param connection connection to the database
     * @return true if everything worked fine
     */
    private void executeScript(Connection connection, InputStream is) throws Exception {
        ConsoleProgressBar.print(0f);
        List<String> queryList = new ArrayList<String>();
        StringBuilder currentQuery = new StringBuilder();
        String line = "";
        boolean inDollarQuote = false; // Track if we're inside $$ ... $$

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    String trimmedLine = line.trim();

                    // Skip empty lines and comments (only if not inside dollar quotes)
                    if (!inDollarQuote && (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("--"))) {
                        continue;
                    }

                    // Remove inline comments (-- ...) unless we're inside dollar quotes
                    if (!inDollarQuote) {
                        int commentIndex = trimmedLine.indexOf("--");
                        if (commentIndex >= 0) {
                            // Check if -- is inside a string literal (single quotes)
                            // Simple check: count single quotes before --
                            String beforeComment = trimmedLine.substring(0, commentIndex);
                            int singleQuoteCount = 0;
                            for (int i = 0; i < beforeComment.length(); i++) {
                                if (beforeComment.charAt(i) == '\'') {
                                    singleQuoteCount++;
                                }
                            }
                            // If even number of quotes, -- is outside string, so it's a comment
                            if (singleQuoteCount % 2 == 0) {
                                trimmedLine = trimmedLine.substring(0, commentIndex).trim();
                            }
                        }
                    }

                    // Skip if line became empty after removing comment
                    if (trimmedLine.isEmpty()) {
                        continue;
                    }

                    // Append line to current query
                    if (currentQuery.length() > 0) {
                        currentQuery.append(" ");
                    }
                    currentQuery.append(trimmedLine);

                    // Check for dollar-quote delimiters ($$)
                    // Count occurrences of $$ to track entry/exit
                    int dollarCount = 0;
                    int index = 0;
                    while ((index = trimmedLine.indexOf("$$", index)) != -1) {
                        dollarCount++;
                        index += 2; // Move past the $$
                    }

                    // Toggle inDollarQuote state for each $$
                    if (dollarCount % 2 == 1) {
                        inDollarQuote = !inDollarQuote;
                    }

                    // Check if the line ends with a semicolon (statement terminator)
                    // Only split if we're NOT inside dollar quotes
                    if (!inDollarQuote && trimmedLine.endsWith(";")) {
                        // Remove the semicolon and add the complete query to the list
                        String completeQuery = currentQuery.toString();
                        if (completeQuery.endsWith(";")) {
                            completeQuery = completeQuery.substring(0, completeQuery.length() - 1).trim();
                        }
                        if (!completeQuery.isEmpty()) {
                            queryList.add(completeQuery);
                        }
                        currentQuery.setLength(0); // Reset for next query
                    }
                }
            }
            // Handle any remaining query that doesn't end with semicolon
            if (currentQuery.length() > 0) {
                String remainingQuery = currentQuery.toString().trim();
                if (!remainingQuery.isEmpty()) {
                    queryList.add(remainingQuery);
                }
            }
        }
        int counter = 0;
        for (String query : queryList) {
            if( DISPLAY_QUERY ){
                System.out.println("Update process: Using unmodified query " + query);
            }
            counter++;
            String modifiedQuery;
            if (this.queryModifier != null) {
                modifiedQuery = this.queryModifier.modifyQuery(query);
            } else {
                modifiedQuery = query;
            }
            float percent = ((float) counter / (float) queryList.size()) * 100f;
            ConsoleProgressBar.print(percent);
            //skip the query if this is made empty by the query modifier. In this case the query is not required by
            //the specified database system
            if( modifiedQuery == null || modifiedQuery.trim().length() == 0){
                continue;
            }
            if( DISPLAY_QUERY ){
                System.out.println("Update process: Using DB specific query " + modifiedQuery);
            }
            try (PreparedStatement statement = connection.prepareStatement(modifiedQuery)) {
                statement.executeUpdate();
            }
        }
        System.out.println();
    }
}
