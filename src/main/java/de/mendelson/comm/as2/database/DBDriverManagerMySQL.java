package de.mendelson.comm.as2.database;

import de.mendelson.util.database.SQLScriptExecutor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.AbstractDBDriverManagerMySQL;
import de.mendelson.util.database.DebuggableConnection;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.database.ISQLQueryModifier;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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
/**
 * Class needed to access the MySQL/MariaDB database
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class DBDriverManagerMySQL extends AbstractDBDriverManagerMySQL implements ISQLQueryModifier {

    public static final boolean DEBUG = false;
    private final static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final static MecResourceBundle rb;
    private final static String MODULE_NAME;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDBDriverManager.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        MODULE_NAME = rb.getResourceString("module.name");
    }

    // MySQL configuration from file/environment variables
    private final MySQLConfig config = MySQLConfig.getInstance();
    public static final boolean USE_CONNECTION_POOLING = true;

    private final HikariConfig configConnectionPoolConfig = new HikariConfig();
    private final HikariConfig configConnectionPoolRuntime = new HikariConfig();
    private static HikariDataSource configDatasource = null;
    private static HikariDataSource runtimeDatasource = null;

    static {
        // Register MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Throwable e) {
            // Try MariaDB driver as fallback
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (Throwable e2) {
                throw new RuntimeException("Unable to register database driver for MySQL/MariaDB database - ["
                        + e.getClass().getSimpleName() + "] " + e.getMessage());
            }
        }
    }

    /**
     * keeps this as singleton for the whole server instance
     */
    private static DBDriverManagerMySQL instance;

    /**
     * Singleton for the whole application. Looks uncommon but uses the double
     * checked method for higher performance - in this case the method is not
     * needed to be synchronized
     */
    public static DBDriverManagerMySQL instance() {
        if (instance == null) {
            synchronized (DBDriverManagerMySQL.class) {
                if (instance == null) {
                    instance = new DBDriverManagerMySQL();
                }
            }
        }
        return (instance);
    }

    private DBDriverManagerMySQL() {
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        return ("1.0");
    }

    /**
     * Setup the driver manager, initialize the connection pool
     */
    @Override
    public synchronized void setupConnectionPool() {
        if (USE_CONNECTION_POOLING && configDatasource == null) {
            // Print configuration for verification
            config.printConfiguration();

            // Setup CONFIG database pool
            this.configConnectionPoolConfig.setJdbcUrl(config.getJdbcUrl(true));
            this.configConnectionPoolConfig.setUsername(config.getUser());
            this.configConnectionPoolConfig.setPassword(config.getPassword());
            this.configConnectionPoolConfig.setPoolName(this.getDBName(DB_CONFIG));
            this.configConnectionPoolConfig.setMaximumPoolSize(config.getMaximumPoolSize());
            this.configConnectionPoolConfig.setMinimumIdle(config.getMinimumIdle());
            this.configConnectionPoolConfig.setConnectionTimeout(config.getConnectionTimeout());
            this.configConnectionPoolConfig.setIdleTimeout(config.getIdleTimeout());
            this.configConnectionPoolConfig.setMaxLifetime(config.getMaxLifetime());
            configDatasource = new HikariDataSource(this.configConnectionPoolConfig);

            // Setup RUNTIME database pool
            this.configConnectionPoolRuntime.setJdbcUrl(config.getJdbcUrl(false));
            this.configConnectionPoolRuntime.setUsername(config.getUser());
            this.configConnectionPoolRuntime.setPassword(config.getPassword());
            this.configConnectionPoolRuntime.setPoolName(this.getDBName(DB_RUNTIME));
            this.configConnectionPoolRuntime.setMaximumPoolSize(config.getMaximumPoolSize());
            this.configConnectionPoolRuntime.setMinimumIdle(config.getMinimumIdle());
            this.configConnectionPoolRuntime.setConnectionTimeout(config.getConnectionTimeout());
            this.configConnectionPoolRuntime.setIdleTimeout(config.getIdleTimeout());
            this.configConnectionPoolRuntime.setMaxLifetime(config.getMaxLifetime());
            runtimeDatasource = new HikariDataSource(this.configConnectionPoolRuntime);
        }
    }

    /**
     * shutdown the connection pool
     */
    @Override
    public void shutdownConnectionPool() throws SQLException {
        if (USE_CONNECTION_POOLING) {
            if (configDatasource != null) {
                configDatasource.close();
            }
            if (runtimeDatasource != null) {
                runtimeDatasource.close();
            }
        }
    }

    /**
     * Returns the URI to connect to MySQL
     */
    private String getConnectionURI(final int DB_TYPE) {
        return config.getJdbcUrl(DB_TYPE == DB_CONFIG);
    }

    /**
     * Returns the DB name, depending on the system wide profile name
     */
    public String getDBName(final int DB_TYPE) {
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            return config.getConfigDatabase();
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            return config.getRuntimeDatabase();
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBDriverManager.");
        }
        return "as2_db";
    }

    /**
     * Creates a new MySQL database
     *
     * @return true if it was created successfully
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    @Override
    public boolean createDatabase(final int DB_TYPE) throws Exception {
        try {
            String createResource = null;
            int dbVersion = 0;
            if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
                dbVersion = AS2ServerVersion.getRequiredDBVersionConfig();
                createResource = "/sqlscript/mysql/config/";
            } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
                dbVersion = AS2ServerVersion.getRequiredDBVersionRuntime();
                createResource = "/sqlscript/mysql/runtime/";
            } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
                throw new RuntimeException("Unknown DB type requested in DBDriverManager.");
            }

            // Check if database already exists by checking for the VERSION table
            boolean databaseExists = false;
            try (Connection connection = DriverManager.getConnection(
                    this.getConnectionURI(DB_TYPE), config.getUser(), config.getPassword())) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeQuery("SELECT COUNT(*) FROM version");
                    databaseExists = true;
                } catch (SQLException e) {
                    // VERSION table doesn't exist, database needs to be created
                    databaseExists = false;
                }
            } catch (SQLException e) {
                // Connection failed or database doesn't exist
                throw new Exception("MySQL database " + this.getDBName(DB_TYPE)
                        + " does not exist. Please create it manually:\n"
                        + "CREATE DATABASE " + this.getDBName(DB_TYPE) + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n"
                        + "GRANT ALL PRIVILEGES ON " + this.getDBName(DB_TYPE) + ".* TO '" + config.getUser() + "'@'%';");
            }

            if (databaseExists) {
                // Database exists, but check if keystores are initialized (only for config DB)
                if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
                    try (Connection connection = DriverManager.getConnection(
                            this.getConnectionURI(DB_TYPE), config.getUser(), config.getPassword())) {

                        // Check if keydata table exists
                        boolean keydataTableExists = false;
                        try (ResultSet rs = connection.getMetaData().getTables(null, this.getDBName(DB_TYPE), "keydata", null)) {
                            keydataTableExists = rs.next();
                        }

                        if (!keydataTableExists) {
                            // keydata table is missing - recreate database structure
                            SQLScriptExecutor executor = new SQLScriptExecutor();
                            executor.create(connection, createResource, dbVersion, new AS2ServerVersion());
                            this.initializeEmptyKeystores(connection);
                            return true;
                        }

                        // Check keystore entries
                        try (PreparedStatement pstmt = connection.prepareStatement(
                                "SELECT COUNT(*) as count FROM keydata WHERE purpose IN (1,2)")) {
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    int keystoreCount = rs.getInt("count");
                                    if (keystoreCount < 2) {
                                        // Missing one or both keystores - initialize them
                                        this.initializeEmptyKeystores(connection);
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }

            // Create tables in the existing database
            try (Connection connection = DriverManager.getConnection(
                    this.getConnectionURI(DB_TYPE), config.getUser(), config.getPassword())) {
                SQLScriptExecutor executor = new SQLScriptExecutor();
                executor.create(connection, createResource, dbVersion, new AS2ServerVersion());

                // Initialize empty keystores after table creation (only for config DB)
                if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
                    this.initializeEmptyKeystores(connection);
                }
            } catch (Exception e) {
                throw new Exception(rb.getResourceString("database.creation.failed." + DB_TYPE)
                        + " [" + e.getMessage() + "]");
            }
            logger.info(MODULE_NAME + " " + rb.getResourceString("database.creation.success." + DB_TYPE));
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_CREATION);
            event.setSubject(rb.getResourceString("database.creation.success." + DB_TYPE));
            SystemEventManagerImplAS2.instance().newEvent(event);
        } catch (Throwable e) {
            Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).severe(rb.getResourceString("database.creation.failed." + DB_TYPE)
                    + " [" + e.getMessage() + "]");
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_CREATION);
            event.setSubject(
                    rb.getResourceString("database.creation.failed." + DB_TYPE));
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getSimpleName() + "]";
            }
            event.setBody(message);
            SystemEventManagerImplAS2.instance().newEvent(event);
            throw e;
        }
        return (true);
    }

    /**
     * Initialize empty PKCS12 keystores in the keydata table
     * Called during initial database creation
     */
    private void initializeEmptyKeystores(Connection connection) throws Exception {
        try {
            // Create empty PKCS12 keystores
            de.mendelson.util.security.BCCryptoHelper cryptoHelper =
                new de.mendelson.util.security.BCCryptoHelper();

            String keystoreType = de.mendelson.util.security.BCCryptoHelper.KEYSTORE_PKCS12;
            String securityProvider = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;  // "BC"

            // Create empty ENC/SIGN keystore (purpose=2)
            java.security.KeyStore encSignKeystore = cryptoHelper.createKeyStoreInstance(keystoreType);
            char[] keystorePass = "test".toCharArray();
            encSignKeystore.load(null, keystorePass);

            // Create empty TLS keystore (purpose=1)
            java.security.KeyStore tlsKeystore = cryptoHelper.createKeyStoreInstance(keystoreType);
            tlsKeystore.load(null, keystorePass);

            // Save keystores to byte arrays
            java.io.ByteArrayOutputStream encSignOut = new java.io.ByteArrayOutputStream();
            encSignKeystore.store(encSignOut, keystorePass);
            byte[] encSignData = encSignOut.toByteArray();

            java.io.ByteArrayOutputStream tlsOut = new java.io.ByteArrayOutputStream();
            tlsKeystore.store(tlsOut, keystorePass);
            byte[] tlsData = tlsOut.toByteArray();

            // Insert into keydata table
            // user_id: 0 = system/admin
            // purpose: 1=TLS, 2=ENC/SIGN
            // storagetype: 2=PKCS12
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO keydata (user_id, purpose, storagedata, storagetype, lastchanged, securityprovider) " +
                    "VALUES (?, ?, ?, ?, ?, ?)")) {

                // Insert ENC/SIGN keystore (purpose=2) for admin user (user_id=0)
                stmt.setInt(1, 0);  // user_id = 0 (admin/system)
                stmt.setInt(2, 2);  // purpose = ENC/SIGN
                stmt.setBytes(3, encSignData);
                stmt.setInt(4, 2);  // storagetype = PKCS12
                stmt.setLong(5, System.currentTimeMillis());
                stmt.setString(6, securityProvider);
                stmt.executeUpdate();

                // Insert TLS keystore (purpose=1) for admin user (user_id=0)
                stmt.setInt(1, 0);  // user_id = 0 (admin/system)
                stmt.setInt(2, 1);  // purpose = TLS
                stmt.setBytes(3, tlsData);
                stmt.setInt(4, 2);  // storagetype = PKCS12
                stmt.setLong(5, System.currentTimeMillis());
                stmt.setString(6, securityProvider);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            throw new Exception("Failed to initialize empty keystores: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a connection to the database
     *
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    @Override
    public synchronized Connection getConnectionWithoutErrorHandling(final int DB_TYPE)
            throws SQLException {
        Connection connection = null;
        if (DB_TYPE == DB_RUNTIME) {
            if (runtimeDatasource != null && runtimeDatasource.getHikariPoolMXBean().getIdleConnections() > 0) {
                connection = runtimeDatasource.getConnection();
            } else {
                connection = DriverManager.getConnection(
                        getConnectionURI(DB_TYPE),
                        config.getUser(), config.getPassword());
            }
        } else if (DB_TYPE == DB_CONFIG) {
            if (configDatasource != null && configDatasource.getHikariPoolMXBean().getIdleConnections() > 0) {
                connection = configDatasource.getConnection();
            } else {
                connection = DriverManager.getConnection(
                        getConnectionURI(DB_TYPE),
                        config.getUser(), config.getPassword());
            }
        } else if (DB_TYPE == DB_DEPRICATED) {
            // deprecated connection: no pooling
            connection = DriverManager.getConnection(
                    getConnectionURI(DB_TYPE),
                    config.getUser(), config.getPassword());
        } else {
            throw new RuntimeException("Requested invalid db type in getConnectionWithoutErrorHandling");
        }
        connection.setReadOnly(false);
        connection.setAutoCommit(true);
        return (new DebuggableConnection(connection));
    }

    @Override
    public String modifyQuery(String query) {
        String newQuery = query;
        // MySQL doesn't need significant query modification for most cases
        // HSQLDB uses "INTEGER GENERATED BY DEFAULT AS IDENTITY"
        // MySQL uses "INT AUTO_INCREMENT" which should already be in the CREATE.sql
        return (newQuery);
    }

    /**
     * Returns some connection pool information for debug purpose
     */
    @Override
    public String getPoolInformation(int DB_TYPE) {
        StringBuilder output = new StringBuilder();
        HikariDataSource datasource;
        if (DB_TYPE == DB_CONFIG) {
            datasource = configDatasource;
            output.append("[CONFIG DB]");
        } else {
            datasource = runtimeDatasource;
            output.append("[RUNTIME DB]");
        }
        if (!USE_CONNECTION_POOLING || datasource == null) {
            output.append("No connection pooling");
        } else {
            int activeConnections = datasource.getHikariPoolMXBean().getActiveConnections();
            int totalConnections = datasource.getHikariPoolMXBean().getTotalConnections();
            int idleConnections = datasource.getHikariPoolMXBean().getIdleConnections();
            output.append(" Total [").append(String.valueOf(totalConnections)).append("]");
            output.append(" Active [").append(String.valueOf(activeConnections)).append("]");
            output.append(" Idle [").append(String.valueOf(idleConnections)).append("]");
        }
        return (output.toString());
    }

    @Override
    public String addLimitToQuery(String query, int maxRows) {
        return (query + " LIMIT " + maxRows);
    }

    @Override
    public void setBytesParameterAsJavaObject(PreparedStatement statement, int index, byte[] data) throws Exception {
        if (data == null) {
            statement.setNull(index, java.sql.Types.BLOB);
        } else {
            statement.setBytes(index, data);
        }
    }
}
