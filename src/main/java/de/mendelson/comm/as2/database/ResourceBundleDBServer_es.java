//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBServer_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleDBServer_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"update.successfully", "{0}: La base de datos fue modificada con éxito para la versión requerida."},
		{"dbserver.shutdown", "El servidor de la base de datos se ha apagado"},
		{"update.error.postgres", "FATAL: No es posible modificar la base de datos de la versión {0} a la versión {1}.\nPor favor, inicie pgAdmin y elimine la base de datos correspondiente."},
		{"info.jdbc", "JDBC: {0}"},
		{"info.clientdriver", "Controlador de cliente: {0}"},
		{"update.error.futureversion", "El sistema ha encontrado una versión futura de {0}. La versión de base de datos soportada con esta versión es {1} pero la base de datos encontrada tiene la versión {2}. No es posible seguir trabajando con esta base de datos ni modificarla."},
		{"upgrade.required", "Debe realizarse una actualización.\nEjecute el archivo as2upgrade.bat o as2upgrade.sh antes de iniciar el servidor."},
		{"update.progress.version.end", "Actualización de {1} a la versión {0} lista."},
		{"dbserver.running.external", "El servidor externo {0} está disponible"},
		{"update.error.hsqldb", "FATAL: No es posible modificar la base de datos de la versión {0} a la versión {1}.\nPor favor, borre todos los archivos AS2_DB_*.* correspondientes en el directorio de instalación.\nSe perderán todos los datos definidos por el usuario."},
		{"database.1", "Base de datos de configuración"},
		{"update.progress.version.start", "Iniciar actualización de {1} a la versión {0}..."},
		{"database.2", "Base de datos en tiempo de ejecución"},
		{"dbserver.running.embedded", "Servidor de BD integrado {0} en ejecución"},
		{"update.versioninfo", "Actualización automática de la base de datos: La versión de la base de datos encontrada es {0}, la versión requerida es {1}."},
		{"info.serveridentification", "Identificación del servidor: {0}"},
		{"dbserver.startup", "Iniciar servidor de BD integrado..."},
		{"update.error.mysql", "FATAL: No es posible modificar la base de datos de la versión {0} a la versión {1}.\nPor favor, inicie MySQLWorkbench y elimine la base de datos correspondiente."},
		{"update.error.oracledb", "FATAL: No es posible modificar la base de datos de la versión {0} a la versión {1}.\nInicie Oracle SQL Developer y elimine la base de datos."},
		{"update.notfound", "Para la actualización, el archivo update{0}to{1}}.sql y/o el archivo Update{0}to{1}.class deben existir en el directorio (de recursos) {2}."},
		{"info.host", "Anfitrión: {0}"},
		{"update.progress", "Actualización incremental de la base de datos iniciada..."},
		{"info.user", "Usuario: {0}"},
		{"module.name", "[BASE DE DATOS]"},
	};
}
