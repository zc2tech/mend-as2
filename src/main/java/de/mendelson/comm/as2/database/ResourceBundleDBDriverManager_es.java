//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBDriverManager_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.database;

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
public class ResourceBundleDBDriverManager_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"database.creation.failed.1", "Se ha producido un error al crear la base de datos de configuración"},
		{"database.creation.failed.2", "Se ha producido un error al crear la base de datos en tiempo de ejecución"},
		{"creating.database.1", "Crear base de datos de configuración"},
		{"creating.database.2", "Crear base de datos en tiempo de ejecución"},
		{"creating.database.details", "Host: {0}, Puerto: {1}, Usuario: {2}, Nombre BD: {3}"},
		{"database.creation.success.1", "La base de datos de configuración se ha creado correctamente"},
		{"database.creation.success.2", "La base de datos en tiempo de ejecución se ha creado correctamente"},
		{"module.name", "[BASE DE DATOS]"},
	};
}
