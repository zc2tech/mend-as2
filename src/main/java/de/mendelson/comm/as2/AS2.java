package de.mendelson.comm.as2;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.client.JDialogLogin;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.ServerAlreadyRunningException;
import de.mendelson.util.DisplayMode;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.Splash;
import de.mendelson.util.WindowTitleUtil;
import de.mendelson.util.font.FontUtil;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.awt.Color;
import java.awt.image.RescaleOp;
import java.util.Locale;
import javax.swing.JOptionPane;

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
 * Start the AS2 server and the configuration GUI
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class AS2 {

    /**
     * Displays a usage of how to use this class
     */
    public static void printUsage() {
        System.out.println("java " + AS2.class.getName() + " <options>");
        System.out.println("Start up a " + AS2ServerVersion.getProductNameShortcut() + " server ");
        System.out.println("Options are:");
        System.out.println("-lang <String>: Language to use for the client/server, nonpersistent. Possible values are " + PreferencesAS2.getSupportedLanguagesAsUsageList() + ".");
        System.out.println("-country <String>: Country/region to use for the client/server, nonpersistent. Possible values are \"DE\", \"US\", \"FR\", \"GB\"...");
        System.out.println("-nohttpserver: Do not start the integrated HTTP server, only useful if you are integrating the product into an other web container");
        System.out.println("-nogui: Run in headless mode without GUI (for cloud/container deployments)");
        System.out.println("-mode <String>: Sets up the LIGHT or DARK mode for the client - default is LIGHT");
        System.out.println("-importTLS: Imports a new TLS keystore to the system and overwrites the existing. For further requirements please have a look at the documentation.");
        System.out.println("-importSignEnc: Imports a new sign/encryption keystore to the system and overwrites the existing. For further requirements please have a look at the documentation.");
    }

    /**
     * Method to start the server on from the command line
     */
    public static void main(String args[]) {
        // Load configuration from properties file
        AS2Config config = new AS2Config();
        boolean startGUI = config.isGuiEnabled();

        String language = null;
        String country = null;
        boolean startHTTP = true;
        boolean importTLS = false;
        boolean importEncSign = false;
        PreferencesAS2 clientPreferences = new PreferencesAS2();
        int optind;
        for (optind = 0; optind < args.length; optind++) {
            if (args[optind].toLowerCase().equals("-lang")) {
                language = args[++optind];
            } else if (args[optind].toLowerCase().equals("-country")) {
                country = args[++optind];
            } else if (args[optind].toLowerCase().equals("-nohttpserver")) {
                startHTTP = false;
            } else if (args[optind].toLowerCase().equals("-nogui")) {
                startGUI = false;
            } else if (args[optind].toLowerCase().equals("-mode")) {
                String modeParameter = args[++optind];
                if (modeParameter != null) {
                    if (modeParameter.equalsIgnoreCase(DisplayMode.DARK)) {
                        clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, DisplayMode.DARK);
                    } else if (modeParameter.equalsIgnoreCase(DisplayMode.HICONTRAST)) {
                        clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, DisplayMode.HICONTRAST);
                    } else {
                        clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, DisplayMode.LIGHT);
                    }
                }
            } else if (args[optind].toLowerCase().equals("-importtls")) {
                importTLS = true;
            } else if (args[optind].toLowerCase().equals("-importencsign")) {
                importEncSign = true;
            } else if (args[optind].toLowerCase().equals("-importsignenc")) {
                importEncSign = true;
            } else if (args[optind].toLowerCase().equals("-?")) {
                AS2.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-h")) {
                AS2.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-help")) {
                AS2.printUsage();
                System.exit(1);
            }
        }
        //load country from preferences
        if (country == null || language == null) {
            if (language == null) {
                language = clientPreferences.get(PreferencesAS2.LANGUAGE);
            }
            if (country == null) {
                country = clientPreferences.get(PreferencesAS2.COUNTRY);
            }
        }
        if (language != null && country != null) {
            // Fallback for unsupported locale combinations in JCalendar
            // JCalendar doesn't support en_CN, so use en_US as fallback
            if (language.toLowerCase().equals("en") && country.equals("CN")) {
                country = "US";
            }
            if (language.toLowerCase().equals("en")) {
                Locale.setDefault(new Locale.Builder().setLanguage(Locale.ENGLISH.getLanguage()).setRegion(country).build());
            } else if (language.toLowerCase().equals("de")) {
                Locale.setDefault(new Locale.Builder().setLanguage(Locale.GERMAN.getLanguage()).setRegion(country).build());
            } else if (language.toLowerCase().equals("fr")) {
                Locale.setDefault(new Locale.Builder().setLanguage(Locale.FRENCH.getLanguage()).setRegion(country).build());
            } else if (language.toLowerCase().equals("it")) {
                Locale.setDefault(new Locale.Builder().setLanguage(Locale.ITALIAN.getLanguage()).setRegion(country).build());
            } else if (language.toLowerCase().equals("es")) {
                Locale spain = new Locale.Builder().setLanguage("es").setRegion("ES").build();
                Locale.setDefault(new Locale.Builder().setLanguage(spain.getLanguage()).setRegion(country).build());
            } else if (language.toLowerCase().equals("pt")) {
                Locale portugal = new Locale.Builder().setLanguage("pt").setRegion("PT").build();
                Locale.setDefault(new Locale.Builder().setLanguage(portugal.getLanguage()).setRegion(country).build());
            } else {
                System.out.println("Language " + language + " is not supported, switching to en");
                Locale.setDefault(new Locale.Builder().setLanguage(Locale.ENGLISH.getLanguage()).setRegion(country).build());
            }
        }

        // Set headless mode if GUI is disabled
        if (!startGUI) {
            System.setProperty("java.awt.headless", "true");
        } else {
            // Check if Mina is available (headless builds exclude it)
            try {
                Class.forName("org.apache.mina.core.service.IoAcceptor");
            } catch (ClassNotFoundException e) {
                System.err.println("ERROR: SwingUI requested but Mina library not found.");
                System.err.println("This appears to be a headless build (built with -Pheadless profile).");
                System.err.println("SwingUI is not available in headless builds.");
                System.err.println("");
                System.err.println("Solutions:");
                System.err.println("  1. Use headless mode: java -jar as2.jar -nogui");
                System.err.println("  2. Access WebUI at: http://localhost:8080/as2/webui/");
                System.err.println("  3. Rebuild with full profile: mvn clean package -Pfull");
                System.err.println("");
                System.err.println("Forcing headless mode...");
                startGUI = false;
                System.setProperty("java.awt.headless", "true");
            }
        }

        String displayMode = clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT);
        if (displayMode.equalsIgnoreCase(DisplayMode.DARK)) {
            //darken all SVG generated images/icons by 10% (also the splash)
            MendelsonMultiResolutionImage.addSVGImageOperation(new RescaleOp(0.9f, 0, null));
        }
        //add colorblind overlays if required to the client icons
        if (clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS)) {
            MendelsonMultiResolutionImage.addSVGOverlay("state_finished.svg", "/de/mendelson/util/colorblind/overlay_state_finished.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("cert_valid.svg", "/de/mendelson/util/colorblind/overlay_state_finished.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_stopped.svg", "/de/mendelson/util/colorblind/overlay_state_stopped.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("cert_invalid.svg", "/de/mendelson/util/colorblind/overlay_state_stopped.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_pending.svg", "/de/mendelson/util/colorblind/overlay_state_pending.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_allselected.svg", "/de/mendelson/util/colorblind/overlay_state_allselected.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("severity_info.svg", "/de/mendelson/util/colorblind/overlay_severity_info.svg");
        }

        // Create splash screen only in GUI mode
        Splash splash = null;
        if (startGUI) {
            // Use test mode splash if in test mode
            String splashImage = WindowTitleUtil.isTestMode()
                ? "/de/mendelson/comm/as2/client/splash_mend_as2_test.svg"
                : "/de/mendelson/comm/as2/client/splash_mend_as2.svg";
            splash = new Splash(splashImage, 330);
            splash.setTextAntiAliasing(false);
            //dark grey
            Color textColor = FontUtil.getFontColor(FontUtil.PRODUCT_OFTP2_COMMUNITY);
            splash.addDisplayString(FontUtil.getProductFont(FontUtil.STYLE_PRODUCT_BOLD, 10),
                    12, 285, AS2ServerVersion.getFullProductName(),
                    textColor);
            splash.setVisible(true);
            splash.toFront();
        }

        try {
            //initialize the security provider
            BCCryptoHelper helper = new BCCryptoHelper();
            helper.initialize();
            // Only start Mina server if GUI mode is enabled (for SwingUI)
            // In headless mode, disable Mina for security - WebUI uses REST API
            new AS2Server(startHTTP, false, false, importTLS, importEncSign,
                         config.shouldSkipConfigCheck(), startGUI, config);
        } catch (ServerAlreadyRunningException e) {
            //don't delete the lockfile in this case!
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    "[" + e.getClass().getSimpleName() + "]",
                    e.getMessage());
            if (splash != null) {
                splash.destroy();
            }
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getName() + "]";
            }
            if (startGUI) {
                JOptionPane.showMessageDialog(null, message);
            } else {
                System.err.println("ERROR: " + message);
            }
            System.exit(1);
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    "[" + e.getClass().getSimpleName() + "]",
                    e.getMessage());
            if (splash != null) {
                splash.destroy();
            }
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getName() + "]";
            }
            if (startGUI) {
                JOptionPane.showMessageDialog(null, message);
            } else {
                System.err.println("ERROR: " + message);
            }
            AS2Server.deleteLockFile();
            System.exit(1);
        }

        // Start GUI client or run in headless mode
        if (startGUI) {
            // Close splash screen before showing login dialog
            if (splash != null) {
                splash.destroy();
                splash.dispose();
            }

            // Show login dialog
            JDialogLogin loginDialog = new JDialogLogin(null, displayMode);
            loginDialog.setVisible(true);

            if (!loginDialog.isLoginSuccessful()) {
                // User cancelled login - exit application
                System.out.println("Login cancelled by user");
                System.exit(0);
            }

            // Get credentials from login dialog
            String username = loginDialog.getUsername();
            char[] password = loginDialog.getPassword();

            // Start client with provided credentials (no splash - already closed)
            AS2Gui gui = new AS2Gui(null, "localhost", username, new String(password), displayMode);

            // Clear sensitive data
            loginDialog.clearPassword();
            if (password != null) {
                for (int i = 0; i < password.length; i++) {
                    password[i] = 0;
                }
            }

            gui.setVisible(true);
        } else {
            // Headless mode - server keeps running without GUI or authentication
            System.out.println("Running in headless mode - GUI disabled");
            System.out.println("Server is now running. Press Ctrl+C to stop.");
            // Keep the main thread alive - server runs in background threads
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                System.out.println("Server shutdown requested");
            }
        }
    }
}
