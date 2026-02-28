//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ResourceBundleConfigurationIssue_es.java 3     21/02/25 16:04 Heller $
package de.mendelson.comm.as2.configurationcheck;

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
public class ResourceBundleConfigurationIssue_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"1", "No se ha encontrado ninguna clave en el almacén de claves TLS"},
		{"10", "Falta el certificado de firma de un interlocutor remoto"},
		{"11", "Falta la clave de cifrado de una estación local"},
		{"12", "Falta la clave de firma de una emisora local"},
		{"13", "Uso de una clave de prueba pública como clave TLS"},
		{"14", "No se recomienda el uso de una máquina virtual Java de 32 bits para un uso productivo, ya que la memoria heap máxima se limita entonces a 1,3 GB."},
		{"15", "Servicio de Windows iniciado con la cuenta local del sistema"},
		{"16", "Gran cantidad de supervisión de directorios por unidad de tiempo"},
		{"17", "Problema de la lista de bloqueo (TLS)"},
		{"18", "Problema de la lista de revocación (enc/sign)"},
		{"19", "El cliente y el servidor se ejecutan en un solo proceso"},
		{"2", "Múltiples claves encontradas en el almacén de claves TLS - sólo puede ser una"},
		{"20", "No hay suficientes asas para el proceso del servidor"},
		{"3", "El certificado ha caducado (TLS)"},
		{"4", "El certificado ha caducado (enc/sign)"},
		{"5", "Activar el borrado automático - Hay un gran número de transacciones en el sistema"},
		{"6", "Asignar al menos 4 núcleos de procesador al sistema"},
		{"7", "Reserve al menos 8 GB de memoria principal para el proceso del servidor"},
		{"8", "Cantidad de conexiones salientes se establece en 0 - el sistema NO enviará"},
		{"9", "Falta el certificado de cifrado de un socio remoto"},
		{"hint.1", "<HTML>No se ha encontrado ninguna clave en el almacén de claves TLS de su sistema.<br>"
			+"Puede reconocer las claves por el símbolo de la llave que aparece delante de ellas al abrir la gestión de certificados.<br>"
			+"Se requiere exactamente una clave en el almacén de claves TLS para realizar el proceso de handshake para la seguridad de la línea.<br>"
			+"Sin esta clave, no podrá acceder ni salir de las conexiones seguras.</HTML>"},
		{"hint.10", "<HTML>No se ha asignado un certificado de firma a un interlocutor de conexión en su configuración.<br>"
			+"En este caso, no puede verificar las firmas digitales de su interlocutor. Por favor, abra la administración de socios y asigne un certificado de firma al socio.</HTML>"},
		{"hint.11", "<HTML>Su estación local no ha asignado una clave de encriptación.<br>"
			+"En esta configuración no se pueden descifrar los mensajes entrantes, independientemente del interlocutor.<br>"
			+"Abra la administración de interlocutores y asigne una clave privada a la estación local.</HTML>"},
		{"hint.12", "<HTML>Su estación local no ha asignado una clave de firma.<br>"
			+"En esta configuración no se pueden firmar digitalmente los mensajes salientes, independientemente del interlocutor.<br>"
			+"Abra la administración de interlocutores y asigne una clave privada a la estación local.</HTML>"},
		{"hint.13", "<HTML>En la entrega mendelson proporciona algunas claves de prueba.<br>"
			+"Están a disposición del público en el sitio web de mendelson.<br>"
			+"Si utiliza estas claves de forma productiva para tareas criptográficas dentro de su transferencia de datos, no ofrecen por tanto <strong>NINGUNA</strong> seguridad.<br>"
			+"Aquí también puedes enviar sin seguridad y sin cifrar.<br>"
			+"Si necesita una clave certificada, póngase en contacto con el servicio de asistencia de mendelson.</HTML>"},
		{"hint.14", "<HTML>Los procesos Java de 32 bits no pueden reservar suficiente memoria para mantener el sistema estable en funcionamiento productivo. Por favor, utilice una JVM de 64 bits.</HTML>"},
		{"hint.15", "<HTML>Ha configurado el Servidor AS2 de mendelson como un servicio de Windows y lo ha iniciado a través de una cuenta local del sistema (\"{0}\").<br>"
			+"Desgraciadamente, es posible que este usuario pierda los derechos sobre sus archivos previamente escritos tras una actualización de Windows, lo que puede provocar diversos problemas en el sistema.<br><br>"
			+"Por favor, configure un usuario separado para el servicio e inicie el servicio con este usuario.</HTML>"},
		{"hint.16", "<HTML>Ha definido un gran número de relaciones de interlocutor en su sistema y supervisa los directorios de salida correspondientes a intervalos demasiado cortos.<br>"
			+"Actualmente, se activan {0} vigilancias de directorio por minuto, el sistema no puede mantener este elevado ritmo.<br>"
			+"Por favor, reduzca este valor aumentando los intervalos de monitorización de los respectivos directorios de socios y también desactivando la monitorización de los socios en los que no sea necesaria.Con un gran número de socios, se recomienda desactivar toda la monitorización de directorios y crear los trabajos de envío desde su backend utilizando los comandos <i>AS2Send.exe</i> o <i>as2send.sh</i> según sea necesario.</HTML>"},
		{"hint.17", "<HTML>Los certificados autenticados contienen un enlace a una lista de revocación que puede utilizarse para declarar inválido este certificado. Por ejemplo, si el certificado ha sido comprometido.<br>"
			+"Se ha producido un problema al comprobar la lista de revocación del siguiente certificado TLS o el certificado ha sido revocado:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Información adicional sobre este certificado<br><br>"
			+"Alias: {1}<br>"
			+"Emisor: {2}<br>"
			+"Huella digital (SHA-1): {3}<br><br><br>"
			+"Tenga en cuenta que la comprobación automática de CRL puede desactivarse en la configuración.</HTML>"},
		{"hint.18", "<HTML>Los certificados autenticados contienen un enlace a una lista de revocación de certificados (CRL), que puede utilizarse para declarar inválido este certificado. Por ejemplo, si el certificado ha sido comprometido.<br>"
			+"Se ha producido un problema al comprobar la lista de revocación del siguiente certificado enc/sign o el certificado ha sido revocado:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Información adicional sobre este certificado<br><br>"
			+"Alias: {1}<br>"
			+"Emisor: {2}<br>"
			+"Huella digital (SHA-1): {3}<br><br><br>"
			+"Tenga en cuenta que la comprobación automática de CRL puede desactivarse en la configuración.</HTML>"},
		{"hint.19", "<HTML>Ha iniciado el cliente y el servidor del producto en un solo proceso. No se recomienda hacer esto en operación productiva. Como los recursos se asignan estáticamente a los programas, en este caso dispone de menos recursos para el funcionamiento del servidor y del cliente.<br><br>"
			+"Por favor, inicie primero el proceso del servidor y luego conéctese al cliente por separado.</HTML>"},
		{"hint.2", "<HTML>El almacén de claves TLS de su sistema contiene varias claves. Sin embargo, sólo puede haber una: se utiliza como clave TLS cuando se inicia el servidor.<br>"
			+"Por favor, elimine claves del almacén de claves TLS hasta que sólo quede una clave.<br>"
			+"Puede reconocer las claves en la gestión de certificados por el símbolo de la clave en la primera columna.<br>"
			+"Después de este cambio, es necesario reiniciar el servidor.</HTML>"},
		{"hint.20", "<HTML>Puede limitar el número de puertos y archivos abiertos por usuario en su sistema operativo.<br>"
			+"El usuario del proceso actual sólo puede utilizar {0} handles, que son muy pocos para el funcionamiento del servidor. El proceso servidor utiliza actualmente {1} handles.<br>"
			+"En Linux, puede ver este valor con \"ulimit -n\".<br><br>"
			+"Por favor, amplíe el valor máximo de las asas disponibles para este proceso al menos a {2}.</HTML>"},
		{"hint.3", "<HTML>Los certificados sólo tienen una vigencia limitada. Suele ser de uno, tres o cinco años.<br>"
			+"Un certificado que utiliza en su sistema para la seguridad de la línea TLS ya no es válido.<br>"
			+"No es posible realizar operaciones criptográficas con un certificado caducado, por lo que le recomendamos que renueve el certificado o haga crear o autenticar uno nuevo.<br><br>"
			+"<strong>Información adicional sobre el certificado:</strong<br><br>"
			+"Alias: {0}<br>"
			+"Emisor: {1}<br>"
			+"Huella digital (SHA-1): {2}<br>"
			+"Válido desde: {3}<br>"
			+"Válido hasta: {4}<br><br>"
			+"</HTML>"},
		{"hint.4", "<HTML>Los certificados sólo tienen una vigencia limitada. Suele ser de uno, tres o cinco años.<br>"
			+"Un certificado que utiliza en su sistema un socio para cifrar/descifrar datos, para la firma digital o para comprobar una firma digital ya no es válido.<br>"
			+"No es posible realizar operaciones criptográficas con un certificado caducado, por lo que le recomendamos que renueve el certificado o haga crear o autenticar uno nuevo.<br><br>"
			+"<strong>Información adicional sobre el certificado:</strong<br><br>"
			+"Alias: {0}<br>"
			+"Emisor: {1}<br>"
			+"Huella digital (SHA-1): {2}<br>"
			+"Válido desde: {3}<br>"
			+"Válido hasta: {4}<br><br>"
			+"</HTML>"},
		{"hint.5", "<HTML>En los ajustes, puede definir cuánto tiempo deben permanecer las transacciones en el sistema.<br>"
			+"Cuantas más transacciones permanezcan en el sistema, más recursos se necesitarán para su administración.<br>"
			+"Por lo tanto, debe utilizar los ajustes para asegurarse de que nunca tiene más de 30000 transacciones en el sistema.<br>"
			+"Tenga en cuenta que no se trata de un sistema de archivo, sino de un adaptador de comunicación.<br>"
			+"Tiene acceso a todos los registros de transacciones anteriores a través de la función de búsqueda integrada del registro del servidor.</HTML>"},
		{"hint.6", "<HTML>Para mejorar el rendimiento, es necesario que diferentes tareas se realicen en paralelo en el sistema.<br>"
			+"Por lo tanto, es necesario reservar un número correspondiente de núcleos de CPU para el proceso.</HTML>"},
		{"hint.7", "<HTML>Este programa está escrito en Java.<br>"
			+"Independientemente de la configuración física de su ordenador, debe reservar una cantidad correspondiente de memoria para el proceso del servidor. En tu caso, has reservado muy poca memoria.<br>"
			+"Consulte la ayuda (sección Instalación), donde se explica cómo reservar la memoria correspondiente para cada método de inicio.<br><br>"
			+"En cualquier caso, asegúrese de no reservar más memoria para el proceso del servidor que la que tiene su sistema de memoria principal. De lo contrario, el software se volverá casi inutilizable porque el sistema está constantemente intercambiando memoria con el disco duro.</HTML>"},
		{"hint.8", "<HTML>Ha realizado cambios en la configuración de modo que actualmente no es posible establecer conexiones salientes.<br>"
			+"Si desea establecer conexiones salientes con los interlocutores, el número de conexiones posibles debe ser al menos 1.</HTML>"},
		{"hint.9", "<HTML>Un interlocutor de conexión no ha asignado un certificado de cifrado en su configuración.<br>"
			+"En este caso, no podrá cifrar los mensajes dirigidos al interlocutor. Abra la administración de interlocutores y asigne un certificado de cifrado al interlocutor.</HTML>"},
	};
}
