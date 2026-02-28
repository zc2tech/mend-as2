//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames_fr.java 2     9/12/24 15:51 H $
package de.mendelson.util.security.cert.gui.keygeneration;

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
* @version $Revision: 2 $
*/
public class ResourceBundleDialogSubjectAlternativeNames_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.cancel", "Démolition"},
		{"label.add", "Ajouter"},
		{"header.name", "Type"},
		{"label.del", "Supprimer"},
		{"button.ok", "Ok"},
		{"title", "Gérer les noms alternatifs des demandeurs"},
		{"header.value", "Valeur"},
		{"info", "Cette boîte de dialogue permet de gérer les noms alternatifs des demandeurs pour le processus de génération de la clé (subject alternative name). Ces valeurs sont une extension du certificat x.509. Si votre partenaire le supporte, vous pouvez par exemple saisir ici des domaines supplémentaires pour votre clé. Dans OFTP2, il peut être nécessaire, selon le partenaire, de remplir certains champs avec des données d''identification, par exemple l''Odette Id de votre système comme URL au format \"oftp://OdetteId\" et à nouveau votre domaine dans le champ Nom DNS."},
	};
}
