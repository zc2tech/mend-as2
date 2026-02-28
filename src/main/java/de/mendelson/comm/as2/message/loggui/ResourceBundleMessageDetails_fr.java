//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_fr.java 19    17/01/25 10:06 Heller $
package de.mendelson.comm.as2.message.loggui;

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
* @author S.Heller
* @version $Revision: 19 $
*/
public class ResourceBundleMessageDetails_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transactionstate.error.connectionrefused", "<HTML>Vous avez essayé de contacter le système partenaire. Soit cela a échoué, soit votre partenaire n''a pas répondu avec une confirmation dans le délai défini.</HTML>"},
		{"header.timestamp", "Date"},
		{"transactiondetails.outbound.insecure", "Il s''agit d''une connexion sortante non sécurisée, vous envoyez des données au partenaire \"{0}\"."},
		{"transactiondetails.outbound.sync", " Vous recevez la confirmation directement comme réponse sur le canal de retour de la connexion sortante (MDN synchrone)."},
		{"header.useragent", "Serveur AS2"},
		{"transactionstate.error.authentication-failed", "<HTML>Le destinataire du message n''a pas pu vérifier avec succès la signature de l''expéditeur dans les données. Il s''agit généralement d''un problème de configuration, car l''expéditeur et le destinataire doivent ici utiliser le même certificat. Veuillez également consulter les détails MDN dans le protocole - celui-ci pourrait contenir plus d''informations.</HTML>"},
		{"title", "Détails du message"},
		{"transactionstate.error.messagecreation.details", "<HTML>Le système n''a pas pu générer la structure de message requise en raison d''un problème de votre côté. Cela n''a rien à voir avec votre système partenaire, aucune connexion n''a été établie.</HTML>"},
		{"message.raw.decrypted", "Données de transmission (non cryptées)"},
		{"transactionstate.error.asyncmdnsend", "<HTML>Un message contenant une requête MDN asynchrone a été reçu et traité avec succès, mais votre système n''a pas pu renvoyer le MDN asynchrone ou celui-ci n''a pas été accepté par le système partenaire.</HTML>"},
		{"transactionstate.error.connectionrefused.details", "<HTML>Il pourrait s''agir d''un problème d''infrastructure, votre système partenaire ne fonctionne pas du tout ou vous avez saisi la mauvaise URL de réception dans la configuration ? Si les données ont été transmises et que votre partenaire ne les a pas confirmées, il se peut que vous ayez choisi une fenêtre de temps trop petite pour la confirmation ?</HTML>"},
		{"transactionstate.ok.receive", "<HTML>Le message {0} a été reçu avec succès par le partenaire \"{1}\". Une confirmation a été envoyée au partenaire.</HTML>"},
		{"title.cem", "Détails du message de l''échange de certificats (CEM)"},
		{"header.encryption", "Cryptage"},
		{"header.messageid", "Numéro de référence"},
		{"transactionstate.error.unexpected-processing-error", "<HTML>Il s''agit d''un message d''erreur très générique. Pour une raison inconnue, le destinataire n''a pas pu traiter le message.</HTML>"},
		{"transactionstate.error.in", "<HTML>Vous avez reçu avec succès le message {0} de votre partenaire \"{1}\" - mais votre système n''a pas été en mesure de le traiter et a répondu avec l''erreur [{2}].</HTML>"},
		{"transactionstate.ok.details", "<HTML>Les données ont été transmises et la transaction a été effectuée avec succès</HTML>"},
		{"message.payload.multiple", "Données utiles ({0})"},
		{"transactionstate.ok.send", "<HTML>Le message {0} a été envoyé avec succès au partenaire \"{1}\" - il a envoyé un accusé de réception correspondant.</HTML>"},
		{"transactiondetails.outbound.secure", "Il s''agit d''une connexion sortante sécurisée, vous envoyez des données au partenaire \"{0}\"."},
		{"transactionstate.error.unknown", "Une erreur inconnue s''est produite."},
		{"transactiondetails.inbound.async", " Vous envoyez la confirmation en établissant une nouvelle connexion avec le partenaire (MDN asynchrone)."},
		{"transactionstate.error.decryption-failed", "<HTML>Le destinataire du message n''a pas pu décrypter le message. La plupart du temps, il s''agit d''un problème de configuration, l''expéditeur utilise-t-il le bon certificat pour le cryptage ?</HTML>"},
		{"message.header", "Données d''en-tête"},
		{"transactionstate.error.messagecreation", "<HTML>Un problème est survenu lors de la génération d''un message AS2 sortant</HTML>"},
		{"header.senderhost", "Émetteur"},
		{"transactiondetails.inbound.secure", "Il s''agit d''une connexion sécurisée entrante, vous recevez des données du partenaire \"{0}\"."},
		{"transactionstate.error.insufficient-message-security", "<HTML>Le destinataire du message s''attendait à un niveau de sécurité plus élevé pour les données reçues (par exemple, des données cryptées au lieu de données non cryptées).</HTML>"},
		{"transactiondetails.outbound.async", " Pour la confirmation, votre partenaire établit une nouvelle connexion avec vous (MDN asynchrone)."},
		{"transactionstate.error.asyncmdnsend.details", "<HTML>L''émetteur du message AS2 transmet l''URL à laquelle il doit renvoyer le MDN - soit ce système n''est pas accessible (problème d''infrastructure ou système partenaire en panne ?), soit le système partenaire n''a pas accepté le MDN asynchrone et a répondu par un HTTP 400.</HTML>"},
		{"transactionstate.pending", "Cette transaction est en attente."},
		{"transactionstate.error.decompression-failed", "<HTML>Le destinataire du message n''a pas pu décompresser le message reçu</HTML>"},
		{"button.ok", "Ok"},
		{"transactionstate.error.unknown-trading-partner", "<HTML>Vous et votre partenaire avez des identifiants AS2 différents pour les deux partenaires de la transmission dans la configuration. Les identifiants suivants ont été utilisés : \"{0}\" (émetteur du message), \"{1}\" (récepteur du message)</HTML>"},
		{"transactiondetails.inbound.insecure", "Il s''agit d''une connexion entrante non sécurisée, vous recevez des données du partenaire \"{0}\"."},
		{"message.payload", "Données utiles"},
		{"transactiondetails.inbound.sync", " Ils envoient la confirmation directement comme réponse sur le canal de retour de la connexion entrante (MDN synchrone)."},
		{"transactionstate.error.out", "<HTML>Vous avez transmis avec succès le message {0} à votre partenaire \"{1}\" - mais il n''a pas été en mesure de le traiter et a répondu avec l''erreur [{2}].</HTML>"},
		{"tab.log", "Log de cette instance de message"},
		{"header.signature", "Signature numérique"},
	};
}
