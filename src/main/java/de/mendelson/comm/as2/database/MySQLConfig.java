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

package de.mendelson.comm.as2.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Configuration loader for MySQL/MariaDB database settings.
 * Loads settings from:
 * 1. Environment variables (highest priority)
 * 2. config/database-mysql.properties file
 * 3. System properties
 * 4. Default values (lowest priority)
 *
 */
public class MySQLConfig {

    private static final Logger logger = Logger.getLogger("de.mendelson.as2.server");
    private static final String CONFIG_FILE = "config/database-mysql.properties";
    private static MySQLConfig instance;
    private Properties properties;

    private MySQLConfig() {
        loadConfiguration();
    }

    public static synchronized MySQLConfig getInstance() {
        if (instance == null) {
            instance = new MySQLConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        properties = new Properties();

        // Load from properties file first
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            logger.info("Loaded MySQL/MariaDB configuration from " + CONFIG_FILE);
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
        return getConfigValue("MYSQL_HOST", "mysql.host", "localhost");
    }

    public String getPort() {
        return getConfigValue("MYSQL_PORT", "mysql.port", "3306");
    }

    public String getUser() {
        return getConfigValue("MYSQL_USER", "mysql.user", "as2user");
    }

    public String getPassword() {
        return getConfigValue("MYSQL_PASSWORD", "mysql.password", "as2password");
    }

    public String getConfigDatabase() {
        return getConfigValue("MYSQL_DB_CONFIG", "mysql.db.config", "as2_db_config");
    }

    public String getRuntimeDatabase() {
        return getConfigValue("MYSQL_DB_RUNTIME", "mysql.db.runtime", "as2_db_runtime");
    }

    public int getMaximumPoolSize() {
        String value = getConfigValue("MYSQL_POOL_MAX_SIZE", "mysql.pool.maximumPoolSize", "10");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid maximumPoolSize value: " + value + ", using default 10");
            return 10;
        }
    }

    public int getMinimumIdle() {
        String value = getConfigValue("MYSQL_POOL_MIN_IDLE", "mysql.pool.minimumIdle", "2");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid minimumIdle value: " + value + ", using default 2");
            return 2;
        }
    }

    public long getConnectionTimeout() {
        String value = getConfigValue("MYSQL_POOL_CONN_TIMEOUT", "mysql.pool.connectionTimeout", "30000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid connectionTimeout value: " + value + ", using default 30000");
            return 30000;
        }
    }

    public long getIdleTimeout() {
        String value = getConfigValue("MYSQL_POOL_IDLE_TIMEOUT", "mysql.pool.idleTimeout", "600000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid idleTimeout value: " + value + ", using default 600000");
            return 600000;
        }
    }

    public long getMaxLifetime() {
        String value = getConfigValue("MYSQL_POOL_MAX_LIFETIME", "mysql.pool.maxLifetime", "1800000");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid maxLifetime value: " + value + ", using default 1800000");
            return 1800000;
        }
    }

    /**
     * Get JDBC URL for MySQL/MariaDB
     * @param isConfigDB true for config database, false for runtime database
     * @return JDBC connection string
     */
    public String getJdbcUrl(boolean isConfigDB) {
        String database = isConfigDB ? getConfigDatabase() : getRuntimeDatabase();
        // MySQL JDBC URL format: jdbc:mysql://host:port/database?parameters
        // useSSL=false for local development, set to true in production
        // serverTimezone=UTC to avoid timezone issues
        // allowPublicKeyRetrieval=true for newer MySQL versions with caching_sha2_password
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8",
                getHost(), getPort(), database);
    }

    /**
     * Print configuration for debugging
     */
    public void printConfiguration() {
        logger.info("========================================");
        logger.info("MySQL/MariaDB Configuration:");
        logger.info("  Host: " + getHost());
        logger.info("  Port: " + getPort());
        logger.info("  User: " + getUser());
        logger.info("  Config DB: " + getConfigDatabase());
        logger.info("  Runtime DB: " + getRuntimeDatabase());
        logger.info("  Pool Max Size: " + getMaximumPoolSize());
        logger.info("  Pool Min Idle: " + getMinimumIdle());
        logger.info("========================================");
    }
}
