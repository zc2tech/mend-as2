//$Header: /as4/de/mendelson/util/database/SQLScriptExecutor.java 3     16/12/24 9:51 Heller $
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
/**
 * Class that can execute SQL scripts or execute predefined commands which are
 * assigned to scripts
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class SQLScriptExecutor {

    public static final String SCRIPT_RESOURCE_CONFIG = "/sqlscript/config/";
    public static final String SCRIPT_RESOURCE_RUNTIME = "/sqlscript/runtime/";
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
        String line = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while (line != null) {
                line = reader.readLine();
                if (line != null && !line.trim().isEmpty() && (!line.startsWith("#"))) {
                    queryList.add(line);
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
