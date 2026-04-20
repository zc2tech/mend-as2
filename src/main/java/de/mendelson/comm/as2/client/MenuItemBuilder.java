/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.client;

import de.mendelson.util.KeyboardShortcutUtil;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Helper class for creating menu items with consistent styling and shortcuts.
 * Reduces boilerplate code in menu construction.
 *
 * @author Julian Xu
 */
public class MenuItemBuilder {

    private final JMenuItem menuItem;

    private MenuItemBuilder(JMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    /**
     * Create a new menu item builder
     */
    public static MenuItemBuilder create() {
        return new MenuItemBuilder(new JMenuItem());
    }

    /**
     * Create a menu item builder with an existing menu item
     */
    public static MenuItemBuilder with(JMenuItem menuItem) {
        return new MenuItemBuilder(menuItem);
    }

    /**
     * Set the menu item text
     */
    public MenuItemBuilder text(String text) {
        menuItem.setText(text);
        return this;
    }

    /**
     * Set the menu item icon
     */
    public MenuItemBuilder icon(Icon icon) {
        menuItem.setIcon(icon);
        return this;
    }

    /**
     * Set keyboard accelerator (Cmd/Ctrl + key)
     */
    public MenuItemBuilder accelerator(int keyCode) {
        menuItem.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(keyCode));
        return this;
    }

    /**
     * Set keyboard accelerator with modifiers
     */
    public MenuItemBuilder accelerator(int keyCode, int modifiers) {
        menuItem.setAccelerator(KeyboardShortcutUtil.createMenuShortcut(keyCode, modifiers));
        return this;
    }

    /**
     * Add action listener
     */
    public MenuItemBuilder action(ActionListener listener) {
        menuItem.addActionListener(listener);
        return this;
    }

    /**
     * Add action listener using lambda-compatible functional interface
     */
    public MenuItemBuilder onAction(Runnable action) {
        menuItem.addActionListener(e -> action.run());
        return this;
    }

    /**
     * Set enabled state
     */
    public MenuItemBuilder enabled(boolean enabled) {
        menuItem.setEnabled(enabled);
        return this;
    }

    /**
     * Set visible state
     */
    public MenuItemBuilder visible(boolean visible) {
        menuItem.setVisible(visible);
        return this;
    }

    /**
     * Set tooltip text
     */
    public MenuItemBuilder tooltip(String tooltip) {
        menuItem.setToolTipText(tooltip);
        return this;
    }

    /**
     * Build and return the configured menu item
     */
    public JMenuItem build() {
        return menuItem;
    }

    /**
     * Convenience method: create a menu item with text, icon, and action
     */
    public static JMenuItem createMenuItem(String text, Icon icon, ActionListener action) {
        return create()
                .text(text)
                .icon(icon)
                .action(action)
                .build();
    }

    /**
     * Convenience method: create a menu item with text, icon, accelerator, and action
     */
    public static JMenuItem createMenuItem(String text, Icon icon, int keyCode, ActionListener action) {
        return create()
                .text(text)
                .icon(icon)
                .accelerator(keyCode)
                .action(action)
                .build();
    }

    /**
     * Convenience method: create a menu item with text, icon, accelerator with modifiers, and action
     */
    public static JMenuItem createMenuItem(String text, Icon icon, int keyCode, int modifiers, ActionListener action) {
        return create()
                .text(text)
                .icon(icon)
                .accelerator(keyCode, modifiers)
                .action(action)
                .build();
    }
}
