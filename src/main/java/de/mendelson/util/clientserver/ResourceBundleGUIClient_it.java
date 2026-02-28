//$Header: /oftp2/de/mendelson/util/clientserver/ResourceBundleGUIClient_it.java 3     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver;

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
public class ResourceBundleGUIClient_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"error", "Problema: {0}"},
		{"logout.from.server", "È stato eseguito un logout dal server"},
		{"password.required", "Errore di accesso, è richiesta una password per l''utente {0}."},
		{"login.failure", "Accesso come utente \"{0}\" fallito"},
		{"client.received.unprocessed.message", "Il server ha inviato un messaggio che non è stato elaborato dal client: {0}"},
		{"connectionrefused.message", "{0}: Nessuna connessione possibile. Assicurarsi che il server sia in funzione."},
		{"login.failed.client.incompatible.title", "L''accesso è stato rifiutato"},
		{"connection.closed.title", "Disconnessione locale"},
		{"connection.closed.message", "La connessione client-server locale è stata scollegata dal server."},
		{"connection.closed", "La connessione client-server locale è stata scollegata dal server."},
		{"connection.success", "Cliente connesso a {0}"},
		{"login.failed.client.incompatible.message", "Il server segnala che questo client non ha la versione corretta.\nSi prega di utilizzare il client che corrisponde al server."},
		{"login.success", "Collegato come utente \"{0}\""},
		{"connectionrefused.title", "Problema di connessione"},
	};
}
