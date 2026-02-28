//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSR_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.csr;

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
public class ResourceBundleCSR_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"csr.generation.failure.message", "{0}"},
		{"cancel", "Cancelar"},
		{"label.selectcsrfile", "Seleccione el archivo para guardar el CSR"},
		{"csr.message.storequestion.renew", "¿Desea renovar la llave en la CA mendelson\no guardar la solicitud en un archivo?"},
		{"csr.option.1.renew", "Renovar en mendelson CA"},
		{"csr.title.renew", "Renovar certificado: Solicitud de firma de certificado (CSR)"},
		{"csrresponse.import.failure.title", "Problema al parchear la llave"},
		{"ca.connection.problem", "HTTP {0}: La CA mendelson no está disponible en este momento. Vuelva a intentarlo más tarde."},
		{"csr.message.storequestion", "¿Desea que la clave sea autenticada por la CA mendelson\no guardar la solicitud en un archivo?"},
		{"csr.generation.success.title", "El CSR se ha creado correctamente"},
		{"csr.generation.failure.title", "Errores durante la creación de CSR"},
		{"csr.generation.success.message", "La solicitud de autenticación generada se guardó en el archivo\narchivo \"{0}\".\nPor favor, envíe estos datos a su CA.\nRecomendamos la CA mendelson (http://ca.mendelson-e-c.com)."},
		{"label.selectcsrrepsonsefile", "Seleccione el archivo de respuesta de la CA"},
		{"csrresponse.import.success.title", "Éxito"},
		{"csrresponse.import.failure.message", "{0}"},
		{"csr.title", "Autenticar certificado: Solicitud de firma de certificado (CSR)"},
		{"csr.option.2", "Guardar en archivo"},
		{"csr.option.1", "Notarización en mendelson CA"},
		{"csrresponse.import.success.message", "La clave se ha parcheado correctamente con la respuesta de la CA."},
	};
}
