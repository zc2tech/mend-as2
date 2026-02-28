//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleAS2ServerProcessing_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"invalid.request.to", "Se ha recibido una petición no válida. No se procesará porque no hay cabecera as2-to."},
		{"local.station", "Estación local"},
		{"send.failed", "Envío fallido"},
		{"message.resend.title", "Envío manual de datos en una nueva transacción"},
		{"server.shutdown", "El usuario {0} apaga el servidor."},
		{"sync.mdn.sent", "MDN sincrónico enviado en respuesta a {0}."},
		{"unable.to.process", "Error durante el procesamiento en el servidor: {0}"},
		{"event.download.not.allowed.subject", "Descarga no permitida"},
		{"invalid.request.messageid", "Se ha recibido una solicitud no válida. No se procesará porque no hay cabecera message-id."},
		{"info.mdn.inboundfiles", "No ha sido posible determinar el mensaje AS2 de referencia para el MDN recibido.\n[MDN entrante (datos): {0}]\n[MDN entrante (cabecera): {1}]"},
		{"message.resend.oldtransaction", "Esta transacción se ha vuelto a enviar manualmente con el nuevo número de transacción [{0}]."},
		{"invalid.request.from", "Se ha recibido una petición no válida. No se procesará porque no hay cabecera as2-from."},
		{"message.resend.newtransaction", "Esta transacción es un reenvío de la transacción [{0}]."},
		{"event.download.not.allowed.body", "Un cliente ha intentado descargar un archivo, pero se le ha impedido.\nRuta de solicitud de descarga: {0}\nDirectorios permitidos: {1}\nUsuario: {2}\nHost: {3}"},
	};
}
