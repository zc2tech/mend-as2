//$Header: /oftp2/de/mendelson/util/mailautoconfig/gui/ResourceBundleMailAutoConfigurationDetection_es.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.mailautoconfig.gui;

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
public class ResourceBundleMailAutoConfigurationDetection_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.port", "Puerto"},
		{"label.email.hint", "Dirección de correo electrónico válida para conocer la configuración del servidor"},
		{"header.service", "Servicio"},
		{"label.detectedprovider", "<HTML>El proveedor de correo reconocido es <strong>{0}</strong></HTML>"},
		{"title", "Averiguar la configuración del servidor de correo"},
		{"detection.failed.title", "Reconocimiento fallido"},
		{"email.invalid.text", "La comprobación no se ha realizado: la dirección de correo {0} no es válida."},
		{"email.invalid.title", "Dirección no válida"},
		{"button.cancel", "Demolición"},
		{"security.1", "StartTLS"},
		{"progress.detection", "Averiguar la configuración del servidor de correo"},
		{"security.0", "Ninguno"},
		{"header.host", "Anfitrión"},
		{"security.2", "TLS"},
		{"button.ok", "Utilizar la configuración seleccionada"},
		{"detection.failed.text", "El sistema no ha podido encontrar la configuración del servidor de correo para la dirección de correo {0}."},
		{"header.security", "Seguridad"},
		{"button.start.detection", "Averígualo"},
		{"label.mailaddress", "Dirección postal"},
	};
}
