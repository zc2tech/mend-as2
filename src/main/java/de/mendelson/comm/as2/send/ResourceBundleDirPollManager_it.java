//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_it.java 5     6/02/25 8:23 Heller $
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
public class ResourceBundleDirPollManager_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"warning.ro", "[Il file di output {0} è protetto da scrittura, questo file viene ignorato."},
		{"warning.notcomplete", "[Il file sorgente {0} non è ancora completamente disponibile, il file viene ignorato."},
		{"title.list.polls.stopped", "Le seguenti attività di monitoraggio sono state interrotte"},
		{"processing.file", "Elaborare il file \"{0}\" per la relazione \"{1}/{2}\"."},
		{"none", "Nessuno"},
		{"warning.noread", "[Monitoraggio directory] Non è possibile accedere in lettura al file sorgente {0}, il file viene ignorato."},
		{"poll.started", "È stato avviato il monitoraggio della directory per la relazione \"{0}/{1}\". Ignora: \"{2}\". Intervallo: {3}s"},
		{"poll.modified", "[Le impostazioni del partner per la relazione \"{0}/{1}\" sono state modificate."},
		{"title.list.polls.started", "Sono stati avviati i seguenti programmi di monitoraggio"},
		{"poll.stopped.notscheduled", "[Il sistema ha cercato di interrompere il monitoraggio della directory \"{0}/{1}\", ma non c''è stato alcun monitoraggio."},
		{"processing.file.error", "Errore di elaborazione del file \"{0}\" per la relazione \"{1}/{2}\": \"{3}\"."},
		{"title.list.polls.running", "Riepilogo delle directory monitorate:"},
		{"poll.log.polling", "[Monitoraggio directory] {0}->{1}: controlla la directory \"{2}\" per i nuovi file"},
		{"manager.status.modified", "Il monitoraggio delle directory è cambiato monitoraggio delle directory, {0} directory sono monitorate"},
		{"poll.stopped", "Il monitoraggio della directory per la relazione \"{0}/{1}\" è stato interrotto."},
		{"poll.log.wait", "[Monitoraggio directory] {0}->{1}: Prossimo processo di polling in {2}s ({3})"},
		{"messagefile.deleted", "Il file \"{0}\" è stato eliminato e trasferito nella coda di elaborazione del server."},
	};
}
