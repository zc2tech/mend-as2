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

package de.mendelson.util;

import de.mendelson.comm.as2.AS2ServerVersion;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Utility class for managing window titles across the application
 * Ensures consistent branding and test mode indication
 *
 */
public class WindowTitleUtil {

    private static final String PRODUCT_NAME = "mend-as2";
    private static final String TEST_MODE_SUFFIX = " [TEST MODE]";
    private static final boolean IS_TEST_MODE;

    static {
        IS_TEST_MODE = Boolean.parseBoolean(System.getProperty("as2.test.mode", "false"));
    }

    /**
     * Returns true if the application is running in test mode
     */
    public static boolean isTestMode() {
        return IS_TEST_MODE;
    }

    /**
     * Builds a complete window title with product name and test mode indicator
     *
     * @param baseTitle The base title for the window (e.g., "Partner Configuration")
     * @return Complete title with product name and optional test mode indicator
     */
    public static String buildTitle(String baseTitle) {
        StringBuilder title = new StringBuilder();

        if (baseTitle != null && !baseTitle.trim().isEmpty()) {
            title.append(baseTitle);
            title.append(" - ");
        }

        title.append(PRODUCT_NAME);

        if (IS_TEST_MODE) {
            title.append(TEST_MODE_SUFFIX);
        }

        return title.toString();
    }

    /**
     * Sets the title for a JFrame with product name and test mode indicator
     *
     * @param frame The frame to set the title for
     * @param baseTitle The base title for the window
     */
    public static void setTitle(JFrame frame, String baseTitle) {
        if (frame != null) {
            frame.setTitle(buildTitle(baseTitle));
        }
    }

    /**
     * Sets the title for a JDialog with product name and test mode indicator
     *
     * @param dialog The dialog to set the title for
     * @param baseTitle The base title for the dialog
     */
    public static void setTitle(JDialog dialog, String baseTitle) {
        if (dialog != null) {
            dialog.setTitle(buildTitle(baseTitle));
        }
    }

    /**
     * Returns the color to use for test mode indicators
     * @return Red color for test mode warnings
     */
    public static Color getTestModeColor() {
        return new Color(220, 20, 60); // Crimson red
    }

    /**
     * Returns the product name
     */
    public static String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * Returns the test mode suffix text
     */
    public static String getTestModeSuffix() {
        return TEST_MODE_SUFFIX;
    }

    /**
     * Convenience method to update title for existing window if needed
     * Useful for windows created before test mode was detected
     */
    public static void updateTitle(JFrame frame) {
        if (frame != null && frame.getTitle() != null) {
            String currentTitle = frame.getTitle();
            // Remove product name and test mode suffix if present
            String baseTitle = currentTitle
                    .replace(" - " + PRODUCT_NAME, "")
                    .replace(PRODUCT_NAME, "")
                    .replace(TEST_MODE_SUFFIX, "")
                    .trim();
            setTitle(frame, baseTitle);
        }
    }

    /**
     * Convenience method to update title for existing dialog if needed
     */
    public static void updateTitle(JDialog dialog) {
        if (dialog != null && dialog.getTitle() != null) {
            String currentTitle = dialog.getTitle();
            // Remove product name and test mode suffix if present
            String baseTitle = currentTitle
                    .replace(" - " + PRODUCT_NAME, "")
                    .replace(PRODUCT_NAME, "")
                    .replace(TEST_MODE_SUFFIX, "")
                    .trim();
            setTitle(dialog, baseTitle);
        }
    }
}
