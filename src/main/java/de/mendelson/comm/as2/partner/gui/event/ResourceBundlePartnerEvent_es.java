//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent_es.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui.event;

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
public class ResourceBundlePartnerEvent_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"process.executeshell", "Ejecución de un comando shell"},
		{"title.configuration.shell", "Configuración del comando Shell [Socio {0}, {1}]"},
		{"shell.hint.samples", "<HTML><strong>Ejemplos</strong><br>"
			+"Windows: <i>cmd /c mover \"$'{'nombrearchivo}\" \"c:\\directorio de destino\"</i>.<br>"
			+"Linux: <i>mv \"$'{'nombrearchivo}\" \"~/directorio de destino/\"</i></HTML>"},
		{"tab.newprocess", "Procesos disponibles para el tratamiento posterior"},
		{"label.shell.command", "({0}):"},
		{"shell.hint.replacement.3", "<HTML>Las siguientes variables se sustituyen por valores del sistema en este comando antes de que se ejecute:<br>"
			+"<i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
		{"shell.hint.replacement.2", "<HTML>Las siguientes variables se sustituyen por valores del sistema en este comando antes de que se ejecute:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"shell.hint.replacement.1", "<HTML>Las siguientes variables se sustituyen por valores del sistema en este comando antes de que se ejecute:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"process.movetodirectory.description", "Mover los datos a otro directorio"},
		{"process.executeshell.description", "Ejecuta un comando shell o un script para post-procesar los datos."},
		{"title.configuration.movetodir", "Mover mensajes al directorio [Socio {0}, {1}]"},
		{"label.movetodir.remotedir.select", "Seleccione el directorio de destino en el servidor"},
		{"label.movetopartner.info", "<HTML>Seleccione el interlocutor remoto al que debe reenviarse el mensaje.</HTML>"},
		{"process.movetopartner", "Transmisión a los socios"},
		{"process.movetopartner.description", "Reenvío a un interlocutor, por ejemplo de la DMZ al sistema ERP."},
		{"label.movetodir.info", "<HTML>Por favor, configure el directorio del lado del servidor al que se debe mover el mensaje.</HTML>"},
		{"type.3", "tras la recepción"},
		{"type.2", "después del envío (error)"},
		{"button.cancel", "Demolición"},
		{"label.shell.info", "<HTML>Por favor, configure el comando shell que se ejecutará en este caso. Por favor, recuerde que esto es específico del sistema operativo, será redirigido al shell por defecto de su sistema operativo.</HTML>.</HTML>"},
		{"type.1", "tras el envío (éxito)"},
		{"label.movetodir.targetdir", "Directorio de destino ({0}):"},
		{"title.select.process", "Seleccione un nuevo proceso como evento ({0})"},
		{"button.ok", "Ok"},
		{"process.movetodirectory", "Mover al directorio"},
		{"title.configuration.movetopartner", "Reenvío de datos a un interlocutor [Interlocutor {0}, {1}]"},
		{"label.movetopartner.noroutingpartner", "<HTML>No hay ningún interlocutor remoto disponible en el sistema al que enviar mensajes. Por favor, añada primero un interlocutor al que enviar mensajes.</HTML>"},
		{"label.movetopartner", "Socio objetivo:"},
	};
}
