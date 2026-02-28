//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeystore_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleExportKeystore_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Directorio de exportación</strong><br><br>"
			+"Introduzca el directorio de exportación al que se exportará el almacén de claves.<br>"
			+"Por razones de seguridad, las claves no se transfieren al cliente,<br>"
			+"para que sólo sea posible guardar en el lado del servidor.<br>"
			+"El sistema crea un archivo de memoria en este directorio que contiene un sello de fecha.</HTML>"},
		{"keystore.export.error.title", "Error"},
		{"label.exportdir.hint", "Directorio en el que se crea el archivo keystore"},
		{"button.browse", "Visite"},
		{"keystore.export.error.message", "Hubo un problema al exportar:\n{0}"},
		{"label.exportdir", "Exportar directorio"},
		{"title", "Exportar todas las entradas a un archivo keystore"},
		{"button.cancel", "Cancelar"},
		{"filechooser.key.export", "Seleccione el directorio de exportación del servidor"},
		{"label.keypass", "Contraseña"},
		{"keystore.export.success.title", "Éxito"},
		{"button.ok", "Ok"},
		{"label.keypass.help", "<HTML><strong>Contraseña del almacén de claves exportado</strong><br><br>"
			+"Es la contraseña con la que se protege el almacén de claves exportado en el lado del servidor.<br>"
			+"Introduzca \"test\" si desea que este almacén de claves se importe automáticamente en el producto mendelson más adelante.</HTML>"},
		{"keystore.exported.to.file", "El archivo keystore fue escrito en \"{0}\"."},
		{"label.keypass.hint", "Contraseña del almacén de claves exportado"},
	};
}
