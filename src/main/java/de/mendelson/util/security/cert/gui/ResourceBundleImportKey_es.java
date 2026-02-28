//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKey_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleImportKey_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"key.import.error.entry.exists", "Importación no posible: ya existe una entrada para esta huella digital con el alias {0}."},
		{"button.browse", "Visite"},
		{"key.import.success.title", "Éxito"},
		{"key.import.error.message", "Se ha producido un error durante el proceso de importación.\n{0}"},
		{"keystore.contains.nokeys", "Este archivo de claves no contiene claves privadas."},
		{"multiple.keys.title", "Varias claves incluidas"},
		{"title", "Importar clave desde archivo de claves (PKCS#12, formato JKS)"},
		{"button.cancel", "Demolición"},
		{"key.import.error.title", "Error"},
		{"enter.keypassword", "Por favor, introduzca la contraseña clave para \"{0}\""},
		{"label.keypass", "contraseña"},
		{"button.ok", "Ok"},
		{"multiple.keys.message", "Seleccione la clave que desea importar"},
		{"label.importkey", "Nombre del fichero"},
		{"filechooser.key.import", "Seleccione un archivo de clave electrónica PKCS#12/JKS para la importación"},
		{"key.import.success.message", "La clave se ha importado correctamente."},
		{"label.importkey.hint", "Fichero de claves a importar (PKCS#12, JKS)"},
		{"label.keypass.hint", "Contraseña del almacén de claves (PKCS#12, JKS)"},
	};
}
