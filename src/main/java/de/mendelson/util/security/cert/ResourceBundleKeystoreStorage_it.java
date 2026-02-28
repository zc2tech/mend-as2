//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleKeystoreStorage_it.java 3     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleKeystoreStorage_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"error.delete.notloaded", "Non è stato possibile eliminare la voce, perché il keystore sottostante non è ancora stato caricato."},
		{"error.nodata", "Non è stato possibile leggere il keystore: Nessun dato disponibile"},
		{"moved.keystore.to.db.title", "Importazione di un file keystore ({0})"},
		{"error.readaccess", "Impossibile leggere il keystore: Non è possibile accedere in lettura a \"{0}\"."},
		{"moved.keystore.reason.commandline", "L''importazione è stata attivata da un parametro della riga di comando all''avvio del server."},
		{"error.save.notloaded", "Il Keystore non può essere salvato, non è ancora stato caricato."},
		{"moved.keystore.to.db", "Importare i dati del keystore da \"{0}\" nel sistema - l''uso previsto è {1}. Tutte le chiavi/certificati esistenti sono stati eliminati."},
		{"error.save", "Non è stato possibile salvare i dati del keystore."},
		{"error.empty", "Non è stato possibile leggere il keystore: I dati del keystore devono essere più lunghi di 0."},
		{"keystore.read.failure", "Il sistema non è riuscito a leggere i certificati sottostanti. Messaggio di errore: \"{0}\". Verificare se si sta utilizzando la password corretta per il keystore."},
		{"moved.keystore.reason.initial", "L''importazione è stata eseguita perché attualmente non esiste una memoria di chiavi interna al sistema. Si tratta di un processo iniziale."},
		{"error.filexists", "Non è stato possibile leggere il keystore: Il file keystore \"{0}\" non esiste."},
		{"error.notafile", "Non è stato possibile leggere il keystore: Il file keystore \"{0}\" non è un file."},
	};
}
