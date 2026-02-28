//$Header: /as2/de/mendelson/comm/as2/cem/ResourceBundleCEM_fr.java 13    9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem;
import de.mendelson.comm.as2.cem.messages.TrustResponse;
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
 * @version $Revision: 13 $
 */
public class ResourceBundleCEM_fr extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {      
        {"trustrequest.rejected", "La réponse à la demande de fiducie reçue a reçu le statut \"" + TrustResponse.STATUS_REJECTED_STR + "\"." },
        {"trustrequest.accepted", "La réponse à la demande de fiducie reçue a reçu le statut \"" + TrustResponse.STATUS_ACCEPTED_STR + "\"." },
        {"trustrequest.working.on", "Traiter au Trust Request {0}." },
        {"trustrequest.certificates.found", "Nombre de certificats transférés: {0}." },
        {"cem.validated.schema", "Le CEM entrant a été validé avec succès." },
        {"cem.structure.info", "Nombre de demandes de confiance dans le CEM entrant: {0}" },
        {"transmitted.certificate.info", "Le certificat transmis a les caractéristiques IssuerDN=\"{0}\" et numéro de série \"{1}\"." },
        {CEMReceiptController.KEYSTORE_TYPE_ENC_SIGN +".cert.already.imported", "Le certificat CEM soumis existe déjà dans le keystore [enc/sign] (alias {0}), l''importation a été ignorée."},
        {CEMReceiptController.KEYSTORE_TYPE_SSL +".cert.already.imported", "Le certificat CEM soumis existe déjà dans le keystore [TLS] (alias {0}), l''importation a été ignorée."},
        {CEMReceiptController.KEYSTORE_TYPE_ENC_SIGN +".cert.imported.success", "Le certificat CEM soumis a été correctement importé [enc/sign] (alias {0})."},
        {CEMReceiptController.KEYSTORE_TYPE_SSL +".cert.imported.success", "Le certificat CEM soumis a été correctement importé [TLS] (alias {0})."},
        {"category." + CEMEntry.CATEGORY_CRYPT, "Cryptage" },
        {"category." + CEMEntry.CATEGORY_SIGN, "Signature" },
        {"category." + CEMEntry.CATEGORY_TLS, "TLS" },
        {"state." + CEMEntry.STATUS_ACCEPTED_INT, "Acceptée par {0}" },
        {"state." + CEMEntry.STATUS_PENDING_INT, "Pas de réponse si loin de {0}" },
        {"state." + CEMEntry.STATUS_REJECTED_INT, "Rejetée par {0}" },
        {"state." + CEMEntry.STATUS_CANCELED_INT, "Annulée" },
        {"state." + CEMEntry.STATUS_PROCESSING_ERROR_INT, "Erreur de traitement" },
        {"cemtype.response", "Le CEM message est du type \"certificate response\"" },
        {"cemtype.request", "Le CEM message est du type \"certificate request\"" },
        {"cem.response.relatedrequest.found", "La réponse de CEM se rapporte à la demande existante \"{0}\"" },
        {"cem.response.prepared", "Le message de réponse de CEM a été créé pour la demande {0}" },
        {"cem.created.request", "La requête CEM a été générée pour la relation \"{0}\"-\"-\"{1}\". Le certificat avec issuerDN \"{2}\" et le numéro de série \"{3}\" a été intégré. L'utilisation définie est {4}." },
    };
    
}