//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult_it.java 3     9/12/24 15:50 Hell $
package de.mendelson.util.clientserver.connectiontest.gui;

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
public class ResourceBundleDialogConnectionTestResult_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"description.2", "Il sistema ha eseguito un test di connessione all''indirizzo {0}, porta {1}. Il risultato seguente mostra se la connessione è stata stabilita con successo e se è in esecuzione un server HTTP a questo indirizzo. Anche se il test è riuscito, non è certo che si tratti di un normale server HTTP o di un server AS2. Se si deve utilizzare una connessione TLS (HTTPS) e questa è stata possibile con successo, è possibile scaricare i certificati del partner e importarli nel keystore."},
		{"header.plain", "{0} [Connessione non protetta]"},
		{"description.3", "Il sistema ha eseguito un test di connessione all''indirizzo {0}, porta {1}. Il risultato seguente mostra se la connessione è stata stabilita con successo e se è in esecuzione un server HTTP a questo indirizzo. Anche se il test è riuscito, non è certo che si tratti di un normale server HTTP o di un server AS4. Se si deve utilizzare una connessione TLS (HTTPS) e questa è stata possibile con successo, è possibile scaricare i certificati del partner e importarli nel keystore."},
		{"header.ssl", "{0} [connessione TLS]"},
		{"title", "Risultato del test di connessione"},
		{"button.viewcert", "<HTML>Importa il/i certificato/i &nbsp;</HTML>"},
		{"AVAILABLE", "[PRESENTE]"},
		{"label.connection.established", "La connessione IP semplice è stata stabilita"},
		{"label.certificates.available.local", "I certificati partner (TLS) sono disponibili nel vostro sistema"},
		{"no.certificate.plain", "Non disponibile (connessione non protetta)"},
		{"FAILED", "[ERRORE]"},
		{"label.running.oftpservice", "È stato trovato un servizio OFTP in esecuzione"},
		{"button.close", "Chiudere"},
		{"description.1", "Il sistema ha eseguito un test di connessione all''indirizzo {0}, porta {1}. Il risultato seguente mostra se la connessione è riuscita e se un server OFTP2 è in esecuzione a questo indirizzo. Se è stata utilizzata una connessione TLS ed è andata a buon fine, è possibile scaricare i certificati del partner e importarli nel proprio keystore."},
		{"OK", "[SUCCESSIVO]"},
		{"NOT_AVAILABLE", "[NON DISPONIBILE]"},
		{"used.cipher", "Per il test è stato utilizzato il seguente algoritmo di crittografia: \"{0}\"."},
	};
}
