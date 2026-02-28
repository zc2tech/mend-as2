//$Header: /as4/de/mendelson/util/systemevents/ResourceBundleSystemEvent_it.java 3     15/01/25 10:18 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEvent_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"type.100000", "Non specificato"},
		{"category.400", "Certificato"},
		{"type.406", "Scambio di certificati (richiesta in entrata)"},
		{"type.802", "Quota raggiunta"},
		{"type.407", "Certificato (importazione del keystore)"},
		{"type.404", "Scambio di certificati"},
		{"type.800", "Quota"},
		{"type.405", "Scadenza del certificato"},
		{"type.801", "Quota raggiunta"},
		{"type.402", "Certificato (alias modificato)"},
		{"type.403", "Certificato (cancellato)"},
		{"category.800", "Contingente"},
		{"type.400", "certificato"},
		{"type.401", "Certificato (aggiunto)"},
		{"type.300", "Transazione"},
		{"category.1000", "Elaborazione dei dati"},
		{"category.1400", "Interfaccia XML"},
		{"type.1202", "Creare una directory"},
		{"type.1201", "File (eliminare)"},
		{"type.1200", "Operazione di file"},
		{"type.1204", "File (copia)"},
		{"type.1203", "File (spostamento)"},
		{"category.500", "Database"},
		{"category.100", "Componente server"},
		{"type.705", "Partner (aggiunto)"},
		{"type.703", "Partner (modificato)"},
		{"origin.2", "Utenti"},
		{"type.704", "Partner (cancellato)"},
		{"origin.3", "Transazione"},
		{"type.701", "Modifica della configurazione"},
		{"type.305", "Transazione (annullamento)"},
		{"type.702", "Controllo della configurazione"},
		{"type.306", "Transazione (reinvio)"},
		{"origin.1", "Sistema"},
		{"type.303", "Transazione (messaggio duplicato)"},
		{"category.900", "Notifica"},
		{"category.100000", "Altro"},
		{"type.700", "Configurazione"},
		{"type.304", "Transazione (eliminare)"},
		{"type.301", "Errore di transazione"},
		{"type.302", "Transazione (riconsegna rifiutata)"},
		{"type.200", "Connessione"},
		{"type.201", "Test di connessione"},
		{"category.1100", "Attivazione"},
		{"category.1500", "Interfaccia REST"},
		{"type.1102", "Scadenza della licenza"},
		{"type.1101", "Aggiornamento della licenza"},
		{"type.1100", "Licenza"},
		{"type.1503", "Cancellare il certificato"},
		{"type.1502", "Configurazione del certificato"},
		{"type.1501", "Aggiungi certificato"},
		{"type.1500", "REST"},
		{"type.1507", "Inviare l''ordine"},
		{"type.1506", "Cancellare il partner"},
		{"type.1505", "Configurazione del partner"},
		{"type.1504", "Aggiungi partner"},
		{"category.200", "Connessione"},
		{"type.101", "Avvio del server"},
		{"type.102", "Server in funzione"},
		{"type.100", "Spegnimento del server"},
		{"severity.1", "Info"},
		{"severity.2", "Avvertenze"},
		{"severity.3", "Errore"},
		{"type.1000", "Elaborazione dei dati"},
		{"category.1200", "Operazione di file"},
		{"type.1400", "XML"},
		{"type.1002", "Post-elaborazione"},
		{"type.1001", "Pre-elaborazione"},
		{"type.1402", "Configurazione del partner"},
		{"type.1401", "Configurazione del certificato"},
		{"type.112", "Spegnimento del server TRFC"},
		{"type.113", "Avvio dello scheduler"},
		{"type.110", "Server TRFC in funzione"},
		{"type.199", "Componente server"},
		{"type.111", "Stato del server TRFC"},
		{"category.700", "Configurazione"},
		{"category.300", "Transazione"},
		{"type.901", "Notifica (invio riuscito)"},
		{"type.109", "Avvio del server TRFC"},
		{"type.902", "Notifica (invio non riuscito)"},
		{"type.503", "Database (inizializzazione)"},
		{"type.107", "Server HTTP in esecuzione"},
		{"type.900", "Notifica"},
		{"type.108", "Spegnimento del server HTTP"},
		{"type.501", "Creazione del database"},
		{"type.105", "Server DB spento"},
		{"type.502", "Database (Aggiornamento)"},
		{"type.106", "Avvio del server HTTP"},
		{"type.103", "Avvio del server DB"},
		{"type.500", "Database"},
		{"type.104", "Server DB in esecuzione"},
		{"category.1300", "Funzionamento del cliente"},
		{"type.1301", "Accesso utente (successo)"},
		{"type.1300", "Cliente"},
		{"type.1303", "Separazione degli utenti"},
		{"type.1302", "Accesso utente (fallito)"},
		{"type.116", "Monitoraggio della directory (stato modificato)"},
		{"type.117", "Porta di ricezione"},
		{"type.114", "Scheduler in funzione"},
		{"type.115", "Spegnimento dello scheduler"},
                {"type." + SystemEvent.TYPE_DATABASE_ROLLBACK, "Rollback transazione"},
	};
}
