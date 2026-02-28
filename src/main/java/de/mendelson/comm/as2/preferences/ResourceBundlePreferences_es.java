//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

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
public class ResourceBundlePreferences_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Borrar entradas de transacciones antiguas</strong><br><br>"
			+"Define el plazo durante el cual las transacciones y los datos temporales asociados permanecen en el sistema y se muestran en el resumen de transacciones.<br>"
			+"Estos ajustes <strong>no</strong> afectan a los datos/archivos recibidos, éstos permanecen inalterados.<br>"
			+"En el caso de las transacciones eliminadas, el registro de transacciones sigue estando disponible a través de la función de búsqueda de registros.</HTML</HTML>"},
		{"button.browse", "Visite"},
		{"label.asyncmdn.timeout", "Tiempo máximo de espera para MDN asíncronos"},
		{"label.deletestatsolderthan", "A partir de datos estadísticos más antiguos que"},
		{"embedded.httpconfig.not.available", "Servidor HTTP no disponible o problemas de acceso al fichero de configuración"},
		{"label.loghttprequests", "Registro de peticiones HTTP desde el servidor HTTP integrado"},
		{"testmail.title", "Enviar un correo electrónico de prueba"},
		{"label.country", "País/Región"},
		{"label.httpport", "Puerto de entrada HTTP"},
		{"tab.connectivity", "Conexiones"},
		{"label.deletemsglog", "Borrado automático de archivos y entradas de registro"},
		{"label.hicontrastmode", "Modo de alto contraste"},
		{"label.mailaccount", "Cuenta del servidor de correo"},
		{"label.max.outboundconnections.help", "<HTML><strong>Máximo de conexiones paralelas salientes</strong><br><br>"
			+"Es el número máximo de conexiones salientes paralelas que abrirá tu sistema.<br>"
			+"Este valor se utiliza principalmente para proteger su sistema asociado de la sobrecarga de conexiones entrantes de su lado.<br><br>"
			+"El valor por defecto es 9999.</HTML>"},
		{"label.httpsport", "Puerto de entrada HTTPS"},
		{"label.keystore.https", "Keystore (para envío vía Https):"},
		{"maintenancemultiplier.minute", "Minuto(s)"},
		{"button.mailserverdetection", "Averiguar el servidor de correo"},
		{"label.mailpass", "Contraseña del servidor de correo"},
		{"label.mailhost.hint", "IP o dominio del servidor"},
		{"label.retry.waittime.help", "<HTML><strong>Tiempo de espera entre reintentos de conexión</strong><br><br>"
			+"Es el tiempo en segundos que el sistema espera antes de volver a conectarse al interlocutor.<br>"
			+"Sólo se realiza un nuevo intento de conexión si no ha sido posible establecer una conexión con un interlocutor (por ejemplo, fallo del sistema del interlocutor o problema de infraestructura).<br>"
			+"El número de reintentos de conexión se puede configurar en la propiedad <strong>Número máximo de reintentos de conexión</strong>.<br><br>"
			+"El valor preestablecido es 30s.</HTML>"},
		{"label.smtpauthorization.pass.hint", "Contraseña del servidor SMTP"},
		{"button.cancel", "Cancelar"},
		{"label.proxy.port.hint", "Puerto"},
		{"label.deletemsgolderthan", "Entradas de transacciones anteriores a"},
		{"label.smtpauthorization.user.hint", "Nombre de usuario del servidor SMTP"},
		{"label.proxy.user", "Usuarios"},
		{"label.smtpauthorization.header", "Autorización SMTP"},
		{"keystore.hint", "<HTML><strong>Atención:</strong><br>"
			+"Por favor, cambie estos parámetros sólo si desea integrar almacenes de claves externos. Cambiar las rutas puede causar problemas durante la actualización.</HTML>"},
		{"tab.log", "Protocolo"},
		{"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Eliminación de directorios de registro antiguos</strong><br><br>"
			+"Aunque se hayan eliminado las transacciones antiguas, los procesos pueden seguir rastreándose a través de los archivos de registro existentes.<br>"
			+"Esta configuración elimina estos archivos de registro y también todos los archivos de eventos del sistema que se encuentren dentro del mismo período de tiempo.</HTML>"},
		{"label.loghttprequests.help", "<HTML><strong>Protocolo de solicitud HTTP</strong><br><br>"
			+"Si está activado, el servidor HTTP integrado (Jetty) escribe un registro de peticiones en los archivos <strong>log/yyyy_MM_dd.jetty.request.log</strong>. Estos archivos de registro no son eliminados por el mantenimiento del sistema - por favor, elimínelos manualmente.<br><br>"
			+"Por favor, reinicie el software para que los cambios en esta configuración surtan efecto.</HTML>"},
		{"tab.maintenance", "Mantenimiento del sistema"},
		{"tab.interface", "Módulos"},
		{"label.smtpauthorization.credentials", "Usuario/contraseña"},
		{"checkbox.notifypostprocessing", "Problemas con el tratamiento posterior"},
		{"label.replyto", "Dirección de respuesta"},
		{"label.language.help", "<HTML><strong>Idioma</strong><br><br>"
			+"Es el idioma de visualización del cliente. Si ejecutas el cliente y el servidor en procesos diferentes (lo cual es recomendable), el idioma del servidor puede ser diferente.<br>"
			+"El idioma utilizado en el protocolo es siempre el idioma del servidor.</HTML>.</HTML>"},
		{"label.displaymode.help", "<HTML><strong>Pantalla</strong><br><br>"
			+"Aquí se establece uno de los modos de visualización soportados por el cliente.<br>"
			+"Esto también se puede establecer a través de parámetros de línea de comandos al llamar.</HTML>"},
		{"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Borrar datos estadísticos antiguos</strong><br><br>"
			+"El sistema recopila datos de compatibilidad de los sistemas asociados y puede mostrarlos en forma de estadísticas.<br>"
			+"Esto determina el plazo de conservación de estos datos.</HTML>"},
		{"label.stricthostcheck", "TLS: Comprobación estricta del nombre de host"},
		{"label.httpsport.help", "<HTML><strong>Puerto de entrada HTTPS</strong><br><br>"
			+"Este es el puerto para las conexiones cifradas entrantes (TLS). Esta configuración se pasa al servidor HTTP incrustado, debe reiniciar el servidor AS2 después de un cambio.<br>"
			+"El puerto forma parte de la URL a la que su interlocutor debe enviar los mensajes AS2. Este es https://Host:<strong>Puerto</strong>/as2/HttpReceptor<br><br>"
			+"El valor por defecto es 8443.</HTML>"},
		{"checkbox.notifycertexpire", "Antes de la expiración de los certificados"},
		{"label.mailport.hint", "Puerto SMTP"},
		{"label.country.help", "<HTML><strong>País/Región</strong><br><br>"
			+"Esta configuración sólo controla esencialmente el formato de fecha utilizado para mostrar datos de transacciones, etc. en el cliente.</HTML>"},
		{"label.smtpauthorization.oauth2.clientcredentials", "OAuth2 (credenciales del cliente)"},
		{"event.preferences.modified.subject", "Se ha modificado el valor {0} de la configuración del servidor"},
		{"warning.changes.canceled", "El usuario ha cancelado el diálogo de ajustes - no se ha realizado ningún cambio en los ajustes."},
		{"label.proxy.useauthentification", "Utilizar autenticación para proxy"},
		{"label.keystore.encryptionsign", "Keystore( cifrado, firma):"},
		{"label.darkmode", "Modo oscuro"},
		{"label.smtpauthorization.none", "Ninguno"},
		{"label.retry.max.help", "<HTML><strong>Número máximo de intentos para establecer una conexión</strong><br><br>"
			+"Es el número de reintentos utilizados para repetir las conexiones con un interlocutor si no se ha podido establecer una conexión.<br>"
			+"El tiempo de espera entre estos reintentos se puede establecer en la propiedad <strong>Tiempo de espera entre reintentos de conexión</strong>.<br><br>"
			+"El valor por defecto es 10.</HTML>"},
		{"tab.security", "Seguridad"},
		{"label.mailport", "Puerto"},
		{"label.logpollprocess.help", "<HTML><strong>Información sobre el proceso de sondeo de los directorios</strong><br><br>"
			+"Si activa esta opción, cada operación de sondeo de un directorio de salida se anota en el registro.<br>"
			+"Como esto puede ser un número muy grande de entradas, por favor, no utilice esta opción bajo ninguna circunstancia en la operación productiva, pero sólo para fines de prueba.</HTML>"},
		{"label.keystore.https.pass", "Contraseña del almacén de claves (para el envío a través de Https):"},
		{"info.restart.client", "Debe reiniciar el cliente para que estos cambios surtan efecto."},
		{"label.proxy.url", "URL proxy"},
		{"label.proxy.pass", "contraseña"},
		{"label.notificationmail", "Destinatario de la notificación Dirección de correo"},
		{"button.testmail", "Enviar correo de prueba"},
		{"maintenancemultiplier.day", "Día(s)"},
		{"label.asyncmdn.timeout.help", "<HTML><strong>Tiempo máximo de espera para MDN asíncronos</strong><br><br>"
			+"El tiempo que el sistema espera una MDN (Notificación de entrega de mensaje) asíncrona para un mensaje AS2 enviado antes de establecer la transacción en estado \"fallido\".<br>"
			+"Este valor es válido en todo el sistema para todos los socios.<br><br>"
			+"El valor por defecto es 30 min.</HTML>"},
		{"label.keystore.pass", "Contraseña del almacén de claves (cifrado/firma digital):"},
		{"label.colorblindness", "Ayuda para el daltonismo"},
		{"dirmsg", "Directorio de noticias"},
		{"label.deletelogdirolderthan", "Registrar datos anteriores a"},
		{"receipt.subdir", "Crear subdirectorios por interlocutor para la recepción de mensajes"},
		{"label.proxy.user.hint", "Usuario de inicio de sesión proxy"},
		{"label.smtpauthorization.oauth2.authorizationcode", "OAuth2 (Código de autorización)"},
		{"tab.proxy", "Proxy"},
		{"checkbox.notifyclientserver", "Problemas con la conexión cliente-servidor"},
		{"label.proxy.pass.hint", "Contraseña de acceso al proxy"},
		{"label.maxmailspermin.help", "<HTML><strong>Número máximo de notificaciones/min</strong><br><br>"
			+"Para evitar demasiados correos electrónicos, puedes resumir las notificaciones estableciendo el número máximo de notificaciones por minuto.<br>"
			+"Con esta función, recibirá correos electrónicos que contengan varias notificaciones.</HTML</HTML>"},
		{"tab.notification", "Notificación"},
		{"checkbox.notifycem", "Actos de intercambio de certificados (CEM)"},
		{"label.maxmailspermin", "Número máximo de notificaciones/min"},
		{"label.logpollprocess", "Información sobre el proceso de sondeo de directorios"},
		{"filechooser.selectdir", "Seleccione el directorio que desea configurar"},
		{"event.preferences.modified.body", "Valor antiguo: {0}\nNuevo valor: {1}"},
		{"label.proxy.use", "Utilizar un proxy HTTP para las conexiones HTTP/HTTP salientes"},
		{"label.deletemsglog.help", "<HTML><strong>Borrado automático de archivos y entradas de registro</strong><br><br>"
			+"En la configuración, tienes la opción de borrar archivos antiguos (mantenimiento del sistema).<br>"
			+"Si ha configurado esta opción y la activa, se registrará cada eliminación de un archivo antiguo.<br>"
			+"También se genera un evento del sistema, que puede informarle de este proceso a través de la función de notificación.</HTML>"},
		{"tab.misc", "General"},
		{"label.language", "Idioma"},
		{"warning.serverrestart.required", "Reinicie el servidor para que estos cambios surtan efecto."},
		{"remotedir.select", "Seleccione el directorio en el servidor"},
		{"label.max.outboundconnections", "Máximo de conexiones paralelas salientes"},
		{"label.retry.waittime", "Tiempo de espera entre reintentos de conexión"},
		{"label.httpsend.timeout", "HTTP/S Tiempo de espera de envío"},
		{"receipt.subdir.help", "<HTML><strong>Subdirectorios de recepción</strong><br><br>"
			+"Establece si los datos deben recibirse en el directorio <strong>&lt;Estación local&gt;/inbox</strong> o <strong>&lt;Estación local&gt;/inbox/&lt;Nombre del socio&gt;</strong>.</HTML>"},
		{"button.modify", "Editar"},
		{"testmail.message.error", "Error al enviar el e-mail de prueba:\n{0}"},
		{"label.displaymode", "Representación"},
		{"label.smtpauthorization.pass", "contraseña"},
		{"label.security", "Seguridad de la conexión"},
		{"button.ok", "Ok"},
		{"checkbox.notifyfailure", "Tras los problemas del sistema"},
		{"checkbox.notifyresend", "Tras los reenvíos rechazados"},
		{"testmail.message.success", "Se ha enviado correctamente un correo electrónico de prueba a {0}."},
		{"label.mailhost", "Servidor de correo (SMTP)"},
		{"label.max.inboundconnections.help", "<HTML><strong>Máximo de conexiones paralelas entrantes</strong><br><br>"
			+"Este es el número máximo de conexiones entrantes paralelas que pueden abrirse desde el exterior a su instalación AS2 de mendelson. Este valor se aplica a todo el software y no se limita a interlocutores individuales.<br>"
			+"La configuración se transmite al servidor HTTP incrustado, debe reiniciar el servidor AS2 después de un cambio.<br><br>"
			+"Aunque es posible limitar el número de conexiones entrantes paralelas, es mejor realizar este ajuste en el cortafuegos o en el proxy de subida, ya que así se aplica a todo el sistema y no sólo a un único programa.<br><br>"
			+"El valor por defecto es 1000.</HTML>"},
		{"header.dirvalue", "Directorio"},
		{"filechooser.keystore", "Seleccione el archivo de almacén de claves (formato JKS)."},
		{"label.trustallservercerts", "TLS: Confíe en todos los certificados de servidor final de sus socios AS2"},
		{"header.dirname", "Tipo"},
		{"warning.clientrestart.required", "Los ajustes del cliente han sido modificados - por favor reinicie el cliente para que sean válidos"},
		{"title", "Ajustes"},
		{"label.litemode", "Modo luz"},
		{"label.httpsend.timeout.help", "<HTML><strong>HTTP/S enviar tiempo de espera</strong><br><br>"
			+"Este es el valor del tiempo de espera de la conexión de red para las conexiones salientes.<br>"
			+"Si transcurrido este tiempo no se ha establecido ninguna conexión con el sistema asociado, el intento de conexión se cancela y pueden realizarse más intentos de conexión posteriormente según los ajustes de reintento.<br><br>"
			+"El valor por defecto es 5000ms.</HTML>"},
		{"label.retry.max", "Número máximo de intentos para establecer una conexión"},
		{"label.notificationmail.help", "<HTML><strong>Dirección de correo electrónico del destinatario de la notificación</strong><br><br>"
			+"Dirección de correo electrónico del destinatario de la notificación.<br>"
			+"Si la notificación debe enviarse a varios destinatarios, introduzca aquí una lista de direcciones de destinatarios separada por comas.</HTML>"},
		{"label.proxy.url.hint", "IP o dominio del proxy"},
		{"label.trustallservercerts.help", "<HTML><strong>TLS: Confíe en todos los certificados de servidor final de sus socios AS2</strong><br><br>"
			+"Normalmente, TLS requiere que todos los certificados de la cadena de confianza del sistema AS2 de su socio se encuentren en su gestor de certificados TLS.<br><br>"
			+"Si activa esta opción, confiará en el certificado final de su sistema asociado al establecer una conexión saliente si sólo dispone de los certificados raíz e intermedio asociados en el gestor de certificados TLS.<br>"
			+"Tenga en cuenta que esta opción sólo tiene sentido si su pareja utiliza un certificado notarial.<br>"
			+"En cualquier caso, siempre se aceptan los certificados autofirmados.<br><br>"
			+"<strong>Advertencia:</strong> Activar esta opción reduce el nivel de seguridad, ya que es posible que se produzcan ataques de intermediario.</HTML>"},
		{"tab.dir", "Directorios"},
		{"label.sec", "s"},
		{"label.days", "Días"},
		{"event.notificationdata.modified.subject", "Se ha modificado la configuración de las notificaciones"},
		{"label.httpport.help", "<HTML><strong>Puerto de entrada HTTP</strong><br><br>"
			+"Este es el puerto para las conexiones entrantes no encriptadas. Esta configuración se pasa al servidor HTTP incrustado, debe reiniciar el servidor AS2 después de un cambio.<br>"
			+"El puerto forma parte de la URL a la que su interlocutor debe enviar los mensajes AS2. Se trata de http://Host:<strong>Port</strong>/as2/HttpReceiver.<br><br>"
			+"El valor por defecto es 8080.</HTML>"},
		{"label.min", "min"},
		{"checkbox.notifyconnectionproblem", "Para problemas de conexión"},
		{"label.mailport.help", "<HTML><strong>Puerto SMTP</strong><br><br>"
			+"Por regla general, es uno de estos valores:<br>"
			+"<strong>25</strong> (puerto estándar)<br>"
			+"<strong>465</strong> (puerto TLS, valor obsoleto)<br>"
			+"<strong>587</strong> (puerto TLS, valor por defecto)<br>"
			+"<strong>2525</strong> (puerto TLS, valor alternativo, no estándar)</HTML>"},
		{"maintenancemultiplier.hour", "Hora(s)"},
		{"tab.language", "Cliente"},
		{"label.stricthostcheck.help", "<HTML><strong>TLS: Comprobación estricta del nombre de host</strong><br><br>"
			+"Aquí puedes establecer si el nombre común (CN) del certificado remoto debe coincidir con el host remoto en el caso de una conexión TLS saliente.<br>"
			+"Esta comprobación sólo se aplica a los certificados autenticados.</HTML>"},
		{"label.smtpauthorization.user", "Usuarios"},
		{"label.autodelete", "Eliminación automática"},
		{"testmail", "Correo de prueba"},
		{"event.notificationdata.modified.body", "Los datos de notificación fueron creados por\n\n{0}\n\na\n\n{1}\n\n cambiado."},
		{"checkbox.notifytransactionerror", "Tras errores en las transacciones"},
		{"label.max.inboundconnections", "Máximo de conexiones paralelas entrantes"},
	};
}
