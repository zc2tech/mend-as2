//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12.java 1     5/12/24 10:58 Heller $ 
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
 * @version $Revision: 1 $
 */
public class ResourceBundleExportKeyPKCS12 extends MecResourceBundle {

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
        {"button.cancel", "Cancel"},
        {"button.browse", "Browse"},
        {"keystore.contains.nokeys", "This keystore does not contain private keys."},        
        {"label.exportdir", "Export directory"},
        {"label.exportdir.hint", "Directory the keystore will be created in (PKCS#12)"},
        {"label.exportdir.help", "<HTML><strong>Export directory</strong><br><br>"
            + "Please specify the export directory to which the private key should be exported here.<br>"
            + "For security reasons, the key is not transferred to the client, so only saving on the server side is possible.<br><br>"
            + "The system will create a save file in this directory that contains a date stamp."
            + "</HTML>"},
        {"label.keypass", "Password"},
        {"label.keypass.hint", "Exported keystore password"},
        {"title", "Export key to keystore(PKCS#12 format)"},
        {"filechooser.key.export", "Please select the export directory on the server"},
        {"key.export.success.title", "Success"},
        {"key.export.error.message", "There occured an error during the export process.\n{0}"},
        {"key.export.error.title", "Error"},
        {"label.alias", "Key"},
        {"key.exported.to.file", "The key \"{0}\" has been written to the PKCS#12 file \"{1}\"."},};

}
