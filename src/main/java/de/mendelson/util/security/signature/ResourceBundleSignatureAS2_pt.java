//$Header: /as2/de/mendelson/util/security/signature/ResourceBundleSignatureAS2_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.security.signature;

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
public class ResourceBundleSignatureAS2_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"signature.0", "Desconhecido"},
		{"signature.1", "Sem assinatura"},
		{"signature.4", "SHA-224"},
		{"signature.5", "SHA-256"},
		{"signature.2", "SHA-1"},
		{"signature.3", "MD5"},
		{"signature.19", "SHA3-384 (RSASSA-PSS)"},
		{"signature.18", "SHA3-256 (RSASSA-PSS)"},
		{"signature.17", "SHA3-224 (RSASSA-PSS)"},
		{"signature.16", "SHA3-512"},
		{"signature.15", "SHA3-384"},
		{"signature.14", "SHA3-256"},
		{"signature.13", "SHA3-224"},
		{"signature.8", "SHA-1 (RSASSA-PSS)"},
		{"signature.12", "SHA-512 (RSASSA-PSS)"},
		{"signature.9", "SHA-224 (RSASSA-PSS)"},
		{"signature.11", "SHA-384 (RSASSA-PSS)"},
		{"signature.22", "DILÍTIO"},
		{"signature.6", "SHA-384"},
		{"signature.10", "SHA-256 (RSASSA-PSS)"},
		{"signature.21", "SPHINCS+"},
		{"signature.7", "SHA-512"},
		{"signature.20", "SHA3-512 (RSASSA-PSS)"},
	};
}
