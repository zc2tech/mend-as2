//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleKeystoreStorage_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleKeystoreStorage_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error.delete.notloaded", "No se ha podido eliminar la entrada, aún no se ha cargado el almacén de claves subyacente."},
		{"error.nodata", "No se ha podido leer el almacén de claves: No hay datos disponibles"},
		{"moved.keystore.to.db.title", "Importación de un archivo keystore ({0})"},
		{"error.readaccess", "No se ha podido leer el almacén de claves: No es posible el acceso de lectura a \"{0}\"."},
		{"moved.keystore.reason.commandline", "La importación se activó mediante un parámetro de línea de comandos al iniciar el servidor."},
		{"error.save.notloaded", "El almacén de claves no se puede guardar, aún no se ha cargado."},
		{"moved.keystore.to.db", "Importar los datos del almacén de claves de \"{0}\" al sistema - el uso previsto es {1}. Se han eliminado todas las claves/certificados existentes."},
		{"error.save", "No se han podido guardar los datos del almacén de claves."},
		{"error.empty", "No se ha podido leer el almacén de claves: Los datos del almacén de claves deben ser mayores que 0."},
		{"keystore.read.failure", "El sistema no ha podido leer los certificados subyacentes. Mensaje de error: \"{0}\". Compruebe si está utilizando la contraseña correcta para el almacén de claves."},
		{"moved.keystore.reason.initial", "La importación se ha realizado porque actualmente no hay memoria interna de claves del sistema. Se trata de un proceso inicial."},
		{"error.filexists", "No se ha podido leer el almacén de claves: El archivo keystore \"{0}\" no existe."},
		{"error.notafile", "No se ha podido leer el almacén de claves: El archivo keystore \"{0}\" no es un archivo."},
	};
}
