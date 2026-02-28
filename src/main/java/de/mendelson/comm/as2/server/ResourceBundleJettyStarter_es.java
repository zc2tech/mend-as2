//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleJettyStarter_es.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundleJettyStarter_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"httpserver.startup.problem", "Problema al inicio ({0})"},
		{"userconfiguration.readerror", "Problema leyendo la configuración de usuario de {0}: {1} ... Ignorar la configuración de usuario e iniciar el servidor web utilizando los valores por defecto definidos."},
		{"httpserver.running", "Servidor HTTP integrado en ejecución ({0})"},
		{"deployment.failed", "NO se ha proporcionado [{0}]: {1}"},
		{"userconfiguration.setvar", "Establecer valor definido por el usuario [{0}] a [{1}]"},
		{"userconfiguration.reading", "Leer configuración definida por el usuario de {0}"},
		{"httpserver.willstart", "Se inicia el servidor HTTP integrado"},
		{"tls.keystore.reloaded", "Se han registrado cambios en el almacén de claves TLS y se han actualizado los datos del almacén de claves del servidor HTTP."},
		{"module.name", "[JETTY]"},
		{"httpserver.stopped", "Servidor HTTP integrado detenido"},
		{"deployment.success", "[{0}] se ha desplegado correctamente"},
		{"listener.started", "Esperar conexiones entrantes {0}"},
	};
}
