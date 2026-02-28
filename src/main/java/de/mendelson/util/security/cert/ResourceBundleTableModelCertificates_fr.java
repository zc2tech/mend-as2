//$Header: /as2/de/mendelson/util/security/cert/ResourceBundleTableModelCertificates_fr.java 6     4/03/25 14:41 Heller $
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
* @version $Revision: 6 $
*/
public class ResourceBundleTableModelCertificates_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.algorithm", "Algorithme"},
		{"header.alias", "Alias"},
		{"header.expire", "Valable jusqu''au"},
		{"header.length", "Longueur"},
		{"header.organization", "Organisation"},
		{"header.trust", "Légalisation"},
		{"trust.root", "Certificat racine"},
		{"trust.selfsigned", "Auto-signé"},
		{"trust.trusted", "Digne de confiance"},
		{"trust.untrusted", "Pas digne de confiance"},
	};
}
