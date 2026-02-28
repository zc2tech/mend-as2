//$Header: /as4/de/mendelson/util/security/cert/gui/ResourceBundleExportPrivateKey.java 2     13/09/24 12:23 Heller $ 
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
 * @version $Revision: 2 $
 */
public class ResourceBundleExportPrivateKey extends MecResourceBundle {

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
        {"keystore.contains.nokeys", "This keystore does not contain private keys."},        
        {"label.exportdir", "Export directory"},
        {"label.exportdir.hint", "Directory the export file will be stored in"},
        {"label.exportdir.help", "<HTML><strong>Export directory</strong><br><br>"
            + "Please specify the export directory to which the private key should be exported here.<br>"
            + "For security reasons, the key is not transferred to the client, so only saving on the server side is possible.<br><br>"
            + "The system will create a save file in this directory that contains a date stamp."
            + "</HTML>"},
        
        {"label.keypass", "Password"},
        {"label.keypass.hint", "Exported keyfile/keystore password"},
        {"title", "Export key to keystore or key file"},
        {"filechooser.key.export", "Please select the export directory on the server"},
        {"key.export.success.title", "Success"},
        {"key.export.error.message", "There occured an error during the export process.\n{0}"},
        {"key.export.error.title", "Error"},
        {"label.alias", "Key"},
        {"label.exportformat", "Export format"},
        {"label.exportformat.help", "<HTML><strong>Export format</strong><br><br>"
            + "You can export the key either in a keystore format (PKCS#12) "
            + "or a PEM-encoded key file.<br>"
            + "The most commonly used format is PKCS#12, a PEM-encoded key file is only "
            + "required for special applications such as reverse proxy configuration.<br><br>"
            + "In the case of the PEM key file, the private key is saved without a password."
            + "</HTML>"},
        {"key.exported.to.file", "The key \"{0}\" has been written to the file \"{1}\"."},};

}
