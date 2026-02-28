//$Header: /oftp2/de/mendelson/util/security/crl/ResourceBundleCRL_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.crl;

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
public class ResourceBundleCRL_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"malformed.crl.url", "URL CRL incorrecta ({0})"},
		{"failed.revoked", "El certificado está revocado: {0}"},
		{"bad.crl", "No se pueden procesar los datos CRL descargados"},
		{"no.crl.entry", "El certificado no tiene una extensión que haga referencia a una URL CRL"},
		{"self.signed.skipped", "Autofirmado - verificación omitida"},
		{"cert.read.error", "No se puede leer el certificado para la URL de la lista de revocación"},
		{"crl.success", "Ok - El certificado no está revocado."},
		{"no.https", "Problema de conexión con URI {0} - HTTPS no está soportado"},
		{"module.name", "[lista negra]"},
		{"error.url.retrieve", "La URL de la lista de revocación no se puede leer del certificado"},
		{"download.failed.from", "La descarga de la lista negra ha fallado ({0})"},
	};
}
