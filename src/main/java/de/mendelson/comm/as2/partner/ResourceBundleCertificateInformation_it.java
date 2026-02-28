//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateInformation_it.java 2     5/12/24 11:50 Heller $
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
public class ResourceBundleCertificateInformation_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"partner.sign.prio", "La firma digitale dei messaggi in arrivo dal partner \"{0}\" viene verificata utilizzando il certificato \"{1}\"."},
		{"localstation.decrypt", "I messaggi in arrivo per la stazione locale \"{0}\" vengono decifrati utilizzando il certificato \"{1}\"."},
		{"localstation.sign", "I messaggi in uscita dalla stazione locale \"{0}\" sono firmati digitalmente tramite il certificato \"{1}\"."},
		{"partner.encrypt", "I messaggi in uscita verso il partner \"{0}\" sono crittografati utilizzando il certificato \"{1}\"."},
	};
}
