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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Utility class for setting up keyboard shortcuts in Swing applications
 * with proper Mac support (using Command key instead of Ctrl)
 *
 */
public class KeyboardShortcutUtil {

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    /**
     * Gets the menu shortcut key mask for the current platform.
     * Returns Command (META) on Mac, Ctrl on other platforms.
     */
    public static int getMenuShortcutKeyMask() {
        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    }

    /**
     * Gets the menu shortcut key name for display (e.g., "Cmd" on Mac, "Ctrl" on others)
     */
    public static String getMenuShortcutKeyName() {
        return IS_MAC ? "Cmd" : "Ctrl";
    }

    /**
     * Adds an ESC key binding to close a dialog.
     *
     * @param dialog The dialog to close on ESC
     */
    public static void addEscapeKeyBinding(JDialog dialog) {
        JRootPane rootPane = dialog.getRootPane();

        // ESC to close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionName = "ESCAPE_ACTION";

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, actionName);
        rootPane.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    /**
     * Adds an ENTER key binding to trigger a button action.
     *
     * @param dialog The dialog containing the button
     * @param button The button to trigger on ENTER
     */
    public static void addEnterKeyBinding(JDialog dialog, JButton button) {
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setDefaultButton(button);
    }

    /**
     * Adds a keyboard shortcut to a button using platform-specific modifier key.
     *
     * @param button The button to add the shortcut to
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @param description Description for the action (e.g., "SAVE")
     */
    public static void addButtonShortcut(AbstractButton button, int keyCode, String description) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, getMenuShortcutKeyMask());

        InputMap inputMap = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = button.getActionMap();

        inputMap.put(keyStroke, description);
        actionMap.put(description, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isEnabled()) {
                    button.doClick();
                }
            }
        });
    }

    /**
     * Adds a keyboard shortcut to a button with custom modifiers.
     *
     * @param button The button to add the shortcut to
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @param modifiers The modifiers (e.g., InputEvent.SHIFT_DOWN_MASK)
     * @param description Description for the action (e.g., "SAVE_AS")
     */
    public static void addButtonShortcut(JButton button, int keyCode, int modifiers, String description) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);

        InputMap inputMap = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = button.getActionMap();

        inputMap.put(keyStroke, description);
        actionMap.put(description, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isEnabled()) {
                    button.doClick();
                }
            }
        });
    }

    /**
     * Sets up common dialog keyboard shortcuts:
     * - ESC to close
     * - ENTER to trigger the default button (if provided)
     * - Platform-specific shortcuts for OK button (if provided)
     *
     * @param dialog The dialog to set up
     * @param okButton Optional OK/Save button (can be null)
     * @param cancelButton Optional Cancel button (can be null)
     */
    public static void setupDialogKeyBindings(JDialog dialog, JButton okButton, JButton cancelButton) {
        // ESC to close
        addEscapeKeyBinding(dialog);

        // Set default button for ENTER if OK button is provided
        if (okButton != null) {
            addEnterKeyBinding(dialog, okButton);
        }

        // Cmd/Ctrl+W to close (standard Mac behavior)
        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, getMenuShortcutKeyMask());
        JRootPane rootPane = dialog.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "CLOSE_WINDOW");
        rootPane.getActionMap().put("CLOSE_WINDOW", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    /**
     * Updates button text to show the keyboard shortcut hint.
     * Example: "Save" becomes "Save (Cmd+S)" on Mac or "Save (Ctrl+S)" on others
     *
     * @param button The button to update
     * @param baseText The base button text (e.g., "Save")
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     */
    public static void setButtonTextWithShortcut(JButton button, String baseText, int keyCode) {
        String keyName = KeyEvent.getKeyText(keyCode);
        String shortcutText = String.format("%s (%s+%s)", baseText, getMenuShortcutKeyName(), keyName);
        button.setText(shortcutText);
    }

    /**
     * Creates a KeyStroke using the platform's menu shortcut key.
     *
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @return KeyStroke with platform-specific modifier
     */
    public static KeyStroke createMenuShortcut(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, getMenuShortcutKeyMask());
    }

    /**
     * Creates a KeyStroke using the platform's menu shortcut key with additional modifiers.
     *
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @param additionalModifiers Additional modifiers (e.g., InputEvent.SHIFT_DOWN_MASK)
     * @return KeyStroke with combined modifiers
     */
    public static KeyStroke createMenuShortcut(int keyCode, int additionalModifiers) {
        return KeyStroke.getKeyStroke(keyCode, getMenuShortcutKeyMask() | additionalModifiers);
    }

    /**
     * Adds a tooltip to a button showing its keyboard shortcut.
     * If the button already has a tooltip, the shortcut is appended.
     *
     * @param button The button to add tooltip to
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     */
    public static void addShortcutTooltip(JButton button, int keyCode) {
        String keyName = KeyEvent.getKeyText(keyCode);
        String shortcutText = getMenuShortcutKeyName() + "+" + keyName;
        addTooltipWithShortcut(button, shortcutText);
    }

    /**
     * Adds a tooltip to a button showing a custom keyboard shortcut.
     *
     * @param button The button to add tooltip to
     * @param shortcutText The shortcut text (e.g., "Cmd+S", "ESC")
     */
    public static void addTooltipWithShortcut(JButton button, String shortcutText) {
        String existingTooltip = button.getToolTipText();
        String newTooltip;

        if (existingTooltip != null && !existingTooltip.isEmpty()) {
            // Append shortcut to existing tooltip
            newTooltip = existingTooltip + " [" + shortcutText + "]";
        } else {
            // Create new tooltip with shortcut
            newTooltip = "Keyboard: " + shortcutText;
        }

        button.setToolTipText(newTooltip);
    }

    /**
     * Adds a tooltip to any component showing a keyboard shortcut.
     *
     * @param component The component to add tooltip to
     * @param shortcutText The shortcut text (e.g., "Cmd+S", "ESC")
     */
    public static void addTooltipWithShortcut(JComponent component, String shortcutText) {
        String existingTooltip = component.getToolTipText();
        String newTooltip;

        if (existingTooltip != null && !existingTooltip.isEmpty()) {
            // Append shortcut to existing tooltip
            newTooltip = existingTooltip + " [" + shortcutText + "]";
        } else {
            // Create new tooltip with shortcut
            newTooltip = "Keyboard: " + shortcutText;
        }

        component.setToolTipText(newTooltip);
    }

    /**
     * Sets up common dialog keyboard shortcuts and adds tooltips to buttons.
     * - ESC to close
     * - ENTER to trigger the default button (if provided)
     * - Cmd/Ctrl+W to close
     * - Tooltips showing shortcuts on buttons
     *
     * @param dialog The dialog to set up
     * @param okButton Optional OK/Save button (can be null)
     * @param cancelButton Optional Cancel button (can be null)
     */
    public static void setupDialogKeyBindingsWithTooltips(JDialog dialog, JButton okButton, JButton cancelButton) {
        // Setup the keyboard bindings
        setupDialogKeyBindings(dialog, okButton, cancelButton);

        // Add tooltips to buttons
        if (okButton != null) {
            addTooltipWithShortcut(okButton, "ENTER");
        }

        if (cancelButton != null) {
            addTooltipWithShortcut(cancelButton, "ESC");
        }

        // Add dialog-level tooltip info
        String dialogShortcuts = "ESC or " + getMenuShortcutKeyName() + "+W to close";
        if (dialog.getRootPane() != null) {
            dialog.getRootPane().setToolTipText(dialogShortcuts);
        }
    }

    /**
     * Adds a keyboard shortcut to a button and automatically adds a tooltip showing the shortcut.
     *
     * @param button The button to add the shortcut to
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @param description Description for the action (e.g., "SAVE")
     */
    public static void addButtonShortcutWithTooltip(JButton button, int keyCode, String description) {
        // Add the keyboard shortcut
        addButtonShortcut(button, keyCode, description);

        // Add tooltip showing the shortcut
        addShortcutTooltip(button, keyCode);
    }

    /**
     * Gets a formatted string for displaying a keyboard shortcut.
     * Example: "Cmd+S" on Mac, "Ctrl+S" on Windows
     *
     * @param keyCode The key code (e.g., KeyEvent.VK_S)
     * @return Formatted shortcut string
     */
    public static String getShortcutDisplayText(int keyCode) {
        String keyName = KeyEvent.getKeyText(keyCode);
        return getMenuShortcutKeyName() + "+" + keyName;
    }

    /**
     * Gets common dialog shortcuts as a formatted help text.
     *
     * @return Help text describing common dialog shortcuts
     */
    public static String getDialogShortcutsHelpText() {
        return String.format(
            "<html><b>Keyboard Shortcuts:</b><br>" +
            "• ENTER - Confirm/OK<br>" +
            "• ESC - Cancel/Close<br>" +
            "• %s+W - Close dialog</html>",
            getMenuShortcutKeyName()
        );
    }

    /**
     * Creates a help label that can be added to dialogs to show available shortcuts.
     *
     * @return JLabel with shortcuts help text
     */
    public static JLabel createShortcutsHelpLabel() {
        JLabel helpLabel = new JLabel(getDialogShortcutsHelpText());
        helpLabel.setFont(helpLabel.getFont().deriveFont(10f));
        Color foreground = UIManager.getColor("Label.disabledForeground");
        if (foreground != null) {
            helpLabel.setForeground(foreground);
        } else {
            helpLabel.setForeground(Color.GRAY);
        }
        return helpLabel;
    }
}
