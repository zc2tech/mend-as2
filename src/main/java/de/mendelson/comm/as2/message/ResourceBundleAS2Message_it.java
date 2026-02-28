//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2Message_it.java 2     5/12/24 11:50 Heller $
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
* @version $Revision: 2 $
*/
public class ResourceBundleAS2Message_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"signature.0", "Sconosciuto"},
		{"compression.1", "Nessuno"},
		{"signature.1", "Nessuna firma"},
		{"compression.0", "Sconosciuto"},
		{"compression.2", "ZLIB"},
		{"signature.4", "SHA-224"},
		{"signature.5", "SHA-256"},
		{"signature.2", "SHA-1"},
		{"signature.3", "MD5"},
		{"encryption.18", "AES-256 (CBC, RSAES-OAEP)"},
		{"encryption.19", "AES-128 (GCM)"},
		{"encryption.16", "AES-128 (CBC, RSAES-OAEP)"},
		{"encryption.17", "AES-192 (CBC RSAES-OAEP)"},
		{"encryption.14", "RC4"},
		{"encryption.15", "DES"},
		{"encryption.12", "RC4-56"},
		{"encryption.13", "RC4-128"},
		{"encryption.10", "AES-256 (CBC)"},
		{"encryption.11", "RC4-40"},
		{"encryption.99", "Sconosciuto"},
		{"direction.0", "Sconosciuto"},
		{"direction.1", "In arrivo"},
		{"direction.2", "A partire da"},
		{"encryption.29", "AES-128 (GCM, RSAES-OAEP)"},
		{"encryption.27", "CAMELIA-192 (CBC)"},
		{"encryption.28", "CAMELIA-256 (CBC)"},
		{"signature.19", "SHA3-384 (RSASSA-PSS)"},
		{"signature.18", "SHA3-256 (RSASSA-PSS)"},
		{"signature.17", "SHA3-224 (RSASSA-PSS)"},
		{"signature.16", "SHA3-512"},
		{"encryption.0", "Sconosciuto"},
		{"signature.15", "SHA3-384"},
		{"signature.14", "SHA3-256"},
		{"encryption.2", "3DES"},
		{"signature.13", "SHA3-224"},
		{"encryption.1", "Nessuna crittografia"},
		{"signature.12", "SHA-512 (RSASSA-PSS)"},
		{"encryption.4", "RC2-64"},
		{"encryption.25", "CHACHA20-POLY1305"},
		{"signature.11", "SHA-384 (RSASSA-PSS)"},
		{"encryption.3", "RC2-40"},
		{"encryption.26", "CAMELIA-128 (CBC)"},
		{"signature.10", "SHA-256 (RSASSA-PSS)"},
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
		{"signature.8", "SHA-1 (RSASSA-PSS)"},
		{"signature.9", "SHA-224 (RSASSA-PSS)"},
		{"signature.22", "DILITIO"},
		{"signature.6", "SHA-384"},
		{"signature.21", "SPHINCS+"},
		{"signature.7", "SHA-512"},
		{"signature.20", "SHA3-512 (RSASSA-PSS)"},
		{"encryption.30", "AES-192 (GCM RSAES-OAEP)"},
		{"encryption.31", "AES-256 (GCM, RSAES-OAEP)"},
	};
}
