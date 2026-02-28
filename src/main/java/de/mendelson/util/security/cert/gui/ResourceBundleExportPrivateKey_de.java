//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportPrivateKey_de.java 3     9/12/24 15:51 Heller $ 
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleExportPrivateKey_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"button.browse", "Durchsuchen"},
        {"keystore.contains.nokeys", "Dieser Keystore beihaltet keine privaten Schlüssel."},
        {"label.exportdir", "Export Verzeichnis"},
        {"label.exportdir.hint", "Verzeichnis, in dem der Schlüsselspeicher/die Schlüsseldatei erstellt werden soll"},
        {"label.exportdir.help", "<HTML><strong>Export Verzeichnis</strong><br><br>"
            + "Bitte geben Sie hier das Exportverzeichnis an, in das der private Schlüssel exportiert werden soll.<br>"
            + "Aus Sicherheitsgründen wird der Schlüssel nicht auf den Client transferiert, daher ist nur ein Speichern auf Serverseite möglich.<br><br>"
            + "Das System wird in diesem Verzeichnis eine Speicherdatei erzeugen, die einen Datumsstempel beinhaltet."
            + "</HTML>"},
        {"label.exportkey", "Dateiname"},
        {"label.exportkey.hint", "Zu erstellende Export Datei"},
        {"label.keypass", "Passwort"},
        {"label.keypass.hint", "Passwort für exportierten Keystore/Schlüsseldatei"},
        {"title", "Schlüssel in Keystore/Schlüsseldatei exportieren"},
        {"filechooser.key.export", "Bitte wählen Sie das Exportverzeichnis auf dem Server"},
        {"key.export.success.title", "Erfolg"},
        {"key.export.error.message", "Es gab einen Fehler beim Export.\n{0}"},
        {"key.export.error.title", "Fehler"},
        {"label.alias", "Schlüssel"},
        {"label.exportformat", "Exportformat"},
        {"label.exportformat.help", "<HTML><strong>Export Format</strong><br><br>"
            + "Sie können den Schlüssel entweder in einem Keystoreformat (PKCS#12) "
            + "oder einer PEM codierten Schlüsseldatei exportieren.<br>"
            + "Die gebräuchliste Form ist das Format PKCS#12, eine PEM codierte "
            + "Schlüsseldatei wird nur für spezielle Anwendungsfälle wie zum Beispiel Reverse Proxy Konfiguration benötigt.<br><br>"
            + "Im Falle der PEM Schlüsseldatei wird der Schlüssel ohne Passwort gespeichert."
            + "</HTML>"},
        {"key.exported.to.file", "Der Schlüssel \"{0}\" wurde in die Datei \"{1}\" exportiert."},};

}
