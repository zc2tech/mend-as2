//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleInfoOnExternalCertificate_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"certinfo.certfile", "Archivo de certificado: {0}"},
		{"button.cancel", "Cerrar"},
		{"title.multiple", "Información sobre certificados externos"},
		{"button.ok", "Importar >>"},
		{"certificate.exists", "Este certificado ya existe en la gestión de certificados, el alias es \"{0}\"."},
		{"certinfo.index", "Certificado {0} de {1}"},
		{"certificate.doesnot.exist", "Este certificado aún no existe en la gestión de certificados"},
		{"no.certificate", "El certificado no ha sido reconocido"},
		{"title.single", "Información sobre un certificado externo"},
	};
}
