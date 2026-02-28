//$Header: /as4/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore.java 5     13/09/24 12:23 Heller $ 
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
 * @version $Revision: 5 $
 */
public class ResourceBundleExportKeystore extends MecResourceBundle {

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
        {"button.cancel", "Cancel"},
        {"button.browse", "Browse"},
        {"label.exportdir", "Export directory"},
        {"label.exportdir.hint", "Directory the keystore will be created in"},
        {"label.exportdir.help", "<HTML><strong>Export directory</strong><br><br>"
            + "Please specify the export directory to which the keystore should be exported to.<br>"
            + "For security reasons, the keys are not transferred to the client, so only saving on the server side is possible.<br><br>"
            + "The system will create a save file in this directory that contains a date stamp."
            + "</HTML>"},
        {"label.keypass", "Password"},
        {"label.keypass.hint", "Exported keystore password"},
        {"label.keypass.help", "<HTML><strong>Exported keystore password</strong><br><br>"
            + "This is the password the server side exported keystore is secured with. Please enter \"test\" if "
            + "this keystore should be automatically imported later into the mendelson product."
            + "</HTML>"},
        {"title", "Export all entries as keystore file"},
        {"filechooser.key.export", "Please select the export directory on the server"},
        {"keystore.export.success.title", "Success"},
        {"keystore.export.error.message", "There occured an error during the export process.\n{0}"},
        {"keystore.export.error.title", "Error"},
        {"label.alias", "Key"},
        {"keystore.exported.to.file", "The keystore has been written to the keystore file \"{0}\"."},};

}
