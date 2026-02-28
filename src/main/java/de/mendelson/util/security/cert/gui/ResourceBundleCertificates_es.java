//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates_es.java 3     11/03/25 16:42 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleCertificates_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"cert.delete.impossible", "La entrada no puede borrarse, está en uso.\nUtilice \"Mostrar uso\" para obtener más información."},
		{"label.keystore.export", "Exportar todas las entradas como un archivo keystore (¡sólo con fines de copia de seguridad!)"},
		{"certificate.import.alias", "Alias para este certificado:"},
		{"tab.info.basic", "detalles"},
		{"warning.deleteallexpired.expired.but.used.text", "{0} Las claves/certificados caducados se utilizan en la configuración y, por tanto, no se eliminan."},
		{"filechooser.certificate.import", "Seleccione el archivo de certificado para la importación"},
		{"button.import", "Importar"},
		{"menu.export", "Exportar"},
		{"certificate.ca.import.success.message", "El certificado CA se ha importado correctamente con el alias \"{0}\"."},
		{"button.keycopy", "Copiar en {0} gestión"},
		{"button.delete", "Borrar clave/certificado"},
		{"label.cert.valid", "Este certificado es válido"},
		{"display.ca.certs", "Mostrar certificados CA ({0})"},
		{"keystore.readonly.message", "Protegido contra escritura. No es posible modificarlo."},
		{"keycopy.success.text", "La entrada [{0}] se ha copiado correctamente"},
		{"menu.tools.generatekey", "Generar nueva clave (autofirmada)"},
		{"label.key.export.pkcs12", "Exportar clave (PKCS#12, PEM) (sólo para copias de seguridad)"},
		{"button.keycopy.signencrypt", "Cifrado/firma"},
		{"label.key.invalid", "Esta clave no es válida"},
		{"dialog.cert.delete.title", "Borrar certificado"},
		{"success.deleteallexpired.title", "Eliminar certificados/claves caducados y no utilizados"},
		{"success.deleteallexpired.text", "{0} se han eliminado las claves/certificados caducados y no utilizados"},
		{"module.locked", "Esta gestión de certificados está actualmente abierta exclusivamente por otro cliente, ¡usted no puede realizar ningún cambio!"},
		{"button.edit", "Renombrar alias"},
		{"label.keystore", "Almacén"},
		{"warning.deleteallexpired.noneavailable.title", "No disponible"},
		{"dialog.cert.delete.message", "¿Realmente desea eliminar el certificado con el alias \"{0}\"?"},
		{"button.cancel", "Cancelar"},
		{"title.cert.in.use", "Se utiliza el certificado"},
		{"label.cert.invalid", "Este certificado no es válido"},
		{"certificate.import.error.message", "Se ha producido un error durante la importación:\n{0}"},
		{"button.delete.all.expired", "Borrar todas las claves/certificados caducados"},
		{"button.keycopy.tls", "TLS"},
		{"button.ok", "Ok"},
		{"generatekey.error.message", "{0}"},
		{"menu.file.close", "Salida"},
		{"label.selectcsrfile", "Seleccione el archivo para guardar la solicitud de autenticación"},
		{"warning.deleteallexpired.noneavailable.text", "No hay entradas caducadas o no utilizadas"},
		{"button.export", "Exportar"},
		{"label.key.valid", "Esta clave es válida"},
		{"certificate.import.success.message", "El certificado se ha importado correctamente con el alias \"{0}\"."},
		{"title.signencrypt", "Claves y certificados (cifrado, firmas)"},
		{"keycopy.target.ro.text", "Operación fallida - el archivo de claves del destino está protegido contra escritura."},
		{"label.cert.export", "Certificado de exportación (para el socio)"},
		{"warning.deleteallexpired.expired.but.used.title", "Claves/certificados utilizados"},
		{"tab.info.trustchain", "Trayectoria de certificación"},
		{"warning.deleteallexpired.text", "¿Realmente desea eliminar {0} entradas caducadas y no utilizadas?"},
		{"module.locked.title", "Módulo en uso"},
		{"title.ssl", "Claves y certificados (TLS)"},
		{"tab.info.extension", "Extensiones"},
		{"menu.tools", "Ampliado"},
		{"button.reference", "Mostrar uso"},
		{"menu.tools.importcsr", "Autenticar certificado: Importar la respuesta de la CA a la solicitud de autenticación"},
		{"module.locked.text", "El módulo {0} es utilizado exclusivamente por otro cliente ({1})."},
		{"modifications.notalllowed.message", "No es posible realizar modificaciones"},
		{"keycopy.target.ro.title", "El objetivo es de sólo lectura"},
		{"label.key.import", "Importe su propia clave privada (desde Keystore PKCS#12, JKS)"},
		{"menu.tools.importcsr.renew", "Renovar certificado: Importar la respuesta de la CA a la solicitud de autenticación"},
		{"keycopy.target.exists.title", "La entrada ya existe en el destino"},
		{"generatekey.error.title", "Error durante la generación de la clave"},
		{"label.trustanchor", "Ancla de confianza"},
		{"menu.file", "Archivo"},
		{"button.newkey", "Clave de importación"},
		{"certificate.import.error.title", "Error"},
		{"certificate.import.success.title", "Éxito"},
		{"keystore.readonly.title", "El almacén de claves está protegido contra escritura - no es posible editarlo"},
		{"label.cert.import", "Certificado de importación (del socio)"},
		{"warning.deleteallexpired.title", "Eliminar claves/certificados caducados y no utilizados"},
		{"menu.tools.generatecsr.renew", "Renovar certificado: Generar solicitud de autenticación (a CA)"},
		{"menu.tools.verifyall", "Comprobar las listas de revocación de todos los certificados (CRL)"},
		{"warning.testkey", "Clave de prueba mendelson de acceso público: ¡no utilizar en operaciones productivas!"},
		{"keycopy.target.exists.text", "Esta entrada ya existe en la administración de certificados de destino (alias {0})."},
		{"menu.tools.generatecsr", "Autenticar certificado: Generar solicitud de autenticación (a CA)"},
		{"menu.import", "Importar"},
	};
}
