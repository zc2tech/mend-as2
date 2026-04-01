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
 * @author S.Heller
 */
public class AS2Config {

    private static final String CONFIG_FILE = "config/as2.properties";
    private static final String PROP_GUI_ENABLED = "as2.startup.gui.enabled";
    private static final String PROP_DISPLAY_MODE = "as2.display.mode";
    private static final String PROP_SKIP_CONFIG_CHECK = "as2.startup.skip.configcheck";

    private static final String ENV_START_GUI = "AS2_START_GUI";
    private static final String ENV_DISPLAY_MODE = "AS2_DISPLAY_MODE";
    private static final String ENV_SKIP_CONFIG_CHECK = "AS2_SKIP_CONFIG_CHECK";

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
}
