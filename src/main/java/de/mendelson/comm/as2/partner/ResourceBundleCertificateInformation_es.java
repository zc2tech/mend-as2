//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateInformation_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleCertificateInformation_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"partner.sign.prio", "La firma digital de los mensajes entrantes del interlocutor \"{0}\" se verifica mediante el certificado \"{1}\"."},
		{"localstation.decrypt", "Los mensajes entrantes para la estación local \"{0}\" se descifran utilizando el certificado \"{1}\"."},
		{"localstation.sign", "Los mensajes salientes de la estación local \"{0}\" se firman digitalmente mediante el certificado \"{1}\"."},
		{"partner.encrypt", "Los mensajes salientes al interlocutor \"{0}\" se cifran utilizando el certificado \"{1}\"."},
	};
}
