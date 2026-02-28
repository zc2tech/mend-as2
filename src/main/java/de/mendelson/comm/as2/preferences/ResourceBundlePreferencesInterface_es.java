//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface_es.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundlePreferencesInterface_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.outboundstatusfiles.help", "<HTML><strong>Archivos de estado para transacciones salientes</strong><br><br>"
			+"Si activa esta opción, se escribirá un archivo de estado en el directorio \"outboundstatus\" por cada transacción saliente.<br>"
			+"Este archivo se utiliza con fines de integración y contiene información sobre la transacción correspondiente. Esto incluye, por ejemplo, el estado de la transacción, el número de mensaje, el remitente y el ID del destinatario.<br>"
			+"El nombre del archivo de estado contiene el número de mensaje y termina con \".sent.state\". Después de enviar los datos, puede analizar este archivo y ver el estado de la transacción.</HTML>.</HTML>"},
		{"label.checkrevocationlists.help", "<HTML><strong>Certificados: Comprobar listas de revocación</strong><br><br>"
			+"Una lista de revocación es una lista de certificados que han sido declarados inválidos debido a diversas preocupaciones o problemas de seguridad. Estos problemas pueden ser, por ejemplo, el compromiso de la clave privada, la pérdida del certificado o la sospecha de actividad fraudulenta. Las listas de revocación son gestionadas por autoridades de certificación u otras entidades de confianza autorizadas a emitir certificados. Comprobar las listas de revocación es importante para garantizar que los certificados utilizados en una conexión o para una operación criptográfica son válidos y dignos de confianza. Un certificado que figure en una lista de revocación no debe seguir utilizándose para operaciones criptográficas, ya que es potencialmente inseguro y podría plantear riesgos para la integridad de la comunicación.<br><br>"
			+"Puede utilizar esta configuración para determinar si el sistema también comprueba las listas de revocación en la comprobación de la configuración.</HTML>"},
		{"label.showsecurityoverwrite", "Gestión de interlocutores: sobrescribir la configuración de seguridad de la estación local"},
		{"label.showquota", "Gestión de socios: visualización de la configuración de notificaciones (cuota)"},
		{"label.cem", "Permitir el intercambio de certificados (CEM)"},
		{"label.outboundstatusfiles", "Ficheros de estado de las transacciones salientes"},
		{"label.showhttpheader", "Gestión de interlocutores: Visualización de la configuración del encabezado HTTP"},
		{"autoimport.tls.help", "<HTML><strong>Certificados TLS: Importación automática si se modifican</strong><br><br>"
			+"Si la conexión de un interlocutor se realiza a través de HTTPS (TLS, la URL comienza por \"https\"), puede comprobar periódicamente si el certificado TLS del interlocutor ha cambiado. Si se ha modificado y aún no se encuentra en su sistema, se importará automáticamente con toda la cadena de autenticación.<br>"
			+"El sistema comprueba los certificados de los interlocutores cada 15 minutos. Por lo tanto, puede pasar algún tiempo antes de que se reconozca un cambio en el certificado TLS de un interlocutor.<br><br>"
			+"También puede llevar a cabo este proceso manualmente realizando una prueba de conexión con un interlocutor y, a continuación, importando los certificados TLS que falten.<br><br>"
			+"Tenga en cuenta que esta es una configuración problemática a nivel de seguridad porque confía automáticamente en un certificado que se encuentra - sin preguntar.</HTML>"},
		{"label.checkrevocationlists", "Certificados: Comprobar listas de revocación"},
		{"autoimport.tls", "Certificados TLS: Importación automática si se modifican"},
		{"label.showsecurityoverwrite.help", "<HTML><strong>Sobrescribir la configuración de seguridad de la estación local</strong><br><br>"
			+"Si activa esta opción, aparecerá una pestaña adicional para cada interlocutor en la gestión de interlocutores.<br>"
			+"Esto le permite definir las claves privadas que se utilizan para este interlocutor en entrada y salida en cualquier caso - independientemente de la configuración de la estación local respectiva.<br>"
			+"Esta opción permite utilizar claves privadas diferentes para cada interlocutor en la misma estación local.<br><br>"
			+"Esta es una opción para la compatibilidad con otros productos AS2 - algunos sistemas tienen exactamente estos requisitos, pero requieren una configuración de relaciones de socios y no socios individuales.</HTML>"},
		{"label.showhttpheader.help", "<HTML><strong>Visualización de la configuración del encabezado HTTP</strong><br><br>"
			+"Si activa esta opción, en la administración de interlocutores aparecerá una ficha adicional para cada interlocutor, en la que podrá definir cabeceras HTTP definidas por el usuario para el envío de datos a este interlocutor.</HTML>"},
	};
}
