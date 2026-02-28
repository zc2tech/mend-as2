//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleCSRUtil_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"response.verification.failed", "No se ha podido verificar la cadena de confianza de la respuesta CSR: {0}"},
		{"verification.failed", "La verificación de la solicitud de firma de certificado (CSR) creada ha fallado."},
		{"missing.cert.in.trustchain", "Para esta operación faltan los certificados de la cadena de autenticación del sistema (certificado raíz y certificado intermedio).\nRecibirá estos certificados de su CA.\nPor favor, importe primero el certificado con los datos de la clave (emisor)\n{0}."},
		{"no.certificates.in.reply", "No se pudo parchear la clave, no se encontraron certificados en la respuesta de la CA."},
		{"response.chain.incomplete", "La cadena de confianza de la respuesta CSR está incompleta."},
		{"response.public.key.does.not.match", "Esta respuesta de la CA no coincide con esta clave."},
	};
}
