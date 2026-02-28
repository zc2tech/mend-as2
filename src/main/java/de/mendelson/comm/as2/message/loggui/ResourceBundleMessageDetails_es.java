//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_es.java 3     17/01/25 10:06 Heller $
package de.mendelson.comm.as2.message.loggui;

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
* @version $Revision: 3 $
*/
public class ResourceBundleMessageDetails_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transactionstate.error.connectionrefused", "<HTML>Ha intentado ponerse en contacto con el sistema asociado. O bien ha fallado o su interlocutor no ha respondido con una confirmación dentro del tiempo definido.</HTML>"},
		{"header.timestamp", "fecha"},
		{"transactiondetails.outbound.insecure", "Se trata de una conexión saliente no segura, está enviando datos al interlocutor \"{0}\"."},
		{"transactiondetails.outbound.sync", " Recibirá la confirmación directamente como respuesta en el canal de retorno de la conexión saliente (MDN síncrono)."},
		{"header.useragent", "Servidor AS2"},
		{"transactionstate.error.authentication-failed", "<HTML>El destinatario del mensaje no ha podido comprobar correctamente la firma del remitente en los datos. Normalmente se trata de un problema de configuración, ya que el remitente y el destinatario deben utilizar el mismo certificado. Consulta también los detalles del MDN en el registro, ya que pueden contener más información.</HTML>"},
		{"title", "Detalles del mensaje"},
		{"transactionstate.error.messagecreation.details", "<HTML>El sistema no pudo generar la estructura de mensajes requerida debido a un problema de su parte. Esto no tiene nada que ver con su sistema asociado, no se estableció ninguna conexión.</HTML>"},
		{"message.raw.decrypted", "Datos de transmisión (sin cifrar)"},
		{"transactionstate.error.asyncmdnsend", "<HTML>Se ha recibido y procesado correctamente un mensaje con una solicitud MDN asíncrona, pero su sistema no ha podido devolver el MDN asíncrono o éste no ha sido aceptado por el sistema asociado.</HTML>"},
		{"transactionstate.error.connectionrefused.details", "<HTML>Podría tratarse de un problema de infraestructura, de que el sistema de su socio no funciona o de que ha introducido una URL de recepción incorrecta en la configuración. Si los datos se han transmitido y su socio no los ha confirmado, es posible que haya establecido un plazo de confirmación demasiado corto.</HTML>"},
		{"transactionstate.ok.receive", "<HTML>El interlocutor \"{1}\" ha recibido correctamente el mensaje {0}. Se ha enviado la correspondiente confirmación al interlocutor.</HTML>"},
		{"title.cem", "Detalles del mensaje de intercambio de certificados (CEM)"},
		{"header.encryption", "Cifrado"},
		{"header.messageid", "Número de referencia"},
		{"transactionstate.error.unexpected-processing-error", "<HTML>Este es un mensaje de error muy genérico. Por un motivo desconocido, el destinatario no ha podido procesar el mensaje.</HTML>"},
		{"transactionstate.error.in", "<HTML>Ha recibido correctamente el mensaje {0} de su interlocutor \"{1}\" - pero su sistema no ha podido procesarlo y ha respondido con el error [{2}].</HTML>"},
		{"transactionstate.ok.details", "<HTML>Los datos se han transferido y la transacción se ha completado con éxito.</HTML>"},
		{"message.payload.multiple", "Datos del usuario ({0})"},
		{"transactionstate.ok.send", "<HTML>El mensaje {0} se ha enviado correctamente al interlocutor \"{1}\" - éste ha enviado la confirmación correspondiente.</HTML>"},
		{"transactiondetails.outbound.secure", "Se trata de una conexión segura saliente, está enviando datos al interlocutor \"{0}\"."},
		{"transactionstate.error.unknown", "Se ha producido un error desconocido."},
		{"transactiondetails.inbound.async", " La confirmación se envía estableciendo una nueva conexión con el interlocutor (MDN asíncrono)."},
		{"transactionstate.error.decryption-failed", "<HTML>El destinatario del mensaje no ha podido descifrar el mensaje. Esto suele ser un problema de configuración, ¿está utilizando el remitente el certificado correcto para el cifrado?</HTML>"},
		{"message.header", "Datos de cabecera"},
		{"transactionstate.error.messagecreation", "<HTML>Ha ocurrido un problema durante la generación de un mensaje AS2 saliente.</HTML>"},
		{"header.senderhost", "Transmisor"},
		{"transactiondetails.inbound.secure", "Se trata de una conexión segura entrante, está recibiendo datos del interlocutor \"{0}\"."},
		{"transactionstate.error.insufficient-message-security", "<HTML>El destinatario del mensaje esperaba un mayor nivel de seguridad para los datos recibidos (por ejemplo, datos cifrados en lugar de datos sin cifrar).</HTML>"},
		{"transactiondetails.outbound.async", " Para confirmar, su interlocutor establece una nueva conexión con usted (MDN asíncrona)."},
		{"transactionstate.error.asyncmdnsend.details", "<HTML>El emisor del mensaje AS2 transmite la URL a la que debe devolver el MDN - o bien este sistema no es accesible (¿problema de infraestructura o el sistema asociado está caído?) o bien el sistema asociado no ha aceptado el MDN asíncrono y ha respondido con un HTTP 400.</HTML>"},
		{"transactionstate.pending", "Esta transacción se encuentra en estado de espera."},
		{"transactionstate.error.decompression-failed", "<HTML>El destinatario del mensaje no ha podido descomprimir el mensaje recibido.</HTML>"},
		{"button.ok", "Ok"},
		{"transactionstate.error.unknown-trading-partner", "<HTML>Usted y su interlocutor tienen identificadores AS2 diferentes para los dos interlocutores de la transmisión en la configuración. Se han utilizado los siguientes identificadores: \"{0}\" (emisor del mensaje), \"{1}\" (destinatario del mensaje)</HTML>"},
		{"transactiondetails.inbound.insecure", "Se trata de una conexión entrante no segura, está recibiendo datos del interlocutor \"{0}\"."},
		{"message.payload", "Datos del usuario"},
		{"transactiondetails.inbound.sync", " La confirmación se envía directamente como respuesta en el canal de retorno de la conexión entrante (MDN síncrono)."},
		{"transactionstate.error.out", "<HTML>Ha enviado correctamente el mensaje {0} a su interlocutor \"{1}\" - pero éste no ha podido procesarlo y le ha respondido con el error [{2}].</HTML>"},
		{"tab.log", "Registro de esta instancia de mensaje"},
		{"header.signature", "Firma digital"},
	};
}
