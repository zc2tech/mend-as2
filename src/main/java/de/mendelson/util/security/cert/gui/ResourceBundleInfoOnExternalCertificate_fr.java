//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate_fr.java 7     9/12/24 15:51 Heller $
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
 * @author E.Pailleau
 * @version $Revision: 7 $
 */
public class ResourceBundleInfoOnExternalCertificate_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"button.ok", "Importer >>"},
        {"button.cancel", "Annuler"},
        {"title.single", "Info sur le certificat externe"},
        {"title.multiple", "Info sur les certificats externe"},
        {"certinfo.certfile", "Dossier de certificat: {0}"},
        {"certificate.exists", "Le certificat existe déjà dans le gestion des certificats, l''alias \"{0}\""},
        {"certificate.doesnot.exist", "Le certificat n''existe pas dans le gestion des certificats"},
        {"no.certificate", "Impossible d'identifier le certificat" },
    };

}
