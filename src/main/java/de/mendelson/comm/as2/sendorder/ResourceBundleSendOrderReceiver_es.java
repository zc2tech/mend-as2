//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_es.java 3     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.sendorder;

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
public class ResourceBundleSendOrderReceiver_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"async.mdn.wait", "Esperar MDN asíncrono hasta {0}."},
		{"as2.send.disabled", "** El número de conexiones salientes paralelas está configurado a 0 - el sistema no enviará mensajes MDN ni AS2. Por favor, cambie esta configuración en la configuración del servidor si desea enviar **."},
		{"max.retry.reached", "Se ha alcanzado el número máximo de reintentos ({0}), la transacción finaliza."},
		{"outbound.connection.prepare.mdn", "Preparar conexión MDN saliente a \"{0}\", conexiones activas: {1}/{2}."},
		{"outbound.connection.prepare.message", "Preparar conexión de mensaje AS2 saliente a \"{0}\", conexiones activas: {1}/{2}."},
		{"send.connectionsstillopen", "Ha reducido el número de conexiones salientes a {0}, pero actualmente sigue habiendo {1} conexiones salientes."},
		{"retry", "Intente una nueva transmisión después de {0}s, repita {1}/{2}."},
	};
}
