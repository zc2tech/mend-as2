//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing_it.java 3     9/12/24 16:03 Heller $
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
public class ResourceBundleAS2ServerProcessing_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"invalid.request.to", "È stata ricevuta una richiesta non valida. Non verrà elaborata perché non c''è l''intestazione as2-to."},
		{"local.station", "Stazione locale"},
		{"send.failed", "Spedizione fallita"},
		{"message.resend.title", "Invio manuale dei dati in una nuova transazione"},
		{"server.shutdown", "L''utente {0} chiude il server."},
		{"sync.mdn.sent", "MDN sincrono inviato in risposta a {0}."},
		{"unable.to.process", "Errore durante l''elaborazione sul server: {0}"},
		{"event.download.not.allowed.subject", "Download non consentito"},
		{"invalid.request.messageid", "È stata ricevuta una richiesta non valida. Non verrà elaborata perché manca l''intestazione message-id."},
		{"info.mdn.inboundfiles", "Non è stato possibile determinare il messaggio AS2 di riferimento per l''MDN ricevuto.\n[MDN in arrivo (dati): {0}]\n[MDN in arrivo (intestazione): {1}]"},
		{"message.resend.oldtransaction", "Questa transazione è stata nuovamente inviata manualmente con il nuovo numero di transazione [{0}]."},
		{"invalid.request.from", "È stata ricevuta una richiesta non valida. Non verrà elaborata perché non c''è l''intestazione as2-from."},
		{"message.resend.newtransaction", "Questa transazione è un reinvio della transazione [{0}]."},
		{"event.download.not.allowed.body", "Un cliente ha cercato di scaricare un file, ma l''operazione è stata impedita.\nPercorso della richiesta di download: {0}\nDirectory consentite: {1}\nUtente: {2}\nHost: {3}"},
	};
}
