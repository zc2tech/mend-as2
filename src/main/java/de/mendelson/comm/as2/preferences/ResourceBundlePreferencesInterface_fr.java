//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface_fr.java 10    9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.timing.PartnerTLSCertificateChangedController;
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
 * @version $Revision: 10 $
 */
public class ResourceBundlePreferencesInterface_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.cem", "Permettre l''échange de certificat (CEM)"},
        {"label.outboundstatusfiles", "Écrire des fichiers de statut de transaction sortante"},
        {"label.outboundstatusfiles.help", "<HTML><strong>Écrire des fichiers de statut de transaction sortante</strong><br><br>"
            + "Si vous activez cette option, un fichier d''état est écrit pour chaque transaction "
            + "sortante dans le répertoire outboundstatus. Ce fichier sert à des fins d''intégration "
            + "et contient des informations sur la transaction en question. Il s''agit par exemple du "
            + "statut de la transaction, du numéro de message, de l''identification de l''expéditeur "
            + "et du destinataire. Le nom du fichier d'état contient le numéro du message et se termine "
            + "par \".sent.state\".<br><br>"
            + "Après l''envoi de données, vous pouvez analyser ce fichier et voir quel est le statut de la "
            + "transaction."
            + "</HTML>"},
        {"label.showsecurityoverwrite", "Gestion des partenaires: Remplacer les paramètres de sécurité de la station locale"},
        {"label.showsecurityoverwrite.help", "<HTML><strong>Remplacer les paramètres de sécurité de la station locale</strong><br><br>"
            + "Si vous activez cette option, un onglet supplémentaire s''affiche pour chaque partenaire "
            + "dans la gestion des partenaires.<br>Vous pouvez y définir les clés privées qui seront utilisées "
            + "dans tous les cas pour ce partenaire à l''entrée et à la sortie - "
            + "indépendamment des paramètres de la station locale correspondante.<br><br>"
            + "Cette option vous permet d'utiliser des clés privées différentes pour chaque partenaire "
            + "avec la même station locale. Il s''agit d'une option de compatibilité avec d''autres produits AS2 - "
            + "certains systèmes ont exactement les mêmes exigences, "
            + "mais qui nécessitent une configuration de relations de partenaires et non de partenaires individuels."
            + "</HTML>"},
        {"label.showhttpheader", "Gestion des partenaires: Laissez configurer les en-têtes de HTTP"},
        {"label.showhttpheader.help", "<HTML><strong>Laissez configurer les en-têtes de HTTP</strong><br><br>"
            + "Si vous activez cette option, un onglet supplémentaire s''affiche par partenaire dans "
            + "la gestion des partenaires, dans lequel vous pouvez définir des en-têtes "
            + "HTTP personnalisés pour l''envoi de données à ce partenaire."
            + "</HTML>"},
        {"label.showquota", "Gestion des partenaires: Laissez configurer l''avis de quote-part"},
        {"label.checkrevocationlists", "Certificats: Vérifier les listes de révocation"},
        {"label.checkrevocationlists.help", "<HTML><strong>Certificats : Vérification des listes de révocation</strong><br><br>"
            + "Une liste de révocation est une liste de certificats qui ont été invalidés en raison de divers problèmes "
            + "ou préoccupations en matière de sécurité. Ces problèmes peuvent inclure la compromission de la clé privée, "
            + "la perte du certificat ou la suspicion d''activité frauduleuse. Les listes de révocation sont gérées par "
            + "des autorités de certification ou d''autres entités de confiance autorisées à délivrer des certificats. "
            + "La vérification des listes de révocation est importante pour s''assurer que les certificats utilisés "
            + "dans une connexion ou pour une opération cryptographique sont valides et dignes de confiance. Un certificat "
            + "figurant sur une liste de révocation ne doit plus être utilisé pour des opérations cryptographiques, "
            + "car il pourrait présenter des risques potentiels pour la sécurité et compromettre l''intégrité de la communication.<br><br>"
            + "Avec l''aide de ce paramètre, vous pouvez déterminer si le système vérifie également les listes de révocation "
            + "lors de la validation de la configuration."
            + "</HTML>"
        },
        {"autoimport.tls", "Certificats TLS: Importation automatique en modification"},
        {"autoimport.tls.help", "<HTML><strong>Certificats TLS: Importation automatique en modification</strong><br><br>"
            + "Si une connexion partenaire est réalisée via HTTPS (TLS, l''URL commence par \"https\"), il est possible "
            + "de vérifier régulièrement si le certificat TLS a été modifié. S''il a été modifié "
            + "et n''est pas encore dans le système, il est alors automatiquement importé avec toute la chaîne d''authentification.<br>"
            + "Le système vérifiera les certificats TLS des partenaires toutes les " 
            + PartnerTLSCertificateChangedController.CHECK_DELAY_IN_MIN + " minutes. Cela signifie qu''il peut s''écouler "
            + "un certain temps avant que le changement de certificat TLS d''un partenaire ne soit détecté.<br><br>"
            + "Vous pouvez également effectuer ce processus manuellement en effectuant un test de "
            + "connexion à un partenaire et en important ensuite les certificats TLS manquants.<br><br>"
            + "Veuillez noter qu''il s''agit d''un réglage problématique au niveau de la sécurité, car il vous permet de "
            + "faire automatiquement confiance à un certificat trouvé - sans demande."
            + "</HTML>"
        },};
}
