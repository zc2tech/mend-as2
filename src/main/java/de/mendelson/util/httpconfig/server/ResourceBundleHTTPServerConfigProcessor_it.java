//$Header: /as2/de/mendelson/util/httpconfig/server/ResourceBundleHTTPServerConfigProcessor_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.util.httpconfig.server;

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
public class ResourceBundleHTTPServerConfigProcessor_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"http.serverstateurl", "Visualizza lo stato del server:"},
		{"webapp.as2api.war", "mendelson AS2 API REST"},
		{"external.ip.error", "IP esterno: -Impossibile determinarlo-."},
		{"webapp._unknown", "Servlet sconosciuto"},
		{"info.cipher.howtochange", "Per disabilitare determinati cifrari per le connessioni in entrata, modificare il file di configurazione del server HTTP incorporato ({0}) con un editor di testo. Cercare la stringa di caratteri <Set name=\"ExcludeCipherSuites\">, aggiungere il cifrario da escludere e riavviare il programma."},
		{"webapp.as4api.war", "mendelson AS4 API REST"},
		{"info.cipher", "I seguenti cifrari sono supportati dal server HTTP sottostante sul lato di ingresso.\nQuali sono supportati dipende dalla VM Java in uso (attualmente {1}).\nÈ possibile disattivare i singoli cifrari nel file di configurazione\nFile di configurazione \"{0}\"."},
		{"http.receipturls", "Ricezione completa degli URL della configurazione corrente"},
		{"webapp.oftp2api.war", "mendelson API REST OFTP2"},
		{"http.server.config.tlskey.none", "Chiave TLS: Non è stata definita alcuna chiave TLS, le connessioni TLS in entrata non sono possibili!"},
		{"external.ip", "IP esterno: {0} / {1}"},
		{"webapp.webas2.war", "Monitoraggio web del server AS2 mendelson"},
		{"webapp.as4.war", "mendelson AS4 che riceve il servlet"},
		{"info.protocols", "I seguenti protocolli sono supportati dal server HTTP sottostante per le connessioni in entrata.\nQuali sono supportati dipende dalla VM Java in uso (attualmente {1}). Il provider di sicurezza TLS utilizzato è {2}.\nÈ possibile disattivare i singoli protocolli nel file di configurazione\nFile di configurazione \"{0}\"."},
		{"http.server.config.listener", "La porta {0} ({1}) è legata all''adattatore di rete {2}."},
		{"webapp.as2.war", "mendelson AS2 che riceve servlet"},
		{"http.deployedwars", "WAR attualmente disponibili nel server HTTP (funzionalità servlet):"},
		{"webapp.as2-sample.war", "esempi di API AS2 di mendelson"},
		{"webapp.as4-sample.war", "esempi di API AS4 di mendelson"},
		{"http.server.config.clientauthentication", "Il server richiede l''autenticazione del client TLS: {0}"},
		{"info.protocols.howtochange", "Per disattivare determinati protocolli sul lato di ingresso, modificare il file di configurazione del server HTTP incorporato ({0}) con un editor di testo. Cercare la stringa di caratteri <Set name=\"ExcludeProtocols\">, aggiungere il protocollo da escludere e riavviare il programma."},
		{"http.server.config.tlskey.info", "Chiave TLS:\n	Alias [{0}]\n	Impronta digitale SHA1 [{1}]\n	Numero di serie [{2}]\n	Valido fino a [{3}]\n"},
	};
}
