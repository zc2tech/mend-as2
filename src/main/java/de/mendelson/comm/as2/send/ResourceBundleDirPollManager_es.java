//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_es.java 4     6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
* @version $Revision: 4 $
*/
public class ResourceBundleDirPollManager_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"warning.ro", "[Supervisión del directorio] El archivo de salida {0} está protegido contra escritura, este archivo se ignora."},
		{"warning.notcomplete", "[Supervisión del directorio] El archivo fuente {0} aún no está completamente disponible, el archivo se ignora."},
		{"title.list.polls.stopped", "Se ha puesto fin a las siguientes actividades de seguimiento"},
		{"processing.file", "Procesar el fichero \"{0}\" para la relación \"{1}/{2}\"."},
		{"none", "Ninguno"},
		{"warning.noread", "[Supervisión de directorios] No es posible el acceso de lectura para el archivo de origen {0}, el archivo se ignora."},
		{"poll.started", "Se ha iniciado la supervisión de directorios para la relación \"{0}/{1}\". Ignorar: \"{2}\". Intervalo: {3}s"},
		{"poll.modified", "[Supervisión de directorios] Se ha modificado la configuración de interlocutor para la relación \"{0}/{1}\"."},
		{"title.list.polls.started", "Se iniciaron los siguientes procesos de supervisión"},
		{"poll.stopped.notscheduled", "[Supervisión de directorios] El sistema ha intentado detener la supervisión de directorios para \"{0}/{1}\", pero no había supervisión."},
		{"processing.file.error", "Error de procesamiento del archivo \"{0}\" para la relación \"{1}/{2}\": \"{3}\"."},
		{"title.list.polls.running", "Resumen de los directorios monitorizados:"},
		{"poll.log.polling", "[Supervisión de directorios] {0}->{1}: Comprobar directorio \"{2}\" para nuevos archivos"},
		{"manager.status.modified", "La monitorización de directorios ha cambiado monitorización de directorios, {0} directorios son monitorizados"},
		{"poll.stopped", "Se ha detenido la supervisión de directorios para la relación \"{0}/{1}\"."},
		{"poll.log.wait", "[Monitorización de directorios] {0}->{1}: Próximo proceso de sondeo en {2}s ({3})"},
		{"messagefile.deleted", "El archivo \"{0}\" fue eliminado y transferido a la cola de procesamiento del servidor."},
	};
}
