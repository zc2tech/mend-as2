//$Header: /oftp2/de/mendelson/util/clientserver/ResourceBundleGUIClient_es.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver;

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
public class ResourceBundleGUIClient_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error", "Problema: {0}"},
		{"logout.from.server", "Se ha realizado una desconexión del servidor"},
		{"password.required", "Error de inicio de sesión, se requiere una contraseña para el usuario {0}."},
		{"login.failure", "Error al iniciar sesión como usuario \"{0}"},
		{"client.received.unprocessed.message", "El servidor ha enviado un mensaje que no ha sido procesado por el cliente: {0}"},
		{"connectionrefused.message", "{0}: No hay conexión posible. Por favor, asegúrese de que el servidor está funcionando."},
		{"login.failed.client.incompatible.title", "Login rechazado"},
		{"connection.closed.title", "Desconexión local"},
		{"connection.closed.message", "La conexión local cliente-servidor se ha desconectado del servidor"},
		{"connection.closed", "La conexión local cliente-servidor se ha desconectado del servidor"},
		{"connection.success", "Cliente conectado a {0}"},
		{"login.failed.client.incompatible.message", "El servidor informa de que este cliente no tiene la versión correcta.\nPor favor, utilice el cliente que coincida con el servidor."},
		{"login.success", "Conectado como usuario \"{0}\""},
		{"connectionrefused.title", "Problema de conexión"},
	};
}
