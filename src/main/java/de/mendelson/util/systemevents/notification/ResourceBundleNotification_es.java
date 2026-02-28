//$Header: /oftp2/de/mendelson/util/systemevents/notification/ResourceBundleNotification_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.systemevents.notification;

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
public class ResourceBundleNotification_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"test.message.debug", "\nHa fallado el envío del mensaje.\n"},
		{"notification.about.event", "Esta notificación se refiere al evento del sistema de {0}.\nUrgencia: {1}\nOrigen: {2}\nTipo: {3}\nId: {4}"},
		{"notification.summary", "Resumen de {0} eventos del sistema"},
		{"authorization.oauth2.authorizationcode", "Código de autorización"},
		{"authorization.oauth2.clientcredentials", "Credenciales del cliente"},
		{"authorization.credentials", "Usuario/contraseña"},
		{"test.message.send", "Se ha enviado un mensaje de prueba a {0}."},
		{"authorization.oauth2", "OAUTH2"},
		{"notification.summary.info", "Recibe este mensaje de resumen porque ha definido un número limitado\nde notificaciones por unidad de tiempo.\nPara obtener detalles sobre los eventos individuales, inicie el cliente\nel cliente y navegue hasta \"Eventos del sistema de archivos\".\nIntroduzca el número único del evento en la máscara de búsqueda.\ndel evento en la máscara de búsqueda."},
		{"misc.message.summary.send", "Se ha enviado un correo electrónico de notificación de resumen a {0}."},
		{"authorization.none", "NONE"},
		{"misc.message.summary.failed", "Falló el envío de un correo electrónico de notificación de resumen a {0}."},
		{"misc.message.send", "Se ha enviado un correo electrónico de notificación a {0} ({1}-{2}-{3})."},
		{"module.name", "[NOTIFICACIÓN POR CORREO ELECTRÓNICO]"},
		{"misc.message.send.failed", "Falló el envío de un correo electrónico de notificación a {0}."},
		{"do.not.reply", "Por favor, no responda a este mensaje."},
	};
}
