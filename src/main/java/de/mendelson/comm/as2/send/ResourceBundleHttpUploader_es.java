//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleHttpUploader_es.java 4     6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
* @version $Revision: 4 $
*/
public class ResourceBundleHttpUploader_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"strict.hostname.check.skipped.selfsigned", "TLS: Se ha omitido la comprobación estricta del nombre de host - el servidor remoto utiliza un certificado autofirmado."},
		{"sending.msg.sync", "Enviar mensaje AS2 a {0}, esperar MDN síncrono para acuse de recibo."},
		{"sending.cem.sync", "Enviar mensaje CEM a {0}, esperar MDN síncrono para confirmar recepción."},
		{"answer.no.sync.empty", "El acuse de recibo síncrono recibido está vacío. Probablemente ha habido un problema al procesar los mensajes AS2 por parte de su interlocutor; póngase en contacto con él."},
		{"error.httpupload", "Transmisión fallida, el servidor AS2 remoto informa \"{0}\"."},
		{"sending.cem.async", "Enviar mensaje CEM a {0}, esperar MDN asíncrono para acuse de recibo en {1}."},
		{"answer.no.sync.mdn", "El acuse de recibo síncrono recibido no tiene el formato correcto. Dado que los problemas de estructura de MDN son inusuales, podría ser que no se tratara de una respuesta del sistema AS2 al que intentaba dirigirse, sino tal vez de la respuesta de un proxy o de la respuesta de un sitio web estándar. Faltan los siguientes valores de cabecera HTTP [{0}].\nLos datos recibidos comienzan con las siguientes estructuras:\n{1}"},
		{"hint.SSLPeerUnverifiedException", "Nota:\nEste problema se produjo durante el handshake TLS. Por lo tanto, el sistema no ha podido establecer una conexión segura con su interlocutor, el problema no tiene nada que ver con el protocolo AS2.\nPor favor, compruebe lo siguiente:\n*¿Ha importado todos los certificados de su interlocutor a su almacén de claves TLS (para TLS, incl. certificados intermedios/root)?\n*¿Ha importado su socio todos los certificados suyos (para TLS, incl. certificados intermedios/root)?"},
		{"hint.ConnectTimeoutException", "Nota:\nEsto suele ser un problema de infraestructura que no tiene nada que ver con el protocolo AS2. No es posible establecer una conexión saliente con su interlocutor.\nPor favor, compruebe lo siguiente para resolver el problema:\n*¿Tiene una conexión a Internet activa?\n*Compruebe si ha introducido correctamente la URL de recepción de su interlocutor en la administración de interlocutores.\n*Por favor, póngase en contacto con su socio, ¿tal vez su sistema AS2 no está disponible?"},
		{"trust.all.server.certificates", "La conexión TLS saliente confiará en todos los certificados del servidor remoto si los certificados raíz e intermedio están disponibles."},
		{"sending.mdn.async", "Enviar acuse de recibo asíncrono (MDN) a {0}."},
		{"using.proxy", "Utilice el proxy {0}:{1}."},
		{"returncode.ok", "Mensaje enviado con éxito (HTTP {0}); {1} transmitido en {2} [{3} KB/s]."},
		{"connection.tls.info", "Conexión TLS saliente establecida [{0}, {1}]"},
		{"error.http502", "Problema de conexión, no se han podido transferir datos. (HTTP 502 - PUERTA DE ENLACE INCORRECTA)"},
		{"returncode.accepted", "Mensaje enviado con éxito (HTTP {0}); {1} transmitido en {2} [{3} KB/s]."},
		{"error.http503", "Problema de conexión, no se han podido transferir datos. (HTTP 503 - SERVICIO NO DISPONIBLE)"},
		{"strict.hostname.check", "Para la conexión TLS saliente, se realiza una comprobación estricta del nombre de host con respecto al certificado del servidor."},
		{"error.http504", "Problema de conexión, no se han podido transferir datos. (HTTP 504 - TIEMPO DE ESPERA DE LA PASARELA)"},
		{"using.proxy.auth", "Utilizar proxy {0}:{1} (autenticación como {2})."},
		{"error.noconnection", "Problema de conexión, no se han podido transferir datos."},
		{"hint.httpcode.signals.problem", "Nota:\nSe ha establecido una conexión con su host asociado - un servidor web se está ejecutando allí.\nEl servidor remoto está señalando que algo está mal con la ruta de petición o el puerto y está devolviendo el código HTTP {0}.\nPor favor, utilice un motor de búsqueda de Internet si necesita más información sobre este código HTTP."},
		{"hint.SSLException", "Nota:\nNormalmente se trata de un problema de negociación a nivel de protocolo. Su interlocutor ha rechazado su conexión.\nO bien su interlocutor espera una conexión segura (HTTPS) y usted quería establecer una conexión no segura o viceversa.\nTambién es posible que su socio requiera una versión TLS diferente o un algoritmo de encriptación diferente al que usted ofrece."},
		{"sending.msg.async", "Enviar mensaje AS2 a {0}, esperar MDN asíncrono para acuse de recibo en {1}."},
	};
}
