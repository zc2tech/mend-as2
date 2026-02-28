//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult_es.java 2     9/12/24 15:50 Hell $
package de.mendelson.util.clientserver.connectiontest.gui;

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
public class ResourceBundleDialogConnectionTestResult_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"description.2", "El sistema ha realizado una prueba de conexión a la dirección {0}, puerto {1}. El siguiente resultado muestra si el establecimiento de la conexión se ha realizado correctamente y si se está ejecutando un servidor HTTP en esta dirección. Aunque la prueba se haya realizado correctamente, no se sabe con certeza si se trata de un servidor HTTP normal o de un servidor AS2. Si se debe utilizar una conexión TLS (HTTPS) y esto ha sido posible con éxito, puede descargar los certificados de su interlocutor e importarlos a su almacén de claves."},
		{"header.plain", "{0} [Conexión no segura]"},
		{"description.3", "El sistema ha realizado una prueba de conexión a la dirección {0}, puerto {1}. El siguiente resultado muestra si el establecimiento de la conexión se ha realizado correctamente y si se está ejecutando un servidor HTTP en esta dirección. Aunque la prueba se haya realizado correctamente, no se sabe con certeza si se trata de un servidor HTTP normal o de un servidor AS4. Si se debe utilizar una conexión TLS (HTTPS) y esto ha sido posible con éxito, puede descargar los certificados de su interlocutor e importarlos a su almacén de claves."},
		{"header.ssl", "{0} [Conexión TLS]"},
		{"title", "Resultado de la prueba de conexión"},
		{"button.viewcert", "<HTML>Importar certificado(s)&nbsp;</HTML>"},
		{"AVAILABLE", "[PRESENTE]"},
		{"label.connection.established", "Se ha establecido la conexión IP simple"},
		{"label.certificates.available.local", "Los certificados asociados (TLS) están disponibles en su sistema"},
		{"no.certificate.plain", "No disponible (conexión no segura)"},
		{"FAILED", "[ERROR]"},
		{"label.running.oftpservice", "Se ha encontrado un servicio OFTP en ejecución"},
		{"button.close", "Cerrar"},
		{"description.1", "El sistema ha realizado una prueba de conexión a la dirección {0}, puerto {1}. El siguiente resultado muestra si la conexión se ha realizado correctamente y si se está ejecutando un servidor OFTP2 en esta dirección. Si se ha utilizado una conexión TLS y ésta ha sido satisfactoria, puede descargar los certificados de su interlocutor e importarlos a su almacén de claves."},
		{"OK", "[SUCCESSFUL]"},
		{"NOT_AVAILABLE", "[NO DISPONIBLE]"},
		{"used.cipher", "Para la prueba se utilizó el siguiente algoritmo de cifrado: \"{0}\""},
	};
}
