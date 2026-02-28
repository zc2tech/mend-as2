//$Header: /oftp2/de/mendelson/util/mailautoconfig/gui/ResourceBundleMailAutoConfigurationDetection_it.java 3     9/12/24 15:50 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleMailAutoConfigurationDetection_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"header.port", "Porto"},
		{"label.email.hint", "Indirizzo e-mail valido per conoscere le impostazioni del server"},
		{"header.service", "Servizio"},
		{"label.detectedprovider", "<HTML>Il provider di posta riconosciuto è <strong>{0}</strong></HTML>"},
		{"title", "Scoprire le impostazioni del server di posta"},
		{"detection.failed.title", "Riconoscimento fallito"},
		{"email.invalid.text", "Il controllo non è stato eseguito - l''indirizzo e-mail {0} non è valido."},
		{"email.invalid.title", "Indirizzo non valido"},
		{"button.cancel", "Demolizione"},
		{"security.1", "AvvioTLS"},
		{"progress.detection", "Scoprire le impostazioni del server di posta"},
		{"security.0", "Nessuno"},
		{"header.host", "Ospite"},
		{"security.2", "TLS"},
		{"button.ok", "Utilizzare la configurazione selezionata"},
		{"detection.failed.text", "Il sistema non ha trovato le impostazioni del server di posta per l''indirizzo di posta {0}."},
		{"header.security", "Sicurezza"},
		{"button.start.detection", "Scoprire"},
		{"label.mailaddress", "Indirizzo postale"},
	};
}
