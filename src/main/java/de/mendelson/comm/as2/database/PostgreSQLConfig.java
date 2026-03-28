package de.mendelson.comm.as2.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Configuration loader for PostgreSQL database settings.
 * Loads settings from:
 * 1. Environment variables (highest priority)
 * 2. config/database-postgresql.properties file
 * 3. System properties
 * 4. Default values (lowest priority)
 *
 * @author S.Heller
 */
public class PostgreSQLConfig {

    private static final Logger logger = Logger.getLogger("de.mendelson.as2.server");
    private static final String CONFIG_FILE = "config/database-postgresql.properties";
    private static PostgreSQLConfig instance;
    private Properties properties;

    private PostgreSQLConfig() {
        loadConfiguration();
    }

    public static synchronized PostgreSQLConfig getInstance() {
        if (instance == null) {
            instance = new PostgreSQLConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        properties = new Properties();

        // Load from properties file first
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            logger.info("Loaded PostgreSQL configuration from " + CONFIG_FILE);
        } catch (IOException e) {
            logger.warning("Could not load " + CONFIG_FILE + ", using defaults and environment variables");
        }
    }

    /**
     * Get configuration value with priority:
     * 1. Environment variable
     * 2. System property
     * 3. Properties file
     * 4. Default value
     */
    private String getConfigValue(String envVar, String propertyKey, String defaultValue) {
        // 1. Check environment variable
        String envValue = System.getenv(envVar);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // 2. Check system property
        String sysProp = System.getProperty(propertyKey);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        // 3. Check properties file
        String propValue = properties.getProperty(propertyKey);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }

        // 4. Return default
        return defaultValue;
    }

    public String getHost() {
        return getConfigValue("POSTGRES_HOST", "postgresql.host", "localhost");
    }

    public String getPort() {
        return getConfigValue("POSTGRES_PORT", "postgresql.port", "5432");
    }

    public String getUser() {
        return getConfigValue("POSTGRES_USER", "postgresql.user", "as2user");
    }

    public String getPassword() {
        return getConfigValue("POSTGRES_PASSWORD", "postgresql.password", "as2password");
    }

    public String getConfigDatabase() {
        return getConfigValue("POSTGRES_DB_CONFIG", "postgresql.db.config", "as2_db_config");
    }

    public String getRuntimeDatabase() {
        return getConfigValue("POSTGRES_DB_RUNTIME", "postgresql.db.runtime", "as2_db_runtime");
    }

    public int getMaximumPoolSize() {
        String value = getConfigValue("POSTGRES_POOL_MAX_SIZE", "postgresql.pool.maximumPoolSize", "10");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid maximumPoolSize value: " + value + ", using default 10");
            return 10;
        }
    }

    public int getMinimumIdle() {
        String value = getConfigValue("POSTGRES_POOL_MIN_IDLE", "postgresql.pool.minimumIdle", "2");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid minimumIdle value: " + value + ", using default 2");
            return 2;
        }
    }

    public long getConnectionTimeout() {
        String value = getConfigValue("POSTGRES_POOL_CONN_TIMEOUT", "postgresql.pool.connectionTimeout", "30000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid connectionTimeout value: " + value + ", using default 30000");
            return 30000;
        }
    }

    public long getIdleTimeout() {
        String value = getConfigValue("POSTGRES_POOL_IDLE_TIMEOUT", "postgresql.pool.idleTimeout", "600000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid idleTimeout value: " + value + ", using default 600000");
            return 600000;
        }
    }

    public long getMaxLifetime() {
        String value = getConfigValue("POSTGRES_POOL_MAX_LIFETIME", "postgresql.pool.maxLifetime", "1800000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid maxLifetime value: " + value + ", using default 1800000");
            return 1800000;
        }
    }

    public boolean isSslEnabled() {
        String value = getConfigValue("POSTGRES_SSL_ENABLED", "postgresql.ssl.enabled", "false");
        return Boolean.parseBoolean(value);
    }

    public String getSslMode() {
        return getConfigValue("POSTGRES_SSL_MODE", "postgresql.ssl.mode", "require");
    }

    /**
     * Get JDBC URL for the specified database
     */
    public String getJdbcUrl(boolean isConfigDb) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:postgresql://")
           .append(getHost())
           .append(":")
           .append(getPort())
           .append("/")
           .append(isConfigDb ? getConfigDatabase() : getRuntimeDatabase());

        if (isSslEnabled()) {
            url.append("?ssl=true&sslmode=").append(getSslMode());
        }

        return url.toString();
    }

    /**
     * Print current configuration (for debugging)
     */
    public void printConfiguration() {
        logger.info("PostgreSQL Configuration:");
        logger.info("  Host: " + getHost());
        logger.info("  Port: " + getPort());
        logger.info("  User: " + getUser());
        logger.info("  Config DB: " + getConfigDatabase());
        logger.info("  Runtime DB: " + getRuntimeDatabase());
        logger.info("  Max Pool Size: " + getMaximumPoolSize());
        logger.info("  Min Idle: " + getMinimumIdle());
        logger.info("  SSL Enabled: " + isSslEnabled());
    }
}
