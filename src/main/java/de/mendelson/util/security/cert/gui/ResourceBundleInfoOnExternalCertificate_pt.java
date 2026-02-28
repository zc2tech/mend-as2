//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleInfoOnExternalCertificate_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"certinfo.certfile", "Ficheiro de certificado: {0}"},
		{"button.cancel", "Fechar"},
		{"title.multiple", "Informações sobre certificados externos"},
		{"button.ok", "Importar >>"},
		{"certificate.exists", "Este certificado já existe na gestão de certificados, o alias é \"{0}\""},
		{"certinfo.index", "Certificado {0} de {1}"},
		{"certificate.doesnot.exist", "Este certificado ainda não existe na gestão de certificados"},
		{"no.certificate", "O certificado não foi reconhecido"},
		{"title.single", "Informações sobre um certificado externo"},
	};
}
