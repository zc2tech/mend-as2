//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBServer_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.database;

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
public class ResourceBundleDBServer_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"update.successfully", "{0}: Il database è stato modificato con successo per la versione richiesta."},
		{"dbserver.shutdown", "Il server del database è stato spento"},
		{"update.error.postgres", "FATAL: Non è possibile modificare il database dalla versione {0} alla versione {1}.\nAvviare pgAdmin ed eliminare il database corrispondente."},
		{"info.jdbc", "JDBC: {0}"},
		{"info.clientdriver", "Driver client: {0}"},
		{"update.error.futureversion", "Il sistema ha trovato una versione futura di {0}. La versione del database supportata da questa versione è {1}, ma il database trovato ha la versione {2}. Non è possibile continuare a lavorare con questo database o modificarlo."},
		{"upgrade.required", "È necessario eseguire un aggiornamento.\nEseguire il file as2upgrade.bat o as2upgrade.sh prima di avviare il server."},
		{"update.progress.version.end", "L''aggiornamento di {1} alla versione {0} è pronto."},
		{"dbserver.running.external", "Il server DB esterno {0} è disponibile"},
		{"update.error.hsqldb", "FATAL: Non è possibile modificare il database dalla versione {0} alla versione {1}.\nCancellare tutti i file AS2_DB_*.* corrispondenti nella directory di installazione.\nIn questo modo tutti i dati definiti dall''utente andranno persi."},
		{"database.1", "Database di configurazione"},
		{"update.progress.version.start", "Avvia l''aggiornamento di {1} alla versione {0}..."},
		{"database.2", "Database di runtime"},
		{"dbserver.running.embedded", "Server DB integrato {0} in esecuzione"},
		{"update.versioninfo", "Aggiornamento automatico del database: La versione del database trovata è {0}, la versione richiesta è {1}."},
		{"info.serveridentification", "Identificazione del server: {0}"},
		{"dbserver.startup", "Avviare il server DB integrato..."},
		{"update.error.mysql", "FATAL: Non è possibile modificare il database dalla versione {0} alla versione {1}.\nAvviare MySQLWorkbench ed eliminare il database corrispondente."},
		{"update.error.oracledb", "FATAL: Non è possibile modificare il database dalla versione {0} alla versione {1}.\nAvviare Oracle SQL Developer ed eliminare il database."},
		{"update.notfound", "Per l''aggiornamento, il file update{0}to{1}.sql e/o il file Update{0}to{1}.class devono esistere nella directory (delle risorse) {2}."},
		{"info.host", "Host: {0}"},
		{"update.progress", "Aggiornamento incrementale del database avviato..."},
		{"info.user", "Utente: {0}"},
		{"module.name", "[DATABASE]"},
	};
}
