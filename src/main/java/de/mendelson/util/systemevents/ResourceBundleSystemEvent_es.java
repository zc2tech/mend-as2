//$Header: /as4/de/mendelson/util/systemevents/ResourceBundleSystemEvent_es.java 3     15/01/25 10:18 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEvent_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"type.100000", "Sin especificar"},
		{"category.400", "Certificado"},
		{"type.406", "Intercambio de certificados (solicitud entrante)"},
		{"type.802", "Cuota alcanzada"},
		{"type.407", "Certificado (importación de almacén de claves)"},
		{"type.404", "Intercambio de certificados"},
		{"type.800", "Cuota"},
		{"type.405", "Caduca el certificado"},
		{"type.801", "Cuota alcanzada"},
		{"type.402", "Certificado (alias modificado)"},
		{"type.403", "Certificado (suprimido)"},
		{"category.800", "Contingente"},
		{"type.400", "certificado"},
		{"type.401", "Certificado (añadido)"},
		{"type.300", "Transacción"},
		{"category.1000", "Tratamiento de datos"},
		{"category.1400", "Interfaz XML"},
		{"type.1202", "Crear directorio"},
		{"type.1201", "Archivo (suprimir)"},
		{"type.1200", "Operación de archivo"},
		{"type.1204", "Archivo (copia)"},
		{"type.1203", "Archivo (mover)"},
		{"category.500", "Base de datos"},
		{"category.100", "Componente de servidor"},
		{"type.705", "Socio (añadido)"},
		{"type.703", "Socio (modificado)"},
		{"origin.2", "Usuarios"},
		{"type.704", "Socio (suprimido)"},
		{"origin.3", "Transacción"},
		{"type.701", "Cambio de configuración"},
		{"type.305", "Transacción (cancelar)"},
		{"type.702", "Comprobación de la configuración"},
		{"type.306", "Transacción (reenvío)"},
		{"origin.1", "Sistema"},
		{"type.303", "Transacción (mensaje duplicado)"},
		{"category.900", "Notificación"},
		{"category.100000", "Otros"},
		{"type.700", "Configuración"},
		{"type.304", "Transacción (suprimir)"},
		{"type.301", "Error de transacción"},
		{"type.302", "Transacción (devolución rechazada)"},
		{"type.200", "Conexión"},
		{"type.201", "Prueba de conexión"},
		{"category.1100", "Activación"},
		{"category.1500", "Interfaz REST"},
		{"type.1102", "Expiración de la licencia"},
		{"type.1101", "Actualización de licencias"},
		{"type.1100", "Licencia"},
		{"type.1503", "Borrar certificado"},
		{"type.1502", "Configuración del certificado"},
		{"type.1501", "Añadir certificado"},
		{"type.1500", "REST"},
		{"type.1507", "Enviar pedido"},
		{"type.1506", "Borrar socio"},
		{"type.1505", "Configuración de socios"},
		{"type.1504", "Añadir socio"},
		{"category.200", "Conexión"},
		{"type.101", "Inicio del servidor"},
		{"type.102", "Servidor en funcionamiento"},
		{"type.100", "Cierre del servidor"},
		{"severity.1", "Información"},
		{"severity.2", "Advertencia"},
		{"severity.3", "Error"},
		{"type.1000", "Tratamiento de datos"},
		{"category.1200", "Operación de archivo"},
		{"type.1400", "XML"},
		{"type.1002", "Tratamiento posterior"},
		{"type.1001", "Preprocesamiento"},
		{"type.1402", "Configuración de socios"},
		{"type.1401", "Configuración del certificado"},
		{"type.112", "Cierre del servidor TRFC"},
		{"type.113", "Se inicia el programador"},
		{"type.110", "Servidor TRFC en funcionamiento"},
		{"type.199", "Componente de servidor"},
		{"type.111", "Estado del servidor TRFC"},
		{"category.700", "Configuración"},
		{"category.300", "Transacción"},
		{"type.901", "Notificación (envío correcto)"},
		{"type.109", "Inicio del servidor TRFC"},
		{"type.902", "Notificación (envío fallido)"},
		{"type.503", "Base de datos (inicialización)"},
		{"type.107", "Servidor HTTP en ejecución"},
		{"type.900", "Notificación"},
		{"type.108", "Cierre del servidor HTTP"},
		{"type.501", "Creación de bases de datos"},
		{"type.105", "Cierre del servidor de base de datos"},
		{"type.502", "Base de datos (actualización)"},
		{"type.106", "Se inicia el servidor HTTP"},
		{"type.103", "Se inicia el servidor de base de datos"},
		{"type.500", "Base de datos"},
		{"type.104", "Servidor de base de datos en funcionamiento"},
		{"category.1300", "Operación cliente"},
		{"type.1301", "Inicio de sesión de usuario (correcto)"},
		{"type.1300", "Cliente"},
		{"type.1303", "Separación de usuarios"},
		{"type.1302", "Inicio de sesión de usuario (fallido)"},
		{"type.116", "Supervisión de directorios (cambio de estado)"},
		{"type.117", "Puerto de recepción"},
		{"type.114", "Programador en marcha"},
		{"type.115", "Apagado del programador"},
                {"type." + SystemEvent.TYPE_DATABASE_ROLLBACK, "Reversión transacción"},
	};
}
