/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.util.MecResourceBundle;

/**
 * Resource bundle for IP Whitelist Management UI (English)
 */
public class ResourceBundleIPWhitelist extends MecResourceBundle {

    // Dialog titles
    public static final String TITLE = "title";
    public static final String TITLE_EDIT = "title.edit";
    public static final String TITLE_ADD = "title.add";

    // Tab names
    public static final String TAB_SETTINGS = "tab.settings";
    public static final String TAB_GLOBAL = "tab.global";
    public static final String TAB_PARTNER = "tab.partner";
    public static final String TAB_USER = "tab.user";
    public static final String TAB_BLOCKLOG = "tab.blocklog";

    // Buttons
    public static final String BUTTON_ADD = "button.add";
    public static final String BUTTON_EDIT = "button.edit";
    public static final String BUTTON_DELETE = "button.delete";
    public static final String BUTTON_REFRESH = "button.refresh";
    public static final String BUTTON_SAVE = "button.save";
    public static final String BUTTON_CANCEL = "button.cancel";
    public static final String BUTTON_CLOSE = "button.close";
    public static final String BUTTON_EXPORT = "button.export";

    // Labels
    public static final String LABEL_IP_PATTERN = "label.ip_pattern";
    public static final String LABEL_DESCRIPTION = "label.description";
    public static final String LABEL_TARGET_TYPE = "label.target_type";
    public static final String LABEL_ENABLED = "label.enabled";
    public static final String LABEL_SELECT_PARTNER = "label.select_partner";
    public static final String LABEL_SELECT_USER = "label.select_user";

    // Settings tab
    public static final String SETTINGS_ENABLE_AS2 = "settings.enable_as2";
    public static final String SETTINGS_ENABLE_TRACKER = "settings.enable_tracker";
    public static final String SETTINGS_ENABLE_WEBUI = "settings.enable_webui";
    public static final String SETTINGS_ENABLE_API = "settings.enable_api";
    public static final String SETTINGS_MODE = "settings.mode";
    public static final String SETTINGS_MODE_GLOBAL_ONLY = "settings.mode.global_only";
    public static final String SETTINGS_MODE_PARTNER_ONLY = "settings.mode.partner_only";
    public static final String SETTINGS_MODE_GLOBAL_AND_SPECIFIC = "settings.mode.global_and_specific";
    public static final String SETTINGS_LOG_RETENTION = "settings.log_retention";

    // Messages
    public static final String MSG_DELETE_CONFIRM = "msg.delete_confirm";
    public static final String MSG_SAVE_SUCCESS = "msg.save_success";
    public static final String MSG_SAVE_ERROR = "msg.save_error";
    public static final String MSG_DELETE_SUCCESS = "msg.delete_success";
    public static final String MSG_DELETE_ERROR = "msg.delete_error";
    public static final String MSG_VALIDATION_IP_REQUIRED = "msg.validation.ip_required";
    public static final String MSG_VALIDATION_IP_INVALID = "msg.validation.ip_invalid";

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS = {
        {TITLE, "IP Whitelist Management"},
        {TITLE_EDIT, "Edit IP Whitelist Entry"},
        {TITLE_ADD, "Add IP Whitelist Entry"},

        {TAB_SETTINGS, "Settings"},
        {TAB_GLOBAL, "Global Whitelist"},
        {TAB_PARTNER, "Partner-Specific"},
        {TAB_USER, "User-Specific"},
        {TAB_BLOCKLOG, "Block Log"},

        {BUTTON_ADD, "Add"},
        {BUTTON_EDIT, "Edit"},
        {BUTTON_DELETE, "Delete"},
        {BUTTON_REFRESH, "Refresh"},
        {BUTTON_SAVE, "Save"},
        {BUTTON_CANCEL, "Cancel"},
        {BUTTON_CLOSE, "Close"},
        {BUTTON_EXPORT, "Export"},

        {LABEL_IP_PATTERN, "IP Pattern:"},
        {LABEL_DESCRIPTION, "Description:"},
        {LABEL_TARGET_TYPE, "Target Type:"},
        {LABEL_ENABLED, "Enabled"},
        {LABEL_SELECT_PARTNER, "Select Partner:"},
        {LABEL_SELECT_USER, "Select User:"},

        {SETTINGS_ENABLE_AS2, "Enable for AS2 Endpoint"},
        {SETTINGS_ENABLE_TRACKER, "Enable for Tracker Endpoint"},
        {SETTINGS_ENABLE_WEBUI, "Enable for WebUI Access"},
        {SETTINGS_ENABLE_API, "Enable for API Access"},
        {SETTINGS_MODE, "Whitelist Mode:"},
        {SETTINGS_MODE_GLOBAL_ONLY, "Global Only"},
        {SETTINGS_MODE_PARTNER_ONLY, "Partner/User Specific Only"},
        {SETTINGS_MODE_GLOBAL_AND_SPECIFIC, "Global + Specific (Recommended)"},
        {SETTINGS_LOG_RETENTION, "Block Log Retention (days):"},

        {MSG_DELETE_CONFIRM, "Are you sure you want to delete this entry?"},
        {MSG_SAVE_SUCCESS, "Settings saved successfully"},
        {MSG_SAVE_ERROR, "Failed to save: {0}"},
        {MSG_DELETE_SUCCESS, "Entry deleted successfully"},
        {MSG_DELETE_ERROR, "Failed to delete: {0}"},
        {MSG_VALIDATION_IP_REQUIRED, "IP Pattern is required"},
        {MSG_VALIDATION_IP_INVALID, "Invalid IP pattern format"},
    };
}
