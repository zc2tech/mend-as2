//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore_de.java 6     9/12/24 15:51 Heller $ 
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
 * @version $Revision: 6 $
 */
public class ResourceBundleExportKeystore_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"button.browse", "Browse"},
        {"label.exportdir", "Exportverzeichnis"},
        {"label.exportdir.hint", "Verzeichnis, in dem die Keystoredatei angelegt wird"},
        {"label.exportdir.help", "<HTML><strong>Exportverzeichnis</strong><br><br>"
            + "Bitte geben Sie das Exportverzeichnis an, in das der Keystore exportiert werden soll.<br>"
            + "Aus Sicherheitsgründen werden die Schlüssel nicht auf den Client übertragen, <br>"
            + "so dass nur ein Speichern auf der Serverseite möglich ist.<br>"
            + "Das System erstellt in diesem Verzeichnis eine Speicherdatei, die einen Datumsstempel enthält."
            + "</HTML>"},
        {"label.keypass", "Password"},
        {"label.keypass.hint", "Passwort des exportierten Keystores"},
        {"label.keypass.help", "<HTML><strong>Passwort des exportierten Keystores</strong><br><br>"
            + "Dies ist das Passwort, mit dem der serverseitig exportierte Keystore gesichert ist.<br>"
            + "Bitte geben Sie \"test\" ein, wenn dieser Keystore später automatisch in das "
            + "mendelson Produkt importiert werden soll."
            + "</HTML>"},
        {"title", "Alle Einträge in Keystore Datei exportieren"},
        {"filechooser.key.export", "Bitte wählen Sie das serverseitige Exportverzeichnis"},
        {"keystore.export.success.title", "Erfolg"},
        {"keystore.export.error.message", "Beim Exportieren gab es ein Problem: \n{0}"},
        {"keystore.export.error.title", "Fehler"},
        {"keystore.exported.to.file", "Die Keystore Datei wurde geschrieben nach \"{0}\"."},};

}
