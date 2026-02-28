//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportCertificate_es.java 2     9/12/24 15:51 Heller $
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
* @version $Revision: 2 $
*/
public class ResourceBundleExportCertificate_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error.empty.certificate", "No se dispone de datos del certificado"},
		{"button.browse", "Visite"},
		{"label.exportfile", "Nombre del archivo"},
		{"title", "Exportar certificado X.509"},
		{"SSH2", "Formato SSH2 (clave pública, *.pub)"},
		{"certificate.export.success.message", "El certificado se ha podido exportar correctamente a\n\"{0}\""},
		{"DER", "Formato binario (DER, *.cer)"},
		{"button.cancel", "Cancelar"},
		{"certificate.export.success.title", "Éxito"},
		{"filechooser.certificate.export", "Seleccione el nombre de archivo para la exportación."},
		{"button.ok", "Ok"},
		{"label.alias", "Alias"},
		{"label.exportfile.hint", "Archivo de certificado generado"},
		{"label.exportformat", "Formato"},
		{"certificate.export.error.title", "Error de exportación"},
		{"PEM", "Formato de texto (PEM, *.cer)"},
		{"certificate.export.error.message", "La exportación del certificado ha fallado:\n{0}"},
		{"PEM_CHAIN", "Formato de texto (+cadena de autenticación) (PEM, *.pem)"},
		{"PKCS#7", "Con cadena de certificación (PKCS#7, *.p7b)"},
	};
}
