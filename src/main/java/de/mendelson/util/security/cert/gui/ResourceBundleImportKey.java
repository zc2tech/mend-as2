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
 * @version $Revision: 4 $
 */
public class ResourceBundleImportKey extends MecResourceBundle {

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
        {"label.importkey", "Filename"},
        {"label.importkey.hint", "Import keystore file (PKCS#12, JKS)"},
        {"label.keypass", "Password"},
        {"label.keypass.hint", "The keystore password of the keystore file to import"},
        {"title", "Import keys from keystore(PKCS#12, JKS format)"},
        {"filechooser.key.import", "Please select the keystore file for the import (PKCS#12, JKS)"},
        {"multiple.keys.message", "Please select the key to import"},
        {"multiple.keys.title", "Keystore contains multiple keys"},
        {"key.import.success.message", "The key has been imported successfully."},
        {"key.import.success.title", "Success"},
        {"key.import.error.message", "There occured an error during the import process.\n{0}"},
        {"key.import.error.title", "Error"},
        {"enter.keypassword", "Enter key password for \"{0}\""},
        {"key.import.error.entry.exists", "Import not possible - an entry with this fingerprint does already exist, the alias is {0}."},
    };

}
