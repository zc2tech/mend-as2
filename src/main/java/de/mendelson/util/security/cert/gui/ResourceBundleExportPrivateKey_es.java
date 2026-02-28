//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportPrivateKey_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleExportPrivateKey_es extends MecResourceBundle {

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
		{"label.exportdir.hint", "Directorio en el que se creará el archivo keystore/key"},
		{"button.browse", "Visite"},
		{"label.exportkey", "Nombre del fichero"},
		{"key.export.error.title", "Error"},
		{"keystore.contains.nokeys", "Este almacén de claves no contiene claves privadas."},
		{"label.exportdir", "Exportar directorio"},
		{"title", "Exportar clave a archivo keystore/key"},
		{"label.exportkey.hint", "Fichero de exportación que debe crearse"},
		{"button.cancel", "Cancelar"},
		{"key.exported.to.file", "La clave \"{0}\" se exportó al fichero \"{1}\"."},
		{"filechooser.key.export", "Seleccione el directorio de exportación en el servidor"},
		{"label.keypass", "contraseña"},
		{"button.ok", "Ok"},
		{"label.alias", "clave"},
		{"label.exportformat", "Formato de exportación"},
		{"key.export.error.message", "Se ha producido un error durante la exportación.\n{0}"},
		{"label.keypass.hint", "Contraseña del archivo de claves exportado"},
		{"label.exportformat.help", "<HTML><strong>Formato de exportación</strong><br><br>"
			+"Puede exportar la clave en un formato de almacén de claves (PKCS#12) o en un archivo de clave codificado con PEM.<br>"
			+"La forma más común es el formato PKCS#12, un archivo de clave codificado PEM sólo es necesario para casos de uso especiales como la configuración de proxy inverso.<br><br>"
			+"En el caso del archivo de clave PEM, la clave se guarda sin contraseña.</HTML>"},
	};
}
