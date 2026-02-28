//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.util.httpconfig.gui;

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
public class ResourceBundleDisplayHTTPConfiguration_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.info.configfile", "Questa finestra mostra la configurazione HTTP/S lato server. Il server HTTP fornito ha la versione <strong>jetty {0}</strong>. È possibile configurare i cifrari e i protocolli nel file \"{1}\" del server. Effettuare le impostazioni di base nel file \"{2}\" o direttamente tramite le impostazioni del server. Per rendere effettive le modifiche, riavviare il server."},
		{"no.ssl.enabled", "Il supporto TLS non è stato attivato nel server HTTP sottostante.\nModificare il file di configurazione {0}\nsecondo la documentazione e riavviare il server."},
		{"reading.configuration", "Leggere la configurazione HTTP..."},
		{"tab.protocols", "Protocolli TLS"},
		{"button.ok", "Chiudere"},
		{"tab.cipher", "Cifrari TLS"},
		{"no.embedded.httpserver", "Non è stato avviato il server HTTP sottostante.\nNon sono disponibili informazioni."},
		{"title", "Configurazione HTTP lato server"},
		{"tab.misc", "Generale"},
	};
}
