/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.util.MecResourceBundle;

/**
 * Resource bundle for IP Whitelist Management UI (German)
 */
public class ResourceBundleIPWhitelist_de extends MecResourceBundle {

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS = {
        {ResourceBundleIPWhitelist.TITLE, "IP-Whitelist-Verwaltung"},
        {ResourceBundleIPWhitelist.TITLE_EDIT, "IP-Whitelist-Eintrag bearbeiten"},
        {ResourceBundleIPWhitelist.TITLE_ADD, "IP-Whitelist-Eintrag hinzufügen"},

        {ResourceBundleIPWhitelist.TAB_SETTINGS, "Einstellungen"},
        {ResourceBundleIPWhitelist.TAB_GLOBAL, "Globale Whitelist"},
        {ResourceBundleIPWhitelist.TAB_PARTNER, "Partnerspezifisch"},
        {ResourceBundleIPWhitelist.TAB_USER, "Benutzerspezifisch"},
        {ResourceBundleIPWhitelist.TAB_BLOCKLOG, "Blockierungsprotokoll"},

        {ResourceBundleIPWhitelist.BUTTON_ADD, "Hinzufügen"},
        {ResourceBundleIPWhitelist.BUTTON_EDIT, "Bearbeiten"},
        {ResourceBundleIPWhitelist.BUTTON_DELETE, "Löschen"},
        {ResourceBundleIPWhitelist.BUTTON_REFRESH, "Aktualisieren"},
        {ResourceBundleIPWhitelist.BUTTON_SAVE, "Speichern"},
        {ResourceBundleIPWhitelist.BUTTON_CANCEL, "Abbrechen"},
        {ResourceBundleIPWhitelist.BUTTON_CLOSE, "Schließen"},
        {ResourceBundleIPWhitelist.BUTTON_EXPORT, "Exportieren"},

        {ResourceBundleIPWhitelist.LABEL_IP_PATTERN, "IP-Muster:"},
        {ResourceBundleIPWhitelist.LABEL_DESCRIPTION, "Beschreibung:"},
        {ResourceBundleIPWhitelist.LABEL_TARGET_TYPE, "Zieltyp:"},
        {ResourceBundleIPWhitelist.LABEL_ENABLED, "Aktiviert"},
        {ResourceBundleIPWhitelist.LABEL_SELECT_PARTNER, "Partner auswählen:"},
        {ResourceBundleIPWhitelist.LABEL_SELECT_USER, "Benutzer auswählen:"},

        {ResourceBundleIPWhitelist.SETTINGS_ENABLE_AS2, "Für AS2-Endpunkt aktivieren"},
        {ResourceBundleIPWhitelist.SETTINGS_ENABLE_TRACKER, "Für Tracker-Endpunkt aktivieren"},
        {ResourceBundleIPWhitelist.SETTINGS_ENABLE_WEBUI, "Für WebUI-Zugriff aktivieren"},
        {ResourceBundleIPWhitelist.SETTINGS_ENABLE_API, "Für API-Zugriff aktivieren"},
        {ResourceBundleIPWhitelist.SETTINGS_MODE, "Whitelist-Modus:"},
        {ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_ONLY, "Nur global"},
        {ResourceBundleIPWhitelist.SETTINGS_MODE_PARTNER_ONLY, "Nur partner-/benutzerspezifisch"},
        {ResourceBundleIPWhitelist.SETTINGS_MODE_GLOBAL_AND_SPECIFIC, "Global + Spezifisch (Empfohlen)"},
        {ResourceBundleIPWhitelist.SETTINGS_LOG_RETENTION, "Aufbewahrung Blockierungsprotokoll (Tage):"},

        {ResourceBundleIPWhitelist.MSG_DELETE_CONFIRM, "Möchten Sie diesen Eintrag wirklich löschen?"},
        {ResourceBundleIPWhitelist.MSG_SAVE_SUCCESS, "Einstellungen erfolgreich gespeichert"},
        {ResourceBundleIPWhitelist.MSG_SAVE_ERROR, "Speichern fehlgeschlagen: {0}"},
        {ResourceBundleIPWhitelist.MSG_DELETE_SUCCESS, "Eintrag erfolgreich gelöscht"},
        {ResourceBundleIPWhitelist.MSG_DELETE_ERROR, "Löschen fehlgeschlagen: {0}"},
        {ResourceBundleIPWhitelist.MSG_VALIDATION_IP_REQUIRED, "IP-Muster ist erforderlich"},
        {ResourceBundleIPWhitelist.MSG_VALIDATION_IP_INVALID, "Ungültiges IP-Musterformat"},
    };
}
