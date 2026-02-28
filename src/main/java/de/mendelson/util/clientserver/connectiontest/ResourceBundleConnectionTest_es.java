//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/ResourceBundleConnectionTest_es.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.connectiontest;

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
public class ResourceBundleConnectionTest_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"test.connection.proxy.auth", "La conexión utiliza el proxy {0} con autenticación (usuario \"{1}\")"},
		{"certificates.found.details", "Certificado [{0}/{1}]: {2}"},
		{"sni.extension.set", "El nombre de host para la extensión TLS SNI se ha establecido en \"{0}\"."},
		{"test.connection.proxy.noauth", "La conexión utiliza el proxy {0} sin autenticación"},
		{"wrong.protocol.hint", "O bien su interlocutor espera una conexión no segura, hay un problema de protocolo o necesita autenticación de cliente."},
		{"certificate.ca", "Certificado CA"},
		{"certificate.does.exist.local", "Este certificado ya existe en su almacén de claves TLS local, el alias es \"{0}\"."},
		{"connection.problem", "{0} no se puede contactar - puede ser un problema de infraestructura o se han introducido datos incorrectos."},
		{"service.found.failure", "Error: No se ha encontrado ningún servicio OFTP en ejecución en {0}."},
		{"certificate.selfsigned", "Autofirmado"},
		{"remote.service.identification", "Identificación del servicio del servidor remoto: \"{0}\""},
		{"test.start.plain", "Iniciar comprobación de conexión para {0}, PLAIN..."},
		{"connection.success", "La conexión con {0} se ha establecido correctamente"},
		{"wrong.protocol", "El protocolo encontrado es \"{0}\", no se trata de una conexión segura. Ha intentado conectarse a este interlocutor utilizando uno de los protocolos [{1}]. Sin embargo, su interlocutor no ofrece ninguno de estos protocolos de seguridad de línea en el puerto y dirección dados."},
		{"tag", "Prueba de conexión a {0}"},
		{"certificate.does.not.exist.local", "Este certificado aún no existe en su almacén de claves TLS local - por favor, impórtelo"},
		{"result.exception", "Se ha producido el siguiente error durante la prueba: {0}."},
		{"local.station", "Estación local"},
		{"certificates.found", "{0} Se han encontrado y descargado certificados"},
		{"protocol.information", "El protocolo utilizado se identificó como \"{0}\""},
		{"info.securityprovider", "Proveedor de seguridad TLS utilizado: {0}"},
		{"certificate.enduser", "Certificado de usuario final"},
		{"exception.occured", "Ha habido un problema durante la prueba de conexión: [{0}] {1}"},
		{"requesting.certificates", "Se descargan los certificados del servidor remoto"},
		{"test.start.ssl", "Iniciar comprobación de conexión a {0}, TLS. Tenga en cuenta que esta prueba confía en cada certificado de servidor, por lo que incluso si esta prueba tiene éxito, esto no significa que su almacén de claves TLS esté configurado correctamente."},
		{"info.protocols", "El cliente permite la negociación a través de los siguientes protocolos TLS: {0}"},
		{"timeout.set", "Establecer tiempo de espera a {0}ms"},
		{"test.connection.direct", "Se utiliza una conexión IP directa"},
		{"exception.occured.oftpservice", "No se ha podido identificar ningún servidor OFTP2 en ejecución en la dirección y puerto dados. Puede tratarse de un problema temporal, por ejemplo que el servidor OFTP2 remoto no se esté ejecutando en ese momento, pero los datos de la dirección son correctos. Se ha producido el siguiente problema: [{0}] {1}"},
		{"service.found.success", "Éxito: Se ha encontrado un servicio OFTP en ejecución en {0}."},
		{"check.for.service.oftp2", "Compruebe si se está ejecutando el servicio OFTP2..."},
	};
}
