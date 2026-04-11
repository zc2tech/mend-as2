package de.mendelson.comm.as2;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
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
 * Loads AS2 server configuration from as2.properties file.
 * Priority: Environment Variables > System Properties > File > Defaults
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class AS2Properties {

    private static AS2Properties instance;
    private static final String CONFIG_FILE = "config/as2.properties";
    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");
    private Properties properties;

    private AS2Properties() {
        this.properties = new Properties();
        loadProperties();
    }

    public static synchronized AS2Properties getInstance() {
        if (instance == null) {
            instance = new AS2Properties();
        }
        return instance;
    }

    private void loadProperties() {
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                properties.load(in);
                logger.info("Loaded AS2 configuration from " + CONFIG_FILE);
            } catch (Exception e) {
                logger.warning("Could not load " + CONFIG_FILE + ": " + e.getMessage());
            }
        }
    }

    /**
     * Get database type (postgresql or mysql)
     * Priority: ENV > System Property > File > Default
     */
    public String getDatabaseType() {
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

        // 3. Properties file
        String propValue = properties.getProperty("as2.database.type");
        if (propValue != null && !propValue.isEmpty()) {
            return normalizeDbType(propValue);
        }

        // 4. Default
        return "postgresql";
    }

    /**
     * Normalize database type to lowercase and validate
     */
    private String normalizeDbType(String dbType) {
        String normalized = dbType.trim().toLowerCase();
        if ("postgresql".equals(normalized) || "postgres".equals(normalized)) {
            return "postgresql";
        }
        if ("mysql".equals(normalized) || "mariadb".equals(normalized)) {
            return "mysql";
        }
        logger.warning("Invalid database type '" + dbType + "', using default: postgresql");
        return "postgresql";
    }

    /**
     * Get boolean property with priority: ENV > System Property > File > Default
     */
    public boolean getBoolean(String propertyKey, String envVar, boolean defaultValue) {
        String envValue = System.getenv(envVar);
        if (envValue != null && !envValue.isEmpty()) {
            return Boolean.parseBoolean(envValue);
        }

        String sysProp = System.getProperty(propertyKey);
        if (sysProp != null && !sysProp.isEmpty()) {
            return Boolean.parseBoolean(sysProp);
        }

        String propValue = properties.getProperty(propertyKey);
        if (propValue != null && !propValue.isEmpty()) {
            return Boolean.parseBoolean(propValue);
        }

        return defaultValue;
    }

    /**
     * Get string property with priority: ENV > System Property > File > Default
     */
    public String getString(String propertyKey, String envVar, String defaultValue) {
        String envValue = System.getenv(envVar);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        String sysProp = System.getProperty(propertyKey);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        String propValue = properties.getProperty(propertyKey);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }

        return defaultValue;
    }
}
