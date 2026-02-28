//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleCertificateReference_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleCertificateReference_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.info.certificate", "<HTML>O certificado com o pseudónimo <strong>{0}</strong> é utilizado pelos seguintes parceiros</HTML>"},
		{"label.notinuse.certificate", "<HTML>O certificado com o pseudónimo <strong>{0}</strong> não é utilizado por nenhum parceiro na configuração</HTML>"},
		{"button.ok", "Ok"},
		{"label.info.key", "<HTML>A chave privada com o pseudónimo <strong>{0}</strong> é utilizada pelos seguintes parceiros</HTML>"},
		{"label.notinuse.key", "<HTML>A chave privada com o pseudónimo <strong>{0}</strong> não é utilizada por nenhum parceiro na configuração</HTML>"},
		{"title", "Utilização do certificado"},
	};
}
