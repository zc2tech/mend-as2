//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesAS2_it.java 5     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

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
* @version $Revision: 5 $
*/
public class ResourceBundlePreferencesAS2_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"country", "Paese"},
		{"showquotaconf", "Mostra la quota nella gestione dei partner"},
		{"proxyport", "Porta proxy HTTP"},
		{"retrycount", "Numero di tentativi di connessione"},
		{"colorblindness", "Supporto per il daltonismo"},
		{"set.to", "è stato impostato su"},
		{"notification.setting.updated", "Le impostazioni di notifica sono state modificate."},
		{"showhttpheaderconf", "Visualizzare la gestione delle intestazioni HTTP nel client"},
		{"checkrevocationlist", "Controllare gli elenchi di revoca dei certificati"},
		{"autologdirdeleteolderthan", "Pulire la directory dei log (più vecchia di)"},
		{"autoimportpartnertlscertificates", "Importazione automatica dei certificati TLS dei partner modificati"},
		{"stricthostcheck", "(TLS) Controllare l''host"},
		{"autostatsdelete", "Cancellare automaticamente i vecchi dati statistici"},
		{"cem", "Utilizzare il CEM"},
		{"language", "Lingua del cliente"},
		{"setting.updated", "L''impostazione è stata aggiornata"},
		{"jetty.http.port", "Porta di ingresso HTTP"},
		{"proxypass", "Dati di accesso al proxy HTTP (password)"},
		{"commed", "Edizione comunitaria"},
		{"embeddedhttpserverrequestlog", "Registro delle richieste del server HTTP integrato"},
		{"proxyuser", "Dati di accesso al proxy HTTP (utente)"},
		{"maxoutboundconnections", "Numero massimo di connessioni simultanee in uscita"},
		{"proxyuseauth", "Utilizzare i dati di accesso del proxy HTTP"},
		{"outboundstatusfile", "Creare un file di stato per ogni transazione"},
		{"module.name", "[IMPOSTAZIONI]"},
		{"proxyuse", "Utilizzare il proxy HTTP per la connessione in uscita"},
		{"automsgdeletelog", "Cancellare le vecchie transazioni (voce di registro)"},
		{"retrywaittime", "Ristabilire la connessione ogni n secondi"},
		{"automsgdeleteolderthanmults", "Cancellare le vecchie transazioni (unità di tempo in s)"},
		{"autostatsdeleteolderthan", "Cancellare le statistiche (più vecchie di)"},
		{"autologdirdelete", "Pulizia automatica della directory di log"},
		{"logpollprocess", "Documentare il processo di sondaggio nel registro"},
		{"showoverwritelocalstationsecurity", "Visualizzazione: Sovrascrivere la sicurezza della stazione locale"},
		{"TRUE", "acceso"},
		{"asyncmdntimeout", "Timeout per MDN async in minuti"},
		{"httpsendtimeout", "Timeout di invio (HTTP/S)"},
		{"jetty.connectionlimit.maxConnections", "Numero massimo di connessioni simultanee in entrata"},
		{"lastupdatecheck", "Ultimo controllo della nuova versione (ora unix)"},
		{"receiptpartnersubdir", "Utilizzare una sottodirectory per ogni partner"},
		{"setting.reset", "L''impostazione del server [{0}] è stata ripristinata al valore predefinito."},
		{"dirmsg", "Directory di base per i messaggi"},
		{"jetty.ssl.port", "Porta di ingresso HTTPS"},
		{"FALSE", "spento"},
		{"automsgdeleteolderthan", "Cancellare le vecchie transazioni (più vecchie di)"},
		{"automsgdelete", "Cancellare automaticamente le vecchie transazioni"},
		{"datasheetreceipturl", "Ricevere l''URL per la scheda tecnica"},
		{"proxyhost", "Host proxy HTTP"},
		{"trustallservercerts", "(TLS) Fiducia in tutti i certificati dei server remoti"},
	};
}
