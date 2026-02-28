//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateUsedByPartnerChecker_it.java 2     5/12/24 11:50 Heller $
package de.mendelson.comm.as2.partner;

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
public class ResourceBundleCertificateUsedByPartnerChecker_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"used.crypt.overwritelocalsecurity", "Cifratura dei dati (sovrascrive la stazione locale)"},
		{"used.crypt", "Crittografia dei dati"},
		{"used.sign.overwritelocalsecurity", "Firma digitale (sovrascrive la stazione locale)"},
		{"used.sign", "Firma digitale"},
	};
}
