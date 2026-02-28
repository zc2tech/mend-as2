//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateInformation_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleCertificateInformation_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"partner.sign.prio", "A assinatura digital das mensagens recebidas do parceiro \"{0}\" é verificada utilizando o certificado \"{1}\"."},
		{"localstation.decrypt", "As mensagens recebidas para a estação local \"{0}\" são desencriptadas utilizando o certificado \"{1}\"."},
		{"localstation.sign", "As mensagens de saída da estação local \"{0}\" são assinadas digitalmente através do certificado \"{1}\"."},
		{"partner.encrypt", "As mensagens enviadas para o parceiro \"{0}\" são encriptadas utilizando o certificado \"{1}\"."},
	};
}
