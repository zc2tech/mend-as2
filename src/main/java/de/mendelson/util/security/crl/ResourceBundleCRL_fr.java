//$Header: /oftp2/de/mendelson/util/security/crl/ResourceBundleCRL_fr.java 4     9/12/24 15:51 Heller $
package de.mendelson.util.security.crl;

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
public class ResourceBundleCRL_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"module.name", "[Liste de blocage]"},
        {"self.signed.skipped", "Auto-signé - vérification ignorée"},
        {"crl.success", "Ok - le certificat n''est pas révoqué"},
        {"failed.revoked", "Le certificat est révoqué : {0}"},
        {"malformed.crl.url", "URL CRL erronée ({0})"},
        {"no.https", "Problème de connexion avec l''URI {0} - HTTPS n''est pas supporté"},
        {"bad.crl", "Les données CRL téléchargées ne sont pas traitables"},
        {"cert.read.error", "Impossible de lire le certificat pour l''URL de la liste de révocation"},
        {"error.url.retrieve", "Impossible de lire l''URL de la liste de révocation à partir du certificat"},
        {"no.crl.entry", "Le certificat n''a pas d''extension qui renvoie à une URL CRL." },
        {"download.failed.from", "Le téléchargement de la liste de révocation a échoué ({0})"},
    };

}
