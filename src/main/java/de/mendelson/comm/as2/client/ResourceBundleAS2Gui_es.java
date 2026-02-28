//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui_es.java 3     8/01/25 16:18 Heller $
package de.mendelson.comm.as2.client;

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
public class ResourceBundleAS2Gui_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dbconnection.failed.message", "No se ha podido establecer la conexión con el servidor de base de datos AS2: {0}"},
		{"dialog.msg.delete.title", "Borrar mensajes"},
		{"buy.license", "Comprar licencia"},
		{"filter.partner", "Restricción de socios"},
		{"uploading.to.server", "Transferencia al servidor"},
		{"dialog.resend.title", "Reenviar datos"},
		{"menu.file.certificate.ssl", "TLS"},
		{"menu.file.certificates", "Certificados"},
		{"menu.file.cem", "Gestión del intercambio de certificados (CEM)"},
		{"dialog.resend.message", "¿Realmente desea reenviar la transacción seleccionada?"},
		{"tab.welcome", "Noticias y actualidad"},
		{"server.answer.timeout.title", "Tiempo de espera en la conexión cliente-servidor"},
		{"details", "Detalles del mensaje"},
		{"filter.showstopped", "Espectáculo parado"},
		{"menu.help.about", "Acerca de"},
		{"welcome", "Bienvenido, {0}"},
		{"resend.failed.unknown.receiver", "Fallo de reenvío: Destinatario {0} desconocido - por favor, compruebe si este interlocutor sigue existiendo en el sistema."},
		{"menu.file.serverinfo", "Mostrar la configuración del servidor HTTP"},
		{"delete.msg", "Borrar"},
		{"dialog.resend.message.multiple", "¿Realmente desea reenviar las {0} transacciones seleccionadas?"},
		{"tab.transactions", "Transacciones"},
		{"filter.localstation", "Restricción de la estación local"},
		{"dbconnection.failed.title", "No hay conexión posible"},
		{"menu.file.ha", "Instancias de alta disponibilidad"},
		{"login.failed.client.incompatible.title", "Login rechazado"},
		{"menu.file.certificate.signcrypt", "Firma/cifrado"},
		{"fatal.error", "Error"},
		{"filter.showfinished", "Mostrar terminado"},
		{"menu.help", "Ayuda"},
		{"menu.file.systemevents", "Eventos del sistema"},
		{"menu.file.resend", "Enviar como una nueva transacción"},
		{"filter.direction.outbound", "A partir de"},
		{"menu.file.resend.multiple", "Enviar como nuevas transacciones"},
		{"menu.file.migrate.hsqldb", "Migrar desde HSQLDB"},
		{"menu.file.cemsend", "Intercambiar certificados con socios (CEM)"},
		{"dialog.msg.delete.message", "¿Realmente quieres borrar permanentemente los mensajes seleccionados?"},
		{"menu.help.forum", "Foro"},
		{"filter.direction", "Restricción direccional"},
		{"filter.showpending", "Mostrar espera"},
		{"filter.to", "Hasta"},
		{"filter.use", "Restricción temporal"},
		{"keyrefresh", "Actualizar certificados"},
		{"menu.help.supportrequest", "Solicitud de asistencia"},
		{"filter.none", "-- Ninguno"},
		{"msg.delete.success.single", "{0} El mensaje ha sido borrado"},
		{"new.version.logentry.2", "Puede descargarlos en {0}."},
		{"resend.failed.unknown.sender", "Reenvío fallido: Remitente desconocido {0} - por favor, compruebe si este interlocutor aún existe en el sistema."},
		{"new.version.logentry.1", "Hay una nueva versión disponible."},
		{"logputput.disabled", "** La salida del registro fue suprimida **"},
		{"menu.file.partner", "Socio"},
		{"menu.file.statistic", "Estadísticas"},
		{"menu.file.certificate", "Certificados"},
		{"login.failed.client.incompatible.message", "El servidor informa que este cliente no tiene la versión correcta.\nPor favor, utilice el cliente que coincida con el servidor."},
		{"filter.from", "En"},
		{"configurecolumns", "Columnas"},
		{"server.answer.timeout.details", "El servidor no responde en el plazo definido: ¿la carga es demasiado elevada?"},
		{"menu.help.helpsystem", "Sistema de ayuda"},
		{"menu.file.datasheet", "Ficha técnica de conexión"},
		{"refresh.overview", "Actualizar la lista de transacciones"},
		{"warning.refreshstopped", "La actualización de la interfaz de usuario está desactivada."},
		{"resend.failed.nopayload", "Fallo al reenviar como nueva transacción: La transacción seleccionada {0} no tiene datos de usuario."},
		{"msg.delete.success.multiple", "{0} Se han borrado mensajes"},
		{"menu.file.quota", "Contingentes"},
		{"menu.file", "Archivo"},
		{"menu.file.send", "Enviar archivo al socio"},
		{"filter", "Filtros"},
		{"new.version", "Ya está disponible una nueva versión. Haga clic aquí para descargarla."},
		{"logputput.enabled", "** La salida de registro ha sido activada **"},
		{"stoprefresh.msg", "Actualización on/off"},
		{"menu.help.shop", "Tienda en línea mendelson"},
		{"filter.direction.inbound", "Entrante"},
		{"menu.file.preferences", "Ajustes"},
		{"menu.file.exit", "Salida"},
		{"menu.file.searchinserverlog", "Buscar en el registro del servidor"},
                {"no.helpset.for.language", "Lo sentimos, no hay sistema de ayuda disponible para su idioma, se utilizará el sistema de ayuda en inglés." },
	};
}
