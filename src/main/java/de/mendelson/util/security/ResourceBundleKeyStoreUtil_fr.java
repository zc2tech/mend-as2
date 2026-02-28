//$Header: /oftp2/de/mendelson/util/security/ResourceBundleKeyStoreUtil_fr.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security;

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
public class ResourceBundleKeyStoreUtil_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"alias.exist", "Une entrée avec l''alias \"{0}\" existe déjà dans le keystore sous-jacent."},
		{"privatekey.notfound", "Le keystore ne contient pas de clé privée avec l''alias \"{0}\"."},
		{"readerror.zipcert", "Il ne s''agit pas d''un certificat valide, mais d''une archive zip."},
		{"readerror.invalidcert", "Le n''est pas un certificat valide ou il utilise un encodage non supporté."},
		{"alias.rename.new.equals.old", "Renommer une entrée de keystore : Le nouvel et l''ancien alias sont identiques."},
		{"ssh2.algorithmn.not.supported", "Le codage SSH2 n''est pas supporté pour les clés de l''algorithme \"{0}\". Les algorithmes pris en charge sont : DSA, RSA"},
	};
}
