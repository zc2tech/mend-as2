//$Header: /as2/de/mendelson/util/security/cert/ResourceBundleTableModelCertificates_it.java 3     4/03/25 14:41 Heller $
package de.mendelson.util.security.cert;

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
* @version $Revision: 3 $
*/
public class ResourceBundleTableModelCertificates_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.algorithm", "Algoritmo"},
		{"header.alias", "Alias"},
		{"header.expire", "Valido fino a"},
		{"header.length", "Lunghezza"},
		{"header.organization", "Organizzazione"},
		{"header.trust", "Notarizzazione"},
		{"trust.root", "Certificato di Master"},
		{"trust.selfsigned", "Autofirmato"},
		{"trust.trusted", "Degno di fiducia"},
		{"trust.untrusted", "Non affidabile"},
	};
}
