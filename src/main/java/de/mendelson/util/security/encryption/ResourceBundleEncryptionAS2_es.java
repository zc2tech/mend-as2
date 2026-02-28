//$Header: /as2/de/mendelson/util/security/encryption/ResourceBundleEncryptionAS2_es.java 1     5/12/24 16:38 Heller $
package de.mendelson.util.security.encryption;

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
* @version $Revision: 1 $
*/
public class ResourceBundleEncryptionAS2_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"encryption.29", "AES-128 (GCM, RSAES-OAEP)"},
		{"encryption.27", "CAMELIA-192 (CBC)"},
		{"encryption.28", "CAMELIA-256 (CBC)"},
		{"encryption.0", "Desconocido"},
		{"encryption.2", "3DES"},
		{"encryption.1", "Sin cifrar"},
		{"encryption.4", "RC2-64"},
		{"encryption.25", "CHACHA20-POLY1305"},
		{"encryption.3", "RC2-40"},
		{"encryption.26", "CAMELIA-128 (CBC)"},
		{"encryption.6", "RC2-196"},
		{"encryption.23", "AES-192 (CCM)"},
		{"encryption.5", "RC2-128"},
		{"encryption.24", "AES-256 (CCM)"},
		{"encryption.8", "AES-128 (CBC)"},
		{"encryption.21", "AES-256 (GCM)"},
		{"encryption.7", "RC2"},
		{"encryption.22", "AES-128 (CCM)"},
		{"encryption.9", "AES-192 (CBC)"},
		{"encryption.20", "AES-192 (GCM)"},
		{"encryption.18", "AES-256 (CBC, RSAES-OAEP)"},
		{"encryption.19", "AES-128 (GCM)"},
		{"encryption.16", "AES-128 (CBC, RSAES-OAEP)"},
		{"encryption.17", "AES-192 (CBC, RSAES-OAEP)"},
		{"encryption.14", "RC4"},
		{"encryption.15", "DES"},
		{"encryption.12", "RC4-56"},
		{"encryption.13", "RC4-128"},
		{"encryption.10", "AES-256 (CBC)"},
		{"encryption.11", "RC4-40"},
		{"encryption.99", "Desconocido"},
		{"encryption.30", "AES-192 (GCM, RSAES-OAEP)"},
		{"encryption.31", "AES-256 (GCM, RSAES-OAEP)"},
	};
}
