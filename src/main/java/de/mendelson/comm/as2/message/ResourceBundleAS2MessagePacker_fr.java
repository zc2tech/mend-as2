//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker_fr.java 21    17/01/25 8:41 Heller $
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
* @author S.Heller
* @version $Revision: 21 $
*/
public class ResourceBundleAS2MessagePacker_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"mdn.signed", "Le MDN sortant a été signé avec l''algorithme \"{0}\", l''alias de clé est \"{1}\" de la station locale \"{2}\"."},
		{"message.compressed.unknownratio", "Les données utiles sortantes ont été comprimées."},
		{"message.creation.error", "Le message avec le message Id \"{0}\" n''a pas pu être créé : {1}. Il s''agit d''un problème qui a déjà eu lieu lors de la création de la structure des messages sortants sur votre système - cela n''a rien à voir avec le système de votre partenaire et aucune tentative n''a été faite pour établir une connexion avec votre partenaire."},
		{"signature.no.aipa", "Le processus de signature n''utilise pas l''attribut Algorithm Identifier Protection dans la signature (comme défini dans la configuration) - ce n''est pas sûr !"},
		{"mdn.creation.start", "Créez des MDN sortants, définissez l''id des messages sur \"{0}\"."},
		{"message.compressed", "Les données utiles sortantes ont été comprimées de {0} à {1}."},
		{"mdn.details", "Détails du MDN sortant : {0}"},
		{"mdn.created", "MDN sortant créé pour le message AS2 \"{0}\", statut défini sur [{1}]."},
		{"mdn.notsigned", "Le MDN sortant n''a pas été signé."},
		{"message.signed", "Le message sortant a été signé numériquement avec l''algorithme \"{1}\", en utilisant la clé avec l''alias \"{0}\" de la station locale \"{2}\"."},
		{"message.encrypted", "Le message sortant a été chiffré avec l''algorithme {1}, en utilisant le certificat avec l''alias \"{0}\" du partenaire distant \"{2}\"."},
		{"message.creation.start", "Créer un message AS2 sortant, définir l''id du message sur \"{0}\"."},
		{"message.notsigned", "Le message sortant n''a pas été signé numériquement."},
		{"message.notencrypted", "Le message sortant n''a pas été chiffré."},
	};
}
