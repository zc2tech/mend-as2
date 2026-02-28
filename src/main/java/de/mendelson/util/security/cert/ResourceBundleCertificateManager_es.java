//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleCertificateManager_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleCertificateManager_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"certificate.not.found.fingerprint.withinfo", "El certificado con la huella SHA-1 \"{0}\" no existe en el sistema. ({1})"},
		{"keystore.PKCS12", "Almacén de claves de cifrado/firma"},
		{"event.certificate.deleted.body", "El siguiente certificado ha sido eliminado del sistema:\n\n{0}"},
		{"certificate.not.found.subjectdn.withinfo", "El certificado con el subjectDN \"{0}\" no existe en el sistema. ({1})"},
		{"certificate.not.found.ski.withinfo", "El certificado con el identificador de clave de asunto \"{0}\" no existe en el sistema. ({1})"},
		{"keystore.PKCS11", "HSM/PKCS#11"},
		{"alias.hasno.key", "El archivo de certificado no contiene una clave con el alias \"{0}\"."},
		{"keystore.reloaded", "({0}) El archivo de certificados ha sido recargado, todas las claves y certificados han sido actualizados."},
		{"event.certificate.added.subject", "{0}: Se ha añadido un nuevo certificado (alias \"{1}\")"},
		{"event.certificate.modified.subject", "{0}: Se ha modificado el alias de un certificado"},
		{"access.problem", "Problemas de acceso a {0}"},
		{"keystore.read.failure", "El sistema no puede leer los certificados/claves almacenados. Mensaje de error: \"{0}\". Asegúrese de que ha establecido la contraseña correcta del almacén de claves."},
		{"event.certificate.deleted.subject", "{0}: Se ha eliminado un certificado (alias \"{1}\")"},
		{"alias.notfound", "El archivo de certificado no contiene un certificado con el alias \"{0}\"."},
		{"certificate.not.found.fingerprint", "El certificado con la huella SHA-1 \"{0}\" no existe."},
		{"event.certificate.added.body", "Se ha añadido un nuevo certificado al sistema con los siguientes datos:\n\n{0}"},
		{"event.certificate.modified.body", "El alias de certificado \"{0}\" se cambiaría por \"{1}\".\n\n\nEstos son los datos del certificado:\n\n{2}"},
		{"keystore.JKS", "Depósito de claves TLS"},
		{"alias.hasno.privatekey", "El archivo de certificado no contiene una clave privada con el alias \"{0}\"."},
		{"certificate.not.found.issuerserial.withinfo", "Se requiere el certificado con el emisor \"{0}\" y el número de serie \"{1}\", pero éste no existe en el sistema ({2})"},
	};
}
