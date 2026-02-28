//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesAS2_es.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundlePreferencesAS2_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"country", "País"},
		{"showquotaconf", "Mostrar cuota en la gestión de socios"},
		{"proxyport", "Puerto proxy HTTP"},
		{"retrycount", "Número de intentos de conexión"},
		{"colorblindness", "Ayuda para el daltonismo"},
		{"set.to", "se fijó en"},
		{"notification.setting.updated", "Se ha modificado la configuración de las notificaciones."},
		{"showhttpheaderconf", "Mostrar la gestión de cabeceras HTTP en el cliente"},
		{"checkrevocationlist", "Comprobar las listas de revocación de certificados"},
		{"autologdirdeleteolderthan", "Limpiar el directorio de registro (más antiguo que)"},
		{"autoimportpartnertlscertificates", "Importación automática de certificados TLS asociados modificados"},
		{"stricthostcheck", "(TLS) Comprobar host"},
		{"autostatsdelete", "Borrar automáticamente datos estadísticos antiguos"},
		{"cem", "Utilizar CEM"},
		{"language", "Idioma del cliente"},
		{"setting.updated", "Se ha actualizado la configuración"},
		{"jetty.http.port", "Puerto de entrada HTTP"},
		{"proxypass", "Datos de acceso al proxy HTTP (contraseña)"},
		{"commed", "Edición comunitaria"},
		{"embeddedhttpserverrequestlog", "Registro de peticiones del servidor HTTP integrado"},
		{"proxyuser", "Datos de acceso al proxy HTTP (usuario)"},
		{"maxoutboundconnections", "Número máximo de conexiones salientes simultáneas"},
		{"proxyuseauth", "Utilizar los datos de acceso del proxy HTTP"},
		{"outboundstatusfile", "Crear un archivo de estado para cada transacción"},
		{"module.name", "[AJUSTES]"},
		{"proxyuse", "Utilizar proxy HTTP para la conexión saliente"},
		{"automsgdeletelog", "Borrar transacciones antiguas (entrada en el registro)"},
		{"retrywaittime", "Restablecer conexión cada n segundos"},
		{"automsgdeleteolderthanmults", "Borrar transacciones antiguas (unidad de tiempo en s)"},
		{"autostatsdeleteolderthan", "Borrar estadísticas (anteriores a)"},
		{"autologdirdelete", "Limpiar automáticamente el directorio de registro"},
		{"logpollprocess", "Documentar el proceso de sondeo en el registro"},
		{"showoverwritelocalstationsecurity", "Visualizar: Sobrescribir la seguridad de la estación local"},
		{"TRUE", "encendido"},
		{"asyncmdntimeout", "Tiempo de espera para MDN asíncrono en minutos"},
		{"httpsendtimeout", "Tiempo de espera de envío (HTTP/S)"},
		{"jetty.connectionlimit.maxConnections", "Número máximo de conexiones entrantes simultáneas"},
		{"lastupdatecheck", "Última comprobación de la nueva versión (hora unix)"},
		{"receiptpartnersubdir", "Utilizar subdirectorio por socio"},
		{"setting.reset", "La configuración del servidor [{0}] se ha restablecido al valor predeterminado."},
		{"dirmsg", "Directorio básico de mensajes"},
		{"jetty.ssl.port", "Puerto de entrada HTTPS"},
		{"FALSE", "apagado"},
		{"automsgdeleteolderthan", "Borrar transacciones antiguas (anteriores a)"},
		{"automsgdelete", "Borrar automáticamente transacciones antiguas"},
		{"datasheetreceipturl", "Recibir la URL de la ficha de datos"},
		{"proxyhost", "Proxy HTTP"},
		{"trustallservercerts", "(TLS) Confiar en todos los certificados de servidores remotos"},
	};
}
