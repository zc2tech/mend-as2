//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_it.java 4     17/01/25 10:06 Heller $
package de.mendelson.comm.as2.message.loggui;

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
* @version $Revision: 4 $
*/
public class ResourceBundleMessageDetails_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transactionstate.error.connectionrefused", "<HTML>Si è tentato di contattare il sistema del partner. Il tentativo non è andato a buon fine oppure il partner non ha risposto con una conferma entro il tempo stabilito.</HTML>"},
		{"header.timestamp", "data"},
		{"transactiondetails.outbound.insecure", "Si tratta di una connessione in uscita non protetta, che invia dati al partner \"{0}\"."},
		{"transactiondetails.outbound.sync", " Si riceve la conferma direttamente come risposta sul canale di ritorno della connessione in uscita (MDN sincrono)."},
		{"header.useragent", "Server AS2"},
		{"transactionstate.error.authentication-failed", "<HTML>Il destinatario del messaggio non è riuscito a verificare la firma del mittente nei dati. In genere si tratta di un problema di configurazione, poiché il mittente e il destinatario devono utilizzare lo stesso certificato. Si consiglia di consultare anche i dettagli MDN nel log, che potrebbero contenere ulteriori informazioni.</HTML>"},
		{"title", "Dettagli del messaggio"},
		{"transactionstate.error.messagecreation.details", "<HTML>Il sistema non ha potuto generare la struttura del messaggio richiesta a causa di un problema dal vostro lato. Questo non ha nulla a che fare con il sistema del partner, non è stata stabilita alcuna connessione.</HTML>"},
		{"message.raw.decrypted", "Dati di trasmissione (non criptati)"},
		{"transactionstate.error.asyncmdnsend", "<HTML>È stato ricevuto ed elaborato con successo un messaggio con una richiesta di MDN asincrono, ma il vostro sistema non è stato in grado di restituire l''MDN asincrono o non è stato accettato dal sistema partner.</HTML>"},
		{"transactionstate.error.connectionrefused.details", "<HTML>Potrebbe trattarsi di un problema di infrastruttura, il sistema del vostro partner non funziona affatto o avete inserito un URL di ricezione sbagliato nella configurazione? Se i dati sono stati trasmessi e il partner non li ha confermati, è possibile che abbiate impostato una finestra temporale di conferma troppo breve?</HTML>"},
		{"transactionstate.ok.receive", "<HTML>Il messaggio {0} è stato ricevuto con successo dal partner \"{1}\". Al partner è stata inviata una conferma corrispondente.</HTML>"},
		{"title.cem", "Dettagli del messaggio sullo scambio di certificati (CEM)"},
		{"header.encryption", "Crittografia"},
		{"header.messageid", "Numero di riferimento"},
		{"transactionstate.error.unexpected-processing-error", "<HTML>Questo è un messaggio di errore molto generico. Per un motivo sconosciuto, il destinatario non è stato in grado di elaborare il messaggio.</HTML>"},
		{"transactionstate.error.in", "<HTML>Hai ricevuto con successo il messaggio {0} dal tuo partner \"{1}\" - ma il tuo sistema non è stato in grado di elaborarlo e ha risposto con l''errore [{2}].</HTML>"},
		{"transactionstate.ok.details", "<HTML>I dati sono stati trasferiti e la transazione è stata completata con successo.</HTML>"},
		{"message.payload.multiple", "Dati utente ({0})"},
		{"transactionstate.ok.send", "<HTML>Il messaggio {0} è stato inviato con successo al partner \"{1}\", che ha inviato una conferma corrispondente.</HTML>"},
		{"transactiondetails.outbound.secure", "Si tratta di una connessione sicura in uscita, in cui si inviano dati al partner \"{0}\"."},
		{"transactionstate.error.unknown", "Si è verificato un errore sconosciuto."},
		{"transactiondetails.inbound.async", " La conferma viene inviata stabilendo una nuova connessione con il partner (MDN asincrono)."},
		{"transactionstate.error.decryption-failed", "<HTML>Il destinatario del messaggio non è stato in grado di decifrare il messaggio. Di solito si tratta di un problema di configurazione: il mittente sta usando il certificato corretto per la crittografia?</HTML>"},
		{"message.header", "Dati di intestazione"},
		{"transactionstate.error.messagecreation", "<HTML>Si è verificato un problema durante la generazione di un messaggio AS2 in uscita</HTML>"},
		{"header.senderhost", "Trasmettitore"},
		{"transactiondetails.inbound.secure", "Si tratta di una connessione sicura in entrata, si stanno ricevendo dati dal partner \"{0}\"."},
		{"transactionstate.error.insufficient-message-security", "<HTML>Il destinatario del messaggio si aspettava un livello di sicurezza più elevato per i dati ricevuti (ad esempio, dati criptati invece di dati non criptati).</HTML>"},
		{"transactiondetails.outbound.async", " Per conferma, l''interlocutore stabilisce una nuova connessione con l''utente (MDN asincrona)."},
		{"transactionstate.error.asyncmdnsend.details", "<HTML>Il mittente del messaggio AS2 trasmette l''URL a cui deve restituire l''MDN - o questo sistema non è accessibile (problema di infrastruttura o il sistema partner è fuori uso?) o il sistema partner non ha accettato l''MDN asincrono e ha risposto con un HTTP 400.</HTML>"},
		{"transactionstate.pending", "Questa transazione è in stato di attesa."},
		{"transactionstate.error.decompression-failed", "<HTML>Il destinatario del messaggio non ha potuto decomprimere il messaggio ricevuto.</HTML>"},
		{"button.ok", "Ok"},
		{"transactionstate.error.unknown-trading-partner", "<HTML>L''utente e il suo partner hanno diversi identificatori AS2 per i due partner della trasmissione nella configurazione. Sono stati utilizzati i seguenti identificatori: \"{0}\" (mittente del messaggio), \"{1}\" (destinatario del messaggio)</HTML>"},
		{"transactiondetails.inbound.insecure", "Si tratta di una connessione non protetta in entrata, si stanno ricevendo dati dal partner \"{0}\"."},
		{"message.payload", "Dati utente"},
		{"transactiondetails.inbound.sync", " La conferma viene inviata direttamente come risposta sul canale di ritorno della connessione in entrata (MDN sincrono)."},
		{"transactionstate.error.out", "<HTML>Hai inviato con successo il messaggio {0} al tuo partner \"{1}\" - ma lui non è stato in grado di elaborarlo e ha risposto con l''errore [{2}].</HTML>"},
		{"tab.log", "Registro di questa istanza di messaggio"},
		{"header.signature", "Firma digitale"},
	};
}
