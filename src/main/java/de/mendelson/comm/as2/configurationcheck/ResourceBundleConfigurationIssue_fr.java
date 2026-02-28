//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ResourceBundleConfigurationIssue_fr.java 9     21/02/25 16:04 Heller $
package de.mendelson.comm.as2.configurationcheck;

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
* @version $Revision: 9 $
*/
public class ResourceBundleConfigurationIssue_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"1", "Aucune clé trouvée dans le keystore TLS"},
		{"10", "Absence de certificat de signature d''un partenaire distant"},
		{"11", "Absence de clé de chiffrement d''une station locale"},
		{"12", "Absence de clé de signature d''une station locale"},
		{"13", "Utilisation d''une clé de test publiquement disponible comme clé TLS"},
		{"14", "L''utilisation d''une VM Java 32 bits n''est pas recommandée pour une utilisation en production, car la mémoire de tas maximale est alors limitée à 1,3 Go."},
		{"15", "Service Windows lancé avec un compte système local"},
		{"16", "Grande quantité de surveillance de répertoire par unité de temps"},
		{"17", "Problème de liste de blocage (TLS)"},
		{"18", "Problème de liste de blocage (enc/sign)"},
		{"19", "Le client et le serveur fonctionnent dans un seul processus"},
		{"2", "Plusieurs clés trouvées dans le keystore TLS - ne peut en contenir qu''une seule"},
		{"20", "Pas assez de handles pour le processus serveur"},
		{"3", "Le certificat a expiré (TLS)"},
		{"4", "Le certificat a expiré (enc/sign)"},
		{"5", "Activer la suppression automatique - Le système contient une grande quantité de transactions."},
		{"6", "Attribuez au moins 4 cœurs de processeur au système"},
		{"7", "Réservez au moins 8 Go de mémoire principale pour le processus serveur"},
		{"8", "La quantité de connexions sortantes est définie sur 0 - le système n''enverra PAS de messages."},
		{"9", "Absence de certificat de cryptage d''un partenaire distant"},
		{"hint.1", "<HTML>Aucune clé n''a été trouvée dans le keystore TLS de votre système.<br>"
			+"Vous reconnaissez les clés au symbole de clé qui les précède lorsque vous ouvrez le gestionnaire de certificats.<br>"
			+"Une seule clé est nécessaire dans le keystore TLS pour effectuer le processus de handshake de la sécurisation de la ligne.<br>"
			+"Sans cette clé, vous ne pouvez donc pas accéder à des connexions sécurisées, que ce soit en entrée ou en sortie.</HTML>"},
		{"hint.10", "<HTML>Un partenaire de connexion n''a pas attribué de certificat de signature dans votre configuration.<br>"
			+"Dans ce cas, vous ne pouvez pas vérifier les signatures numériques de votre partenaire. Veuillez ouvrir la gestion des partenaires et attribuer un certificat de signature au partenaire.</HTML>"},
		{"hint.11", "<HTML>Votre station locale n''a pas de clé de chiffrement attribuée.<br>"
			+"Dans cette configuration, vous ne pouvez pas décrypter les messages entrants, quel que soit le partenaire.<br>"
			+"Veuillez ouvrir la gestion des partenaires et attribuer une clé privée à la station locale.</HTML>"},
		{"hint.12", "<HTML>Votre station locale n''a pas de clé de signature attribuée.<br>"
			+"Dans cette configuration, vous ne pouvez pas signer numériquement les messages sortants - quel que soit le partenaire.<br>"
			+"Veuillez ouvrir la gestion des partenaires et attribuer une clé privée à la station locale.</HTML>"},
		{"hint.13", "<HTML>Dans la livraison, mendelson met à disposition quelques clés de test.<br>"
			+"Elles sont disponibles publiquement sur le site web de mendelson.<br>"
			+"Si vous utilisez ces clés de manière productive pour des tâches cryptographiques au sein de votre transfert de données, elles n''offrent donc <strong> AUCUNE</strong> sécurité.<br>"
			+"Ici, vous pouvez également envoyer des messages non sécurisés et non cryptés.<br>"
			+"Si vous avez besoin d''une clé certifiée, veuillez contacter le support mendelson.</HTML>"},
		{"hint.14", "<HTML>Les processus Java 32bit ne peuvent pas réserver suffisamment de mémoire pour maintenir la stabilité du système en production. Veuillez utiliser une JVM 64bit.</HTML>"},
		{"hint.15", "<HTML>Vous avez configuré le serveur mendelson AS2 en tant que service Windows et vous le démarrez via un compte système local (\"{0}\").<br>"
			+"Malheureusement, il est possible que cet utilisateur perde les droits sur les fichiers qu''il a écrits auparavant après une mise à jour de Windows, ce qui peut entraîner de nombreux problèmes de système.<br><br>"
			+"Veuillez créer votre propre utilisateur pour le service et démarrer le service avec cet utilisateur.</HTML>"},
		{"hint.16", "<HTML>Vous avez défini dans votre système une grande quantité de relations avec des partenaires et vous surveillez les répertoires sortants correspondants à des intervalles de temps trop courts.<br>"
			+"Actuellement, {0} surveillances de répertoire sont activées par minute, le système ne peut pas respecter ce taux élevé.<br>"
			+"Veuillez réduire cette valeur en augmentant les intervalles de surveillance des répertoires respectifs des partenaires et en désactivant également les surveillances pour les partenaires où cela n''est pas nécessaire.Si vous avez un grand nombre de partenaires, il est recommandé de désactiver toutes les surveillances de répertoires et de créer les ordres d''envoi depuis votre backend à l''aide des commandes <i>AS2Send.exe</i> ou <i>as2send.sh</i> selon les besoins.</HTML>"},
		{"hint.17", "<HTML>Les certificats authentifiés contiennent un lien vers une liste de révocation qui permet d''invalider ce certificat. Par exemple, si le certificat a été compromis.<br>"
			+"Il y a eu un problème lors de la vérification de la liste de révocation du certificat TLS suivant ou le certificat a été révoqué :<br>"
			+"<strong>{0}</strong><br><br>"
			+"Informations complémentaires sur ce certificat<br><br>"
			+"Alias : {1}<br>"
			+"Éditeur : {2}<br>"
			+"Empreinte digitale (SHA-1) : {3}<br><br><br>"
			+"Veuillez noter que la vérification CRL automatique peut être désactivée dans les paramètres.</HTML>"},
		{"hint.18", "<HTML>Les certificats authentifiés contiennent un lien vers une liste de révocation de certificats (CRL, certificate revocation list) qui permet d''invalider ce certificat. Par exemple, si le certificat a été compromis.<br>"
			+"Il y a eu un problème lors de la vérification de la liste de révocation du certificat enc/sign suivant ou le certificat a été révoqué :<br>"
			+"<strong>{0}</strong><br><br>"
			+"Informations complémentaires sur ce certificat<br><br>"
			+"Alias : {1}<br>"
			+"Éditeur : {2}<br>"
			+"Empreinte digitale (SHA-1) : {3}<br><br><br>"
			+"Veuillez noter que la vérification CRL automatique peut être désactivée dans les paramètres.</HTML>"},
		{"hint.19", "<HTML>Vous avez démarré le client et le serveur du produit dans un seul processus. Il n''est pas recommandé de le faire en mode de production. Comme les ressources sont attribuées aux programmes de manière statique, vous avez dans ce cas moins de ressources pour le fonctionnement du serveur et du client.<br><br>"
			+"Veuillez d''abord démarrer le processus serveur, puis vous connecter séparément au client.</HTML>"},
		{"hint.2", "<HTML>Dans le keystore TLS de votre système, il y a plusieurs clés. Toutefois, il ne peut en contenir qu''une seule, qui sera utilisée comme clé TLS au démarrage du serveur.<br>"
			+"Veuillez continuer à supprimer des clés du TLS Keystore jusqu''à ce qu''il n''y ait plus qu''une seule clé.<br>"
			+"Vous pouvez reconnaître les clés dans la gestion des certificats grâce au symbole de la clé dans la première colonne.<br>"
			+"Après cette modification, il est nécessaire de redémarrer le serveur.</HTML>"},
		{"hint.20", "<HTML>Vous pouvez limiter dans votre système d''exploitation le nombre de ports et de fichiers ouverts par utilisateur.<br>"
			+"L''utilisateur de votre processus actuel ne peut utiliser que {0} handles, ce qui est trop peu pour le fonctionnement du serveur. Actuellement, le processus serveur utilise {1} handles.<br>"
			+"Sous Linux, vous pouvez voir cette valeur avec \"ulimit -n\".<br><br>"
			+"Veuillez étendre la valeur maximale des handles disponibles pour ce processus à au moins {2}.</HTML>"},
		{"hint.3", "<HTML>Les certificats ont une durée de vie limitée. En général, il s''agit d''un, trois ou cinq ans.<br>"
			+"Un certificat que vous utilisez dans votre système pour la sécurisation de la ligne TLS n''est plus valable.<br>"
			+"Il n''est pas possible d''effectuer des opérations cryptographiques avec un certificat expiré - c''est pourquoi vous devez vous occuper de renouveler le certificat ou d''en créer un nouveau ou de le faire certifier.<br><br>"
			+"<strong>Informations complémentaires sur le certificat:</strong>.<br><br>"
			+"Alias : {0}<br>"
			+"Éditeur : {1}<br>"
			+"Empreinte digitale (SHA-1) : {2}<br>"
			+"Valable à partir du : {3}<br>"
			+"Valable jusqu''au : {4}<br><br>"
			+"</HTML>"},
		{"hint.4", "<HTML>Les certificats ont une durée de vie limitée. En général, il s''agit d''un, trois ou cinq ans.<br>"
			+"Un certificat que vous utilisez dans votre système pour un partenaire pour crypter/décrypter des données, pour signer numériquement ou pour vérifier une signature numérique<br>"
			+"Il n''est pas possible d''effectuer des opérations cryptographiques avec un certificat expiré - c''est pourquoi vous devez vous occuper de renouveler le certificat ou d''en créer un nouveau ou de le faire certifier.<br><br>"
			+"<strong>Informations complémentaires sur le certificat:</strong>.<br><br>"
			+"Alias : {0}<br>"
			+"Éditeur : {1}<br>"
			+"Empreinte digitale (SHA-1) : {2}<br>"
			+"Valable à partir du : {3}<br>"
			+"Valable jusqu''au : {4}<br><br>"
			+"</HTML>"},
		{"hint.5", "<HTML>Dans les paramètres, vous pouvez définir combien de temps les transactions doivent rester dans le système.<br>"
			+"Plus il reste de transactions dans le système, plus les ressources nécessaires à leur gestion sont importantes.<br>"
			+"C''est pourquoi vous devez veiller, à l''aide des paramètres, à ne jamais avoir plus de 30000 transactions au maximum dans le système.<br>"
			+"Veuillez noter qu''il ne s''agit pas d''un système d''archivage, mais d''un adaptateur de communication.<br>"
			+"Vous avez accès à tous les journaux des transactions passées via la fonction de recherche intégrée du journal du serveur.</HTML>"},
		{"hint.6", "<HTML>Pour un meilleur débit, il est nécessaire que différentes tâches soient exécutées en parallèle dans le système.<br>"
			+"Il est donc nécessaire de réserver un nombre correspondant de cœurs de CPU pour le processus.</HTML>"},
		{"hint.7", "<HTML>Ce programme est écrit en Java.<br>"
			+"Quelle que soit la configuration physique de votre ordinateur, vous devez réserver une quantité de mémoire appropriée au processus serveur. Dans votre cas, vous avez réservé trop peu de mémoire.<br>"
			+"Veuillez consulter l''aide (section Installation) - vous y trouverez comment réserver la mémoire correspondante pour chaque méthode de démarrage.<br><br>"
			+"Veuillez en tout cas vous assurer que vous ne réservez pas plus de mémoire pour le processus serveur que votre système n''en a de mémoire principale. Sinon, le logiciel devient presque inutilisable, car le système transfère constamment de la mémoire sur le disque dur.</HTML>"},
		{"hint.8", "<HTML>Vous avez effectué des modifications de configuration, de sorte qu''aucune connexion sortante n''est actuellement possible.<br>"
			+"Si vous souhaitez établir des connexions sortantes avec des partenaires, le nombre de connexions possibles devrait être fixé au moins à la valeur 1.</HTML>"},
		{"hint.9", "<HTML>Un partenaire de connexion n''a pas attribué de certificat de cryptage dans votre configuration.<br>"
			+"Dans ce cas, vous ne pouvez pas crypter les messages qui lui sont destinés. Veuillez ouvrir la gestion des partenaires et attribuer un certificat de cryptage au partenaire.</HTML>"},
	};
}
