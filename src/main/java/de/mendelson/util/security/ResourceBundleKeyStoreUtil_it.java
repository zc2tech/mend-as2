//$Header: /oftp2/de/mendelson/util/security/ResourceBundleKeyStoreUtil_it.java 4     9/12/24 15:51 Heller $
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
* @version $Revision: 4 $
*/
public class ResourceBundleKeyStoreUtil_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"alias.exist", "Una voce con l''alias \"{0}\" esiste già nel keystore sottostante."},
		{"privatekey.notfound", "Il keystore non contiene una chiave privata con l''alias \"{0}\"."},
		{"readerror.zipcert", "Non si tratta di un certificato valido, ma di un archivio zip."},
		{"readerror.invalidcert", "Il certificato non è valido o utilizza una codifica non supportata."},
		{"alias.rename.new.equals.old", "Rinomina una voce del keystore: Il nuovo e il vecchio alias sono identici."},
		{"ssh2.algorithmn.not.supported", "La codifica SSH2 non è supportata per le chiavi dell''algoritmo \"{0}\". Gli algoritmi supportati sono: DSA, RSA"},
	};
}
