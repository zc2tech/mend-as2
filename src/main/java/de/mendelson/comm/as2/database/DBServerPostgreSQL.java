package de.mendelson.comm.as2.database;

import de.mendelson.util.database.IUpdater;
import de.mendelson.util.database.IDBServer;
import de.mendelson.util.database.DBClientInformation;
import de.mendelson.util.database.DBServerInformation;
import de.mendelson.util.database.SQLScriptExecutor;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.database.ISQLQueryModifier;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Logger;

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
 * PostgreSQL database server wrapper. Unlike HSQLDB, PostgreSQL runs as an
 * external server process, so this class mainly handles database initialization,
 * version checking, and updates.
 *
 * @author S.Heller
 * @version $Revision: 4 $
 * @since build 70
 */
public class DBServerPostgreSQL implements IDBServer {

    /**
     * Resourcebundle to localize messages of the DB server
     */
    private static final MecResourceBundle rb;
    /**
     * Log messages
     */
    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final static String MODULE_NAME;

    private final DBDriverManagerPostgreSQL dbDriverManager;
    private DBServerInformation dbServerInformation = new DBServerInformation();
    private DBClientInformation dbClientInformation = new DBClientInformation();

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDBServer.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        MODULE_NAME = rb.getResourceString("module.name");
    }

    /**
     * Initialize PostgreSQL database server wrapper
     */
    public DBServerPostgreSQL(IDBDriverManager driverManager,
            DBServerInformation dbServerInformation,
            DBClientInformation dbClientInformation) throws Exception {
        if (dbServerInformation != null) {
            this.dbServerInformation = dbServerInformation;
        }
        if (dbClientInformation != null) {
            this.dbClientInformation = dbClientInformation;
        }
        this.dbDriverManager = (DBDriverManagerPostgreSQL) driverManager;
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        return ("1.0");
    }

    /**
     * Returns the product information of the database
     */
    @Override
    public DBServerInformation getDBServerInformation() {
        return (this.dbServerInformation);
    }

    /**
     * Returns the product information of the driver
     */
    @Override
    public DBClientInformation getDBClientInformation() {
        return (this.dbClientInformation);
    }

    /**
     * Ensure PostgreSQL server is accessible and databases are initialized.
     * Unlike HSQLDB, PostgreSQL runs as an external service.
     */
    @Override
    public void ensureServerIsRunning() throws Exception {
        PostgreSQLConfig config = PostgreSQLConfig.getInstance();

        SystemEventManagerImplAS2.instance().newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_DATABASE_SERVER_STARTUP_BEGIN,
                rb.getResourceString("dbserver.startup"),
                "Connecting to external PostgreSQL server at " + config.getHost() + ":" + config.getPort());

        StringBuilder startupLog = new StringBuilder();
        startupLog.append("PostgreSQL Configuration:\n");
        startupLog.append("  Host: ").append(config.getHost()).append("\n");
        startupLog.append("  Port: ").append(config.getPort()).append("\n");
        startupLog.append("  Config DB: ").append(config.getConfigDatabase()).append("\n");
        startupLog.append("  Runtime DB: ").append(config.getRuntimeDatabase()).append("\n");

        try {
            this.createCheck();
        } catch (Exception e) {
            this.logger.severe(MODULE_NAME + " " + e.getMessage());
            throw e;
        }

        // PostgreSQL doesn't need defragmentation like HSQLDB
        // Use VACUUM command if needed (typically done by autovacuum)

        Connection configConnection = null;
        Connection runtimeConnection = null;
        try {
            configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            if (configConnection == null) {
                throw new Exception("Cannot connect to CONFIG database");
            }

            // PostgreSQL specific optimizations (optional)
            try (Statement statement = configConnection.createStatement()) {
                // Analyze tables for better query planning
                statement.execute("ANALYZE");
            } catch (Exception e) {
                this.logger.warning("Could not run ANALYZE on CONFIG database: " + e.getMessage());
            }

            // Check if a DB update is necessary
            this.updateDB(IDBDriverManager.DB_CONFIG);

            runtimeConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            if (runtimeConnection == null) {
                throw new Exception("Cannot connect to RUNTIME database");
            }

            try (Statement statement = runtimeConnection.createStatement()) {
                statement.execute("ANALYZE");
            } catch (Exception e) {
                this.logger.warning("Could not run ANALYZE on RUNTIME database: " + e.getMessage());
            }

            // Check if a runtime DB update is necessary
            this.updateDB(IDBDriverManager.DB_RUNTIME);

            DatabaseMetaData data = runtimeConnection.getMetaData();
            this.dbServerInformation.setHost(config.getHost());
            this.dbServerInformation.setProductName(data.getDatabaseProductName());
            this.dbServerInformation.setProductVersion(data.getDatabaseProductVersion());
            this.dbServerInformation.setJDBCVersion(data.getJDBCMajorVersion() + "." + data.getJDBCMinorVersion());

            this.dbClientInformation.setProductName(data.getDriverName());
            this.dbClientInformation.setProductVersion(data.getDriverVersion());

            String runningMessage = rb.getResourceString("dbserver.running.external",
                    new Object[]{data.getDatabaseProductName() + " " + data.getDatabaseProductVersion()});
            this.logger.info(MODULE_NAME + " " + runningMessage);

            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_SERVER_RUNNING,
                    runningMessage,
                    startupLog.toString());
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_SERVER_RUNNING,
                    rb.getResourceString("dbserver.startup"),
                    startupLog.toString() + "\n"
                    + "[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
            this.logger.severe(MODULE_NAME + " " + "DBServer.startup: " + e.getMessage());
            throw e;
        } finally {
            if (configConnection != null) {
                try {
                    configConnection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e);
                }
            }
            if (runtimeConnection != null) {
                try {
                    runtimeConnection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e);
                }
            }
        }

        this.dbDriverManager.setupConnectionPool();

        // Wait until the server is accessible
        while (true) {
            try (Connection testConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
                break;
            } catch (Throwable e) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    // nop
                }
            }
        }
    }

    /**
     * Check if databases exist and create tables if they don't exist
     */
    private void createCheck() throws Exception {
        if (!this.databaseExists(IDBDriverManager.DB_CONFIG)) {
            // New installation - create tables
            this.dbDriverManager.createDatabase(IDBDriverManager.DB_CONFIG);
        }
        if (!this.databaseExists(IDBDriverManager.DB_RUNTIME)) {
            // New installation - create tables
            this.dbDriverManager.createDatabase(IDBDriverManager.DB_RUNTIME);
        }
    }

    /**
     * Returns if the passed database type exists
     */
    private boolean databaseExists(int databaseType) {
        String TABLE_NAME = "TABLE_NAME";
        String[] TABLE_TYPES = {"TABLE"};
        boolean databaseFound = false;
        try (Connection connection = this.dbDriverManager.getConnectionWithoutErrorHandling(databaseType)) {
            if (connection != null) {
                DatabaseMetaData metadata = connection.getMetaData();
                try (ResultSet tableResult = metadata.getTables(null, "public", null, TABLE_TYPES)) {
                    while (tableResult.next()) {
                        if (tableResult.getString(TABLE_NAME).equalsIgnoreCase("version")) {
                            databaseFound = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return (false);
        }
        return (databaseFound);
    }

    /**
     * Returns the actual db version of the passed type
     */
    private int getActualDBVersion(final int DB_TYPE) {
        int foundVersion = -1;
        Connection connection = null;
        try {
            connection = this.dbDriverManager.getConnectionWithoutErrorHandling(DB_TYPE);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery(
                        "SELECT MAX(actualversion) AS maxversion FROM version")) {
                    if (result.next()) {
                        foundVersion = result.getInt("maxversion");
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e);
                }
            }
        }
        return (foundVersion);
    }

    /**
     * Update the database if this is necessary.
     */
    private void updateDB(final int DB_TYPE) {
        int requiredDBVersion = -1;
        String dbName = null;
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            dbName = rb.getResourceString("database." + IDBDriverManager.DB_CONFIG);
            requiredDBVersion = AS2ServerVersion.getRequiredDBVersionConfig();
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            dbName = rb.getResourceString("database." + IDBDriverManager.DB_RUNTIME);
            requiredDBVersion = AS2ServerVersion.getRequiredDBVersionRuntime();
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBServer:updateDB.");
        }

        int foundVersion = this.getActualDBVersion(DB_TYPE);

        // Check for future version (fatal error)
        if (foundVersion != -1 && foundVersion > requiredDBVersion) {
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.error.futureversion",
                    new Object[]{
                        rb.getResourceString("database." + DB_TYPE),
                        String.valueOf(requiredDBVersion),
                        String.valueOf(foundVersion)}));
            SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_UPDATE);
            event.setSubject(rb.getResourceString("database." + DB_TYPE));
            event.setBody(rb.getResourceString("update.error.futureversion",
                    new Object[]{
                        rb.getResourceString("database." + DB_TYPE),
                        String.valueOf(requiredDBVersion),
                        String.valueOf(foundVersion)}));
            SystemEventManagerImplAS2.instance().newEvent(event);
            System.exit(-1);
        }

        // Check if update is needed
        if (foundVersion != -1 && foundVersion < requiredDBVersion) {
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.versioninfo",
                    new Object[]{
                        String.valueOf(foundVersion),
                        String.valueOf(requiredDBVersion)
                    }));
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress"));

            for (int i = foundVersion; i < requiredDBVersion; i++) {
                this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress.version.start",
                        new Object[]{String.valueOf(i + 1), dbName}));
                if (!this.startDBUpdate(i, DB_TYPE)) {
                    this.logger.severe(MODULE_NAME + " " + rb.getResourceString("update.error.postgresql",
                            new Object[]{String.valueOf(i), String.valueOf(i + 1)}));
                    SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_DATABASE_UPDATE);
                    event.setSubject(rb.getResourceString("database." + DB_TYPE));
                    event.setBody(rb.getResourceString("update.error.postgresql",
                            new Object[]{String.valueOf(i), String.valueOf(i + 1)}));
                    SystemEventManagerImplAS2.instance().newEvent(event);
                    System.exit(-1);
                }

                // Set new version to the database
                this.setNewDBVersion(DB_TYPE, i + 1);
                int newActualVersion = this.getActualDBVersion(DB_TYPE);
                this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress.version.end",
                        new Object[]{String.valueOf(newActualVersion), dbName}));

                SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_DATABASE_UPDATE);
                event.setSubject(rb.getResourceString("update.successfully", dbName));
                event.setBody(rb.getResourceString("update.progress.version.end",
                        new Object[]{String.valueOf(newActualVersion), dbName}));
                SystemEventManagerImplAS2.instance().newEvent(event);
            }
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.successfully", dbName));
        }
    }

    /**
     * Sets the new DB version to the passed number if the update was successful
     */
    private void setNewDBVersion(final int DB_TYPE, int version) {
        try (Connection connection = this.dbDriverManager.getConnectionWithoutErrorHandling(DB_TYPE)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO version(actualversion,updatedate,updatecomment)VALUES(?,?,?)")) {
                statement.setInt(1, version);
                statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()),
                        Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                statement.setString(3, AS2ServerVersion.getFullProductName() + ": update");
                statement.executeUpdate();
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e);
        }
    }

    /**
     * Sends a shutdown signal to the database connection pool.
     * PostgreSQL server itself continues running as an external service.
     */
    @Override
    public void shutdown() {
        try {
            // Close connection pools
            this.dbDriverManager.shutdownConnectionPool();

            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_SERVER_SHUTDOWN,
                    rb.getResourceString("dbserver.shutdown"),
                    "PostgreSQL connection pools closed. External PostgreSQL server continues running.");

            String shutdownMessage = rb.getResourceString("dbserver.shutdown");
            System.out.println(shutdownMessage);
            this.logger.info(MODULE_NAME + " Connection pools closed.");
        } catch (Exception e) {
            System.out.println("DB connection pool shutdown: " + e.getMessage());
            this.logger.warning(MODULE_NAME + " Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * Start the DB update from the startVersion to the startVersion+1 - transactional
     */
    private boolean startDBUpdate(int startVersion, final int DB_TYPE) {
        String updateResource = null;
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            updateResource = SQLScriptExecutor.SCRIPT_RESOURCE_CONFIG;
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            updateResource = SQLScriptExecutor.SCRIPT_RESOURCE_RUNTIME;
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBServer.");
        }

        // SQL file to execute for the update process
        String sqlResource = updateResource + "update" + startVersion + "to" + (startVersion + 1) + ".sql";
        SQLScriptExecutor executor = new SQLScriptExecutor();
        executor.setQueryModifier((ISQLQueryModifier) this.dbDriverManager);
        String transactionName = "DB_UPDATE_" + startVersion + "_to_" + (startVersion + 1) + "_DB" + DB_TYPE;

        try {
            try (Connection updateConnectionNoAutoCommit = this.dbDriverManager
                    .getConnectionWithoutErrorHandling(DB_TYPE)) {
                updateConnectionNoAutoCommit.setAutoCommit(false);
                try (Statement transactionStatement = updateConnectionNoAutoCommit.createStatement()) {
                    this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                    try {
                        if (executor.resourceExists(sqlResource)) {
                            executor.executeScript(updateConnectionNoAutoCommit, sqlResource);
                        } else {
                            // Check if a java file should be executed that changes something in
                            // the database. This will happen only if the .sql file has not been found
                            String javaUpdateClass = updateResource.replace('/', '.') + "Update"
                                    + startVersion + "to" + (startVersion + 1);
                            if (javaUpdateClass.startsWith(".")) {
                                javaUpdateClass = javaUpdateClass.substring(1);
                            }
                            try {
                                Class<?> cl = Class.forName(javaUpdateClass);
                                IUpdater updater = (IUpdater) cl.getDeclaredConstructor().newInstance();
                                updater.startUpdate(updateConnectionNoAutoCommit);
                                if (!updater.updateWasSuccessfully()) {
                                    throw new Exception("Update failed.");
                                }
                            } catch (ClassNotFoundException e) {
                                this.logger.info(MODULE_NAME + " " + "DBServer.startDBUpdate (ClassNotFoundException):" + e);
                                this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.notfound",
                                        new Object[]{String.valueOf(startVersion),
                                            String.valueOf(startVersion + 1),
                                            updateResource
                                        }));
                                throw new Exception("Update failed.");
                            }
                        }
                        this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                        return (true);
                    } catch (Throwable e) {
                        SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                        this.dbDriverManager.rollbackTransaction(transactionStatement);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e);
        }
        return (false);
    }
}

