//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.httpconfig.gui;

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
public class ResourceBundleDisplayHTTPConfiguration_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.info.configfile", "Este diálogo le muestra la configuración HTTP/S del lado del servidor. El servidor HTTP suministrado tiene la versión <strong>jetty {0}</strong>. Puede configurar los cifrados y protocolos en el fichero \"{1}\" del servidor. Por favor, realice los ajustes básicos en el archivo \"{2}\" o directamente a través de los ajustes del servidor. Reinicie el servidor para que los cambios surtan efecto."},
		{"no.ssl.enabled", "No se ha activado el soporte TLS en el servidor HTTP subyacente.\nPor favor, modifique el archivo de configuración {0}\nde acuerdo con la documentación y reinicie el servidor."},
		{"reading.configuration", "Leer configuración HTTP..."},
		{"tab.protocols", "Protocolos TLS"},
		{"button.ok", "Cerrar"},
		{"tab.cipher", "Cifrados TLS"},
		{"no.embedded.httpserver", "No ha iniciado el servidor HTTP subyacente.\nNo hay información disponible."},
		{"title", "Configuración HTTP del servidor"},
		{"tab.misc", "General"},
	};
}
