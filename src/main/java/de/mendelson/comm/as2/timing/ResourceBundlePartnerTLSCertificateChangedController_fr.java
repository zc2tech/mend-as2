//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundlePartnerTLSCertificateChangedController_fr.java 5     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundlePartnerTLSCertificateChangedController_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"module.name", "Test de certificat TLS"},
        {"import.success", "Le certificat TLS \"{0}\" du partenaire [{1}] a été importé automatiquement."},
        {"import.success.event.header", "Importation automatique d''un certificat TLS" },
        {"import.success.event.body", "Le système est configuré de telle sorte qu''il contrôle régulièrement les "
            + "partenaires connectés via TLS pour voir s''ils ont modifié leur certificat TLS. Si c''est le cas "
            + "et que le certificat TLS n''existe pas dans votre gestionnaire de certificats TLS local, "
            + "il est automatiquement importé.\n"
            + "Le système a trouvé un nouveau certificat pour le "
            + "partenaire \"{0}\" sous l''URL \"{1}\" et l''a importé avec succès dans votre gestionnaire de "
            + "certificats TLS avec l''alias \"{2}\"." },
        {"import.failed", "Le certificat TLS du partenaire {0} n''a pas été importé automatiquement: {1}"},
        {"autoimport.tls.check.started", "L''importation automatique de certificats TLS partenaires modifiés a été activée."},
        {"autoimport.tls.check.stopped", "L''importation automatique de certificats TLS partenaires modifiés a été désactivée."},};

}
