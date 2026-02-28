//$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/ResourceBundleConfigurationIssueDetails_es.java 3     21/02/25 16:04 Heller $
package de.mendelson.comm.as2.configurationcheck.gui;

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
public class ResourceBundleConfigurationIssueDetails_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.close", "Cerrar"},
		{"button.jumpto.config", "A la configuración"},
		{"button.jumpto.generic", "El problema"},
		{"button.jumpto.keystore", "A la gestión de certificados"},
		{"button.jumpto.partner", "Gestión de socios"},
		{"button.next", "Siguiente problema >>"},
		{"title", "Problema de configuración (detalles) - Problema {0}/{1}"},
	};
}
