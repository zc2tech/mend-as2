//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2Server_es.java 3     18/02/25 14:39 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleAS2Server_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"server.willstart", "{0} comienza"},
		{"server.started.issue", "Advertencia: Se ha detectado 1 problema de configuración al iniciar el servidor."},
		{"server.hello", "Esto es {0}"},
		{"server.shutdown", "{0} se apaga."},
		{"bind.exception", "{0}\nHa definido un puerto que actualmente está siendo utilizado por otro proceso en su sistema.\nPuede ser el puerto cliente-servidor o el puerto HTTP/S que haya definido en la configuración HTTP.\nPor favor, cambie su configuración o detenga el otro proceso antes de utilizar el {1}."},
		{"server.already.running", "Parece que ya se está ejecutando una instancia de mendelson AS2.\nSin embargo, también podría ser que una instancia anterior no se hubiera terminado correctamente. Si está seguro de que no hay ninguna otra instancia en ejecución,\npor favor, borre el archivo de bloqueo \"{0}\"\n(fecha de inicio {1}) y reinicie el servidor."},
		{"server.start.details", "{0} Parámetro:\n\nInicia el servidor HTTP integrado: {1}\nPermitir conexiones cliente-servidor desde otros hosts: {2}\nMemoria Heap: {3}\nVersión de Java: {4}\nUsuario del sistema: {5}\nIdentificación del sistema: {6}"},
		{"server.hello.licenseexpire.single", "La licencia caduca en {0} días ({1}). Debe renovar la licencia a través del soporte de mendelson (service@mendelson.de) si desea seguir utilizándola después."},
		{"server.hello.licenseexpire", "La licencia caduca en {0} días ({1}). Debe renovar la licencia a través del soporte de mendelson (service@mendelson.de) si desea seguir utilizándola pasado ese tiempo."},
		{"server.started", "mendelson AS2 2024 build 613 iniciado en {0} ms."},
		{"server.startup.failed", "Hubo un problema al iniciar el servidor - el inicio fue cancelado"},
		{"server.started.issues", "Advertencia: Se han detectado {0} problemas de configuración al iniciar el servidor."},
		{"fatal.limited.strength", "Esta Java VM no soporta la longitud de clave requerida. Por favor, instale los archivos \"Unlimited jurisdiction key strength policy\" antes de iniciar el servidor mendelson AS2."},
		{"server.nohttp", "No se ha iniciado el servidor HTTP integrado."},
                {"server.started.usedlibs", "Librerías usadas" },
	};
}
