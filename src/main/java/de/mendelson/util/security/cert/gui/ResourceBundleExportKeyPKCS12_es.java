//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleExportKeyPKCS12_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.exportdir.help", "<HTML><strong>Directorio de exportación</strong><br><br>"
			+"Introduzca aquí el directorio de exportación al que debe exportarse la clave privada.<br>"
			+"Por razones de seguridad, la clave no se transfiere al cliente, por lo que sólo puede guardarse en el servidor.<br><br>"
			+"El sistema creará un archivo de memoria en este directorio que contiene un sello de fecha.</HTML>"},
		{"key.export.success.title", "Éxito"},
		{"label.exportdir.hint", "Directorio en el que se creará el almacén de claves (PKCS#12)"},
		{"button.browse", "Visite"},
		{"label.exportkey", "Nombre del fichero"},
		{"key.export.error.title", "Error"},
		{"keystore.contains.nokeys", "Este almacén de claves no contiene claves privadas."},
		{"label.exportdir", "Exportar directorio"},
		{"title", "Exportar clave a almacén de claves (formato PKCS#12)"},
		{"label.exportkey.hint", "Exportar el archivo keystore que se va a crear (PKCS#12)"},
		{"button.cancel", "Cancelar"},
		{"key.exported.to.file", "La clave \"{0}\" se exportó al archivo PKCS#12 \"{1}\"."},
		{"filechooser.key.export", "Seleccione el directorio de exportación en el servidor"},
		{"label.keypass", "contraseña"},
		{"button.ok", "Ok"},
		{"label.alias", "clave"},
		{"key.export.error.message", "Se ha producido un error durante la exportación.\n{0}"},
		{"label.keypass.hint", "Contraseña para el almacén de claves exportado"},
	};
}
