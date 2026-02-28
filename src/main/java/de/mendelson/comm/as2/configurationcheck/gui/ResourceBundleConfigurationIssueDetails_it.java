//$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/ResourceBundleConfigurationIssueDetails_it.java 3     21/02/25 16:04 Heller $
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
public class ResourceBundleConfigurationIssueDetails_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.close", "Chiudere"},
		{"button.jumpto.config", "Alla configurazione"},
		{"button.jumpto.generic", "Il problema"},
		{"button.jumpto.keystore", "Alla gestione dei certificati"},
		{"button.jumpto.partner", "Gestione dei partner"},
		{"button.next", "Il prossimo problema >>"},
		{"title", "Problema di configurazione (dettagli) - Problema {0}/{1}"},
	};
}
