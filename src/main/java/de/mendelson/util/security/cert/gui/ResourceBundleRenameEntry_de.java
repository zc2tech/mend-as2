//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleRenameEntry_de.java 13    9/12/24 15:51 Heller $ 
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
 * @version $Revision: 13 $
 */
public class ResourceBundleRenameEntry_de extends MecResourceBundle {

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
        {"label.newalias", "Neuer Alias"},
        {"label.newalias.hint", "Der Alias, der zukünftig verwendet werden soll"},
        {"title", "Alias ({0}) umbenennen"},
        {"alias.exists.title", "Umbennen des Alias schlug fehl" },
        {"alias.exists.message", "Der Alias \"{0}\" exisitert bereits im unterliegenden Keystore." },
    };

}
