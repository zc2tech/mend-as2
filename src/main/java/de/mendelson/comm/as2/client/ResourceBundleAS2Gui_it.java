//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui_it.java 4     8/01/25 16:18 Heller $
package de.mendelson.comm.as2.client;

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
public class ResourceBundleAS2Gui_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"dbconnection.failed.message", "Non è stato possibile stabilire una connessione al server di database AS2: {0}"},
		{"dialog.msg.delete.title", "Cancellare i messaggi"},
		{"buy.license", "Acquista la licenza"},
		{"filter.partner", "Limitazione dei partner"},
		{"uploading.to.server", "Trasferimento al server"},
		{"dialog.resend.title", "Reinvio dei dati"},
		{"menu.file.certificate.ssl", "TLS"},
		{"menu.file.certificates", "Certificati"},
		{"menu.file.cem", "Gestione dello scambio di certificati (CEM)"},
		{"dialog.resend.message", "Si desidera davvero inviare nuovamente la transazione selezionata?"},
		{"tab.welcome", "Notizie e aggiornamenti"},
		{"server.answer.timeout.title", "Timeout della connessione client-server"},
		{"details", "Dettagli del messaggio"},
		{"filter.showstopped", "Mostra interrotta"},
		{"menu.help.about", "Circa"},
		{"welcome", "Benvenuti, {0}"},
		{"resend.failed.unknown.receiver", "Reinvio fallito: destinatario sconosciuto {0} - verificare se questo partner esiste ancora nel sistema."},
		{"menu.file.serverinfo", "Mostra la configurazione del server HTTP"},
		{"delete.msg", "Cancellare"},
		{"dialog.resend.message.multiple", "Si desidera davvero inviare nuovamente le {0} transazioni selezionate?"},
		{"tab.transactions", "Transazioni"},
		{"filter.localstation", "Limitazione della stazione locale"},
		{"dbconnection.failed.title", "Nessuna connessione possibile"},
		{"menu.file.ha", "Istanze di alta disponibilità"},
		{"login.failed.client.incompatible.title", "L''accesso è stato rifiutato"},
		{"menu.file.certificate.signcrypt", "Firma/crittografia"},
		{"fatal.error", "Errore"},
		{"filter.showfinished", "Mostra finita"},
		{"menu.help", "Aiuto"},
		{"menu.file.systemevents", "Eventi di sistema"},
		{"menu.file.resend", "Inviare come nuova transazione"},
		{"filter.direction.outbound", "A partire da"},
		{"menu.file.resend.multiple", "Inviare come nuove transazioni"},
		{"menu.file.migrate.hsqldb", "Migrare da HSQLDB"},
		{"menu.file.cemsend", "Scambio di certificati con i partner (CEM)"},
		{"dialog.msg.delete.message", "Volete davvero eliminare definitivamente i messaggi selezionati?"},
		{"menu.help.forum", "Forum"},
		{"filter.direction", "Restrizione direzionale"},
		{"filter.showpending", "Mostra l''attesa"},
		{"filter.to", "Fino a quando"},
		{"filter.use", "Limitazione di tempo"},
		{"keyrefresh", "Aggiornamento dei certificati"},
		{"menu.help.supportrequest", "Richiesta di supporto"},
		{"filter.none", "-- Nessuno"},
		{"msg.delete.success.single", "{0} Il messaggio è stato cancellato"},
		{"new.version.logentry.2", "È possibile scaricarli all''indirizzo {0}."},
		{"resend.failed.unknown.sender", "Reinvio fallito: mittente sconosciuto {0} - verificare se questo partner esiste ancora nel sistema."},
		{"new.version.logentry.1", "È disponibile una nuova versione."},
		{"logputput.disabled", "** L''output del log è stato soppresso."},
		{"menu.file.partner", "Partner"},
		{"menu.file.statistic", "Statistiche"},
		{"menu.file.certificate", "Certificati"},
		{"login.failed.client.incompatible.message", "Il server segnala che questo client non ha la versione corretta.\nSi prega di utilizzare il client che corrisponde al server."},
		{"filter.from", "Da"},
		{"configurecolumns", "Colonne"},
		{"server.answer.timeout.details", "Il server non risponde entro l''intervallo di tempo definito: il carico è troppo elevato?"},
		{"menu.help.helpsystem", "Sistema di aiuto"},
		{"menu.file.datasheet", "Scheda tecnica per il collegamento"},
		{"refresh.overview", "Aggiornare l''elenco delle transazioni"},
		{"warning.refreshstopped", "L''aggiornamento dell''interfaccia utente è disattivato."},
		{"resend.failed.nopayload", "Reinvio come nuova transazione non riuscito: la transazione selezionata {0} non ha dati utente."},
		{"msg.delete.success.multiple", "{0} I messaggi sono stati cancellati"},
		{"menu.file.quota", "Contingenti"},
		{"menu.file", "File"},
		{"menu.file.send", "Inviare il file al partner"},
		{"filter", "Filtri"},
		{"new.version", "È disponibile una nuova versione. Fare clic qui per scaricarla."},
		{"logputput.enabled", "** L''uscita di registro è stata attivata."},
		{"stoprefresh.msg", "Aggiornamento on/off"},
		{"menu.help.shop", "Negozio online mendelson"},
		{"filter.direction.inbound", "In arrivo"},
		{"menu.file.preferences", "Impostazioni"},
		{"menu.file.exit", "Uscita"},
		{"menu.file.searchinserverlog", "Ricerca nel registro del server"},
                {"no.helpset.for.language", "Siamo spiacenti, non è disponibile un sistema di aiuto per la vostra lingua; verrà utilizzato il sistema di aiuto in inglese." },
	};
}
