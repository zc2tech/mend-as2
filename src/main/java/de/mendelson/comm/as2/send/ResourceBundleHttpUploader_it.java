//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleHttpUploader_it.java 5     6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
public class ResourceBundleHttpUploader_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"strict.hostname.check.skipped.selfsigned", "TLS: il controllo del nome host rigoroso è stato saltato - il server remoto utilizza un certificato autofirmato."},
		{"sending.msg.sync", "Invia un messaggio AS2 a {0}, si aspetta un MDN sincrono per la conferma di ricezione."},
		{"sending.cem.sync", "Inviare il messaggio CEM a {0}, attendere la conferma di ricezione da parte di MDN sincrono."},
		{"answer.no.sync.empty", "La conferma di ricezione sincrona ricevuta è vuota. Probabilmente si è verificato un problema nell''elaborazione dei messaggi AS2 da parte del vostro partner."},
		{"error.httpupload", "Trasmissione fallita, il server AS2 remoto segnala \"{0}\"."},
		{"sending.cem.async", "Invia un messaggio CEM a {0}, aspetta un MDN asincrono per la conferma di ricezione su {1}."},
		{"answer.no.sync.mdn", "La risposta sincrona ricevuta non è nel formato corretto. Poiché i problemi di struttura di MDN sono insoliti, potrebbe essere che questa non sia una risposta del sistema AS2 a cui si stava cercando di rivolgersi, ma forse la risposta di un proxy o la risposta di un sito web standard? Mancano i seguenti valori di intestazione HTTP: [{0}].\nI dati ricevuti iniziano con le seguenti strutture:\n{1}"},
		{"hint.SSLPeerUnverifiedException", "Nota:\nIl problema si è verificato durante l''handshake TLS. Il sistema non è stato quindi in grado di stabilire una connessione sicura con il vostro partner; il problema non ha nulla a che fare con il protocollo AS2.\nVerificare quanto segue:\n*Avete importato tutti i certificati del vostro partner nel vostro keystore TLS (per TLS, compresi i certificati intermedi/root)?\n*Il vostro partner ha importato tutti i certificati da voi (per TLS, inclusi i certificati intermedi/root)?"},
		{"hint.ConnectTimeoutException", "Nota:\nIn genere si tratta di un problema di infrastruttura che non ha nulla a che vedere con il protocollo AS2. Non è possibile stabilire una connessione in uscita con il partner.\nPer risolvere il problema, verificare quanto segue:\n*Si dispone di una connessione Internet attiva?\n*Controllare se è stato inserito l''URL di ricezione corretto del partner nell''amministrazione del partner.\n*Contattare il partner, forse il suo sistema AS2 non è disponibile?"},
		{"trust.all.server.certificates", "La connessione TLS in uscita si affiderà a tutti i certificati del server remoto se i certificati root e intermedi sono disponibili."},
		{"sending.mdn.async", "Invia una conferma di ricezione asincrona (MDN) a {0}."},
		{"using.proxy", "Utilizzare il proxy {0}:{1}."},
		{"returncode.ok", "Messaggio inviato con successo (HTTP {0}); {1} trasmesso in {2} [{3} KB/s]."},
		{"connection.tls.info", "Connessione TLS in uscita stabilita [{0}, {1}]"},
		{"error.http502", "Problema di connessione, non è stato possibile trasferire i dati. (HTTP 502 - GATEWAY ERRATO)"},
		{"returncode.accepted", "Messaggio inviato con successo (HTTP {0}); {1} trasmesso in {2} [{3} KB/s]."},
		{"error.http503", "Problema di connessione, non è stato possibile trasferire i dati. (HTTP 503 - SERVIZIO NON DISPONIBILE)"},
		{"strict.hostname.check", "Per la connessione TLS in uscita, viene effettuato un controllo rigoroso del nome host in relazione al certificato del server."},
		{"error.http504", "Problema di connessione, non è stato possibile trasferire i dati. (HTTP 504 - TIMEOUT DEL GATEWAY)"},
		{"using.proxy.auth", "Utilizzare il proxy {0}:{1} (autenticazione come {2})."},
		{"error.noconnection", "Problema di connessione, non è stato possibile trasferire i dati."},
		{"hint.httpcode.signals.problem", "Nota:\nÈ stata stabilita una connessione all''host del partner, dove è in esecuzione un server Web.\nIl server remoto segnala che qualcosa non va nel percorso o nella porta della richiesta e restituisce il codice HTTP {0}.\nPer ulteriori informazioni su questo codice HTTP, utilizzare un motore di ricerca su Internet."},
		{"hint.SSLException", "Nota:\nIn genere si tratta di un problema di negoziazione a livello di protocollo. Il partner ha rifiutato la connessione.\nO il partner si aspetta una connessione sicura (HTTPS) e voi volevate stabilire una connessione non sicura o viceversa.\nÈ anche possibile che il partner richieda una versione TLS diversa o un algoritmo di crittografia diverso da quello offerto."},
		{"sending.msg.async", "Invia un messaggio AS2 a {0}, aspetta un MDN asincrono per la conferma di ricezione su {1}."},
	};
}
