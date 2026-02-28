//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleCertificateReference_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleCertificateReference_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.info.certificate", "<HTML>El certificado con el alias <strong>{0}</strong> es utilizado por los siguientes socios</HTML>"},
		{"label.notinuse.certificate", "<HTML>El certificado con el alias <strong>{0}</strong> no es utilizado por ningún interlocutor en la configuración</HTML>.</HTML>"},
		{"button.ok", "Ok"},
		{"label.info.key", "<HTML>La clave privada con el alias <strong>{0}</strong> es utilizada por los siguientes interlocutores</HTML>"},
		{"label.notinuse.key", "<HTML>La clave privada con el alias <strong>{0}</strong> no es utilizada por ningún interlocutor en la configuración</HTML>"},
		{"title", "Uso del certificado"},
	};
}
