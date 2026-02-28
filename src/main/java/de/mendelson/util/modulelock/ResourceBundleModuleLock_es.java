//$Header: /oftp2/de/mendelson/util/modulelock/ResourceBundleModuleLock_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.modulelock;

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
public class ResourceBundleModuleLock_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"Partner management", "Gestión de socios"},
		{"TLS keystore", "Gestión de certificados (TLS)"},
		{"configuration.locked.otherclient", "El módulo {0} está abierto exclusivamente por otro cliente,\nActualmente no puede realizar ningún cambio.\nDatos del otro cliente:\nIP: {1}\nUsuario: {2}\nId de proceso: {3}"},
		{"ENC/SIGN keystore", "Gestión de certificados (cifrado/firma)"},
		{"configuration.changed.otherclient", "Es posible que otro cliente haya realizado cambios en el módulo {0}.\nVuelva a abrir esta interfaz de configuración para recargar la configuración actual."},
		{"Server settings", "Configuración del servidor"},
		{"modifications.notallowed.message", "Por el momento no es posible realizar cambios"},
	};
}
