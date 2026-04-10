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

package de.mendelson.comm.as2;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Configuration loader for AS2 server startup settings
 * Reads from config/as2.properties with environment variable overrides
 *
 */
public class AS2Config {

    private static final String CONFIG_FILE = "config/as2.properties";
    private static final String PROP_GUI_ENABLED = "as2.startup.gui.enabled";
    private static final String PROP_DISPLAY_MODE = "as2.display.mode";
    private static final String PROP_SKIP_CONFIG_CHECK = "as2.startup.skip.configcheck";
    private static final String PROP_TEST_MODE = "as2.test.mode";
    private static final String PROP_LOG_LEVEL = "as2.log.level";

    private static final String ENV_START_GUI = "AS2_START_GUI";
    private static final String ENV_DISPLAY_MODE = "AS2_DISPLAY_MODE";
    private static final String ENV_SKIP_CONFIG_CHECK = "AS2_SKIP_CONFIG_CHECK";
    private static final String ENV_TEST_MODE = "AS2_TEST_MODE";
    private static final String ENV_LOG_LEVEL = "AS2_LOG_LEVEL";

    private final Properties properties;

    public AS2Config() {
        this.properties = new Properties();
        this.loadConfiguration();
    }

    /**
     * Load configuration with priority: env vars > properties file > defaults
     */
    private void loadConfiguration() {
        // Load from properties file if it exists
        Path configFile = Paths.get(CONFIG_FILE);
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                properties.load(in);
            } catch (Exception e) {
                System.err.println("Warning: Could not load " + CONFIG_FILE + ": " + e.getMessage());
            }
        }

        // Override with environment variables
        String envStartGUI = System.getenv(ENV_START_GUI);
        if (envStartGUI != null) {
            properties.setProperty(PROP_GUI_ENABLED, envStartGUI);
        }

        String envDisplayMode = System.getenv(ENV_DISPLAY_MODE);
        if (envDisplayMode != null) {
            properties.setProperty(PROP_DISPLAY_MODE, envDisplayMode);
        }

        String envSkipConfigCheck = System.getenv(ENV_SKIP_CONFIG_CHECK);
        if (envSkipConfigCheck != null) {
            properties.setProperty(PROP_SKIP_CONFIG_CHECK, envSkipConfigCheck);
        }

        String envTestMode = System.getenv(ENV_TEST_MODE);
        if (envTestMode != null) {
            properties.setProperty(PROP_TEST_MODE, envTestMode);
        }

        String envLogLevel = System.getenv(ENV_LOG_LEVEL);
        if (envLogLevel != null) {
            properties.setProperty(PROP_LOG_LEVEL, envLogLevel);
        }

        // Set system property so WindowTitleUtil can access test mode setting
        System.setProperty(PROP_TEST_MODE, properties.getProperty(PROP_TEST_MODE, "false"));
    }

    /**
     * Returns whether GUI should be started
     * @return true if GUI should start, false for headless mode
     */
    public boolean isGuiEnabled() {
        return Boolean.parseBoolean(properties.getProperty(PROP_GUI_ENABLED, "true"));
    }

    /**
     * Returns the display mode for GUI
     * @return LIGHT, DARK, or HICONTRAST
     */
    public String getDisplayMode() {
        return properties.getProperty(PROP_DISPLAY_MODE, "LIGHT");
    }

    /**
     * Returns whether to skip configuration check at startup
     * @return true to skip config check, false to run it
     */
    public boolean shouldSkipConfigCheck() {
        return Boolean.parseBoolean(properties.getProperty(PROP_SKIP_CONFIG_CHECK, "false"));
    }

    /**
     * Returns whether to run in test mode (uses alternative ports)
     * @return true for test mode, false for normal operation
     */
    public boolean isTestMode() {
        return Boolean.parseBoolean(properties.getProperty(PROP_TEST_MODE, "false"));
    }

    /**
     * Returns the Client-Server communication port based on test mode
     * @return 41234 in test mode, 1234 in normal mode
     */
    public int getClientServerPort() {
        return isTestMode() ? 41234 : 1234;
    }

    /**
     * Returns the HTTP port based on test mode
     * @return 11080 in test mode, 8080 in normal mode
     */
    public int getHttpPort() {
        return isTestMode() ? 11080 : 8080;
    }

    /**
     * Returns the HTTPS port based on test mode
     * @return 11443 in test mode, 8443 in normal mode
     */
    public int getHttpsPort() {
        return isTestMode() ? 11443 : 8443;
    }

    /**
     * Returns the log level for the server logger
     * Valid values: ALL, FINEST, FINER, FINE, INFO, WARNING, SEVERE, OFF
     * @return log level name, defaults to INFO
     */
    public String getLogLevel() {
        return properties.getProperty(PROP_LOG_LEVEL, "INFO").toUpperCase();
    }
}
