//$Header: /mec_as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_fr.java 40    21/03/25 9:12 Heller $
package de.mendelson.comm.as2.message;

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
 * @version $Revision: 40 $
 */
public class ResourceBundleAS2MessageParser_fr extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"inbound.connection.syncmdn", "Un MDN synchrone a été reçu sur le canal de retour de votre connexion sortante."},
        {"original.filename.found", "Le nom de fichier original a été transmis par l''émetteur sous la forme \"{0}\"."},
        {"msg.incoming.identproblem", "La transmission entrante est un message AS2. Il n''a pas été traité car il y avait un problème avec l''identification du partenaire."},
        {"mdn.incoming.relationship", "La transmission reçue est un accusé de réception (MDN) [{0}]."},
        {"msg.already.processed", "Un message avec le numéro de message [{0}] a déjà été traité"},
        {"message.signature.using.alias", "Utilise le certificat \"{0}\" du partenaire distant \"{1}\" pour vérifier la signature numérique du message AS2 reçu."},
        {"found.attachments", "Des pièces jointes contenant des données utiles ont été trouvées {0} dans le message AS2."},
        {"contentmic.match", "Le code d''intégrité du message (MIC) correspond au message AS2 envoyé."},
        {"mdn.details", "Détails de l''accusé de réception reçu (MDN) de {0} : \"{1}\"."},
        {"msg.incoming.ha", "La transmission entrante est un message AS2 [{0}], taille des données brutes : {1}, traité par [{2}]."},
        {"mdn.signed.error", "L''accusé de réception reçu (MDN) est signé numériquement contrairement à la configuration du partenaire \"{0}\"."},
        {"mdn.notsigned", "L''accusé de réception reçu (MDN) n''est pas signé numériquement."},
        {"mdn.signature.using.alias", "Utilise le certificat \"{0}\" du partenaire distant \"{1}\" pour vérifier la signature numérique des MDN reçus."},
        {"signature.analyzed.digest", "L''algorithme \"{0}\" a été utilisé par l''émetteur pour la signature numérique."},
        {"mdn.signature.ok", "La signature numérique des MDN reçus a été vérifiée avec succès."},
        {"mdn.signature.failure", "La vérification de la signature numérique des MDN reçus a échoué - {0}"},
        {"data.compressed.expanded", "Les données utiles compressées du message AS2 reçu ont été développées de {0} à {1}."},
        {"mdn.state", "L''état de l''accusé de réception reçu (MDN) est [{0}]."},
        {"msg.signed", "Le message AS2 reçu est signé numériquement."},
        {"inbound.connection.tls", "Connexion TLS entrante de [{0}] vers le port {1} [{2}, {3}]."},
        {"mdn.answerto", "L''accusé de réception (MDN) reçu avec le numéro de message \"{0}\" est la réponse au message AS2 \"{1}\" qui a été émis."},
        {"msg.notsigned", "Le message AS2 reçu n''est pas signé numériquement."},
        {"decryption.inforequired", "Pour décrypter le message AS2 reçu, une clé avec les paramètres suivants est nécessaire :\n{0}"},
        {"invalid.original.filename.log", "Nom de fichier original non valide détecté dans la transaction. \"{0}\" est remplacé par \"{1}\" et le traitement se poursuit."},
        {"filename.extraction.error", "Impossible d''extraire le nom de fichier original du message AS2 reçu : \"{0}\", est ignoré."},
        {"mdn.incoming.ha", "La transmission reçue est un accusé de réception (MDN), traité par [{0}]."},
        {"mdn.incoming", "La transmission reçue est un accusé de réception (MDN)."},
        {"mdn.incoming.relationship.ha", "La transmission reçue est un accusé de réception (MDN) [{0}], traité par [{1}]."},
        {"mdn.signed", "L''accusé de réception (MDN) est signé numériquement ({0})."},
        {"signature.analyzed.digest.failed", "Le système n''a pas pu trouver l''algorithme de signature du message AS2 entrant."},
        {"msg.encrypted", "Le message AS2 reçu est crypté."},
        {"contentmic.failure", "Le code d''intégrité du message (MIC) ne correspond pas au message AS2 envoyé (attendu : {0}, reçu : {1})."},
        {"data.unable.to.process.content.transfer.encoding", "Des données ont été reçues, mais elles n''ont pas pu être traitées car elles contiennent des erreurs. Le codage de transfert de contenu \"{0}\" est inconnu."},
        {"original.filename.undefined", "Le nom de fichier d''origine n''a pas été transféré."},
        {"decryption.infoassigned", "Pour décrypter le message AS2 reçu, une clé avec les paramètres suivants a été utilisée (alias \"{0}\") :\n{1}"},
        {"data.not.compressed", "Les données AS2 reçues ne sont pas compressées."},
        {"inbound.connection.raw", "Connexion entrante de [{0}] vers le port {1}"},
        {"mdn.unsigned.error", "L''accusé de réception reçu (MDN) n''est PAS signé numériquement, contrairement à la configuration du partenaire \"{0}\"."},
        {"msg.incoming", "La transmission entrante est un message AS2 [{0}], taille des données brutes : {1}"},
        {"found.cem", "Le message reçu est une demande d''échange de certificats (CEM)."},
        {"msg.notencrypted", "Le message AS2 reçu n''est pas crypté."},
        {"message.signature.ok", "La signature numérique du message AS2 reçu a été vérifiée avec succès."},
        {"mdn.unexpected.messageid", "L''accusé de réception reçu (MDN) référence le message AS2 du numéro de référence \"{0}\", qui n''attend pas de MDN."},
        {"message.signature.failure", "La vérification de la signature numérique du message AS2 reçu a échoué - {0}"},
        {"decryption.done.alias", "Les données du message AS2 reçu ont été décryptées à l''aide de la clé \"{0}\" de la station locale \"{3}\", l''algorithme de cryptage était \"{1}\", l''algorithme de cryptage de la clé était \"{2}\"."},
        {"invalid.original.filename.title", "Nom de fichier original non valide détecté dans la transaction"},
        {"invalid.original.filename.body", "Le système a extrait un nom de fichier original invalide dans la transaction {0} de {1} vers {2}.\nLe nom de fichier trouvé \"{3}\" a été remplacé par \"{4}\" et le traitement a continué avec ce nouveau nom de fichier. Cela peut avoir des conséquences sur le déroulement de votre traitement, car les noms de fichiers contiennent parfois des métadonnées."},};
}
