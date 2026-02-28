//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleJettyStarter_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleJettyStarter_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"httpserver.startup.problem", "Problema all''inizio ({0})"},
		{"userconfiguration.readerror", "Problema di lettura della configurazione utente di {0}: {1} ... Ignorare la configurazione utente e avviare il server web utilizzando i valori predefiniti definiti"},
		{"httpserver.running", "Server HTTP integrato in esecuzione ({0})"},
		{"deployment.failed", "[{0}] non è stato fornito: {1}"},
		{"userconfiguration.setvar", "Impostare il valore definito dall''utente [{0}] su [{1}]."},
		{"userconfiguration.reading", "Leggere la configurazione definita dall''utente da {0}"},
		{"httpserver.willstart", "Avvio del server HTTP integrato"},
		{"tls.keystore.reloaded", "Le modifiche sono state registrate nel keystore TLS e i dati del keystore del server HTTP sono stati aggiornati."},
		{"module.name", "[JETTY]"},
		{"httpserver.stopped", "Arresto del server HTTP integrato"},
		{"deployment.success", "[{0}] è stato distribuito con successo"},
		{"listener.started", "Attendere le connessioni in arrivo {0}"},
	};
}
