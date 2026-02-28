//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_fr.java 8     9/12/24 15:51 Heller $
package de.mendelson.util.security.csr;
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
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class ResourceBundleCSRUtil_fr extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    private static final Object[][] CONTENTS = {                
        {"verification.failed", "Vérification de la CSR created a échoué" },
        {"no.certificates.in.reply", "Aucun certificat de la réponse de la CSR, incapable de patcher la clé" },
        {"missing.cert.in.trustchain", "Les certificats de la chaîne de confiance (certificat racine et intermédiaire) manquent "
            + "dans le système pour cette opération.\nVous recevrez ces certificats de votre autorité de certification.\n"
            + "Veuillez importer le certificat avec le \nissuer\n {0} keystore du premier." },
        {"response.chain.incomplete", "La chaîne de certificats de la réponse est incomplète" },
        {"response.verification.failed", "Problème de vérification de la chaîne de certificats de la réponse: {0}" },
        {"response.public.key.does.not.match", "Ce n''est pas la solution CA de cette clé." },
    };

}