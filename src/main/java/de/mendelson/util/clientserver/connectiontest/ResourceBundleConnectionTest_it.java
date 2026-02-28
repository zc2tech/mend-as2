//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/ResourceBundleConnectionTest_it.java 3     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.connectiontest;

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
public class ResourceBundleConnectionTest_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"test.connection.proxy.auth", "La connessione utilizza il proxy {0} con autenticazione (utente \"{1}\")"},
		{"certificates.found.details", "Certificato [{0}/{1}]: {2}"},
		{"sni.extension.set", "Il nome host per l''estensione SNI TLS è stato impostato su \"{0}\"."},
		{"test.connection.proxy.noauth", "La connessione utilizza il proxy {0} senza autenticazione"},
		{"wrong.protocol.hint", "O il partner si aspetta una connessione non protetta, o c''è un problema di protocollo o richiede l''autenticazione del cliente."},
		{"certificate.ca", "Certificato CA"},
		{"certificate.does.exist.local", "Questo certificato esiste già nel keystore TLS locale, l''alias è \"{0}\"."},
		{"connection.problem", "{0} non è raggiungibile - potrebbe trattarsi di un problema di infrastruttura o di dati non corretti immessi"},
		{"service.found.failure", "Errore: non è stato trovato alcun servizio OFTP in esecuzione a {0}"},
		{"certificate.selfsigned", "Autografato"},
		{"remote.service.identification", "Identificazione del servizio del server remoto: \"{0}\"."},
		{"test.start.plain", "Avvia il controllo della connessione per {0}, PLAIN..."},
		{"connection.success", "La connessione a {0} è stata stabilita con successo"},
		{"wrong.protocol", "Il protocollo trovato è \"{0}\", questa non è una connessione sicura. Si è tentato di connettersi a questo partner utilizzando uno dei protocolli [{1}]. Tuttavia, il partner non offre nessuno di questi protocolli di sicurezza sulla porta e sull''indirizzo indicati."},
		{"tag", "Test di connessione a {0}"},
		{"certificate.does.not.exist.local", "Questo certificato non è ancora presente nel keystore TLS locale: importarlo."},
		{"result.exception", "Durante il test si è verificato il seguente errore: {0}."},
		{"local.station", "Stazione locale"},
		{"certificates.found", "{0} I certificati sono stati trovati e scaricati"},
		{"protocol.information", "Il protocollo utilizzato è stato identificato come \"{0}\"."},
		{"info.securityprovider", "Provider di sicurezza TLS utilizzato: {0}"},
		{"certificate.enduser", "Certificato dell''utente finale"},
		{"exception.occured", "Si è verificato un problema durante il test di connessione: [{0}] {1}"},
		{"requesting.certificates", "I certificati del server remoto vengono scaricati"},
		{"test.start.ssl", "Avvia la verifica della connessione a {0}, TLS. Si noti che questo test si basa su ogni certificato del server, quindi anche se il test ha successo, non significa che il keystore TLS sia configurato correttamente."},
		{"info.protocols", "Il client consente la negoziazione tramite i seguenti protocolli TLS: {0}"},
		{"timeout.set", "Impostare il timeout su {0}ms"},
		{"test.connection.direct", "Viene utilizzata una connessione IP diretta"},
		{"exception.occured.oftpservice", "Non è stato possibile identificare alcun server OFTP2 in funzione all''indirizzo e alla porta indicati. Potrebbe trattarsi di un problema temporaneo, ad esempio il server OFTP2 remoto non è in esecuzione, ma i dati dell''indirizzo sono corretti. Si è verificato il seguente problema: [{0}] {1}"},
		{"service.found.success", "Successo: è stato trovato un servizio OFTP in esecuzione a {0}"},
		{"check.for.service.oftp2", "Verificare l''esecuzione del servizio OFTP2..."},
	};
}
