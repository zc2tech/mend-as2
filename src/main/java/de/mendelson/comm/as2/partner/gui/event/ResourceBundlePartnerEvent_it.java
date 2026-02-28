//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent_it.java 5     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui.event;

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
public class ResourceBundlePartnerEvent_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"process.executeshell", "Esecuzione di un comando di shell"},
		{"title.configuration.shell", "Configurazione del comando shell [Partner {0}, {1}]"},
		{"shell.hint.samples", "<HTML><strong>Esempi</strong><br>"
			+"Windows: <i>cmd /c sposta \"$'{'nome del file}\" \"c:\\directory di destinazione\"</i>.<br>"
			+"Linux: <i>mv \"$'{'filename}\" \"~/target directory/\"</i></HTML>"},
		{"tab.newprocess", "Processi disponibili per la post-elaborazione"},
		{"label.shell.command", "comando ({0}):"},
		{"shell.hint.replacement.3", "<HTML>Le seguenti variabili vengono sostituite dai valori di sistema in questo comando prima che venga eseguito:<br>"
			+"<i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
		{"shell.hint.replacement.2", "<HTML>Le seguenti variabili vengono sostituite dai valori di sistema in questo comando prima che venga eseguito:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject}, $'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"shell.hint.replacement.1", "<HTML>Le seguenti variabili vengono sostituite dai valori di sistema in questo comando prima che venga eseguito:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject}, $'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"process.movetodirectory.description", "Spostare i dati in un''altra directory"},
		{"process.executeshell.description", "Eseguire un comando di shell o uno script per post-elaborare i dati."},
		{"title.configuration.movetodir", "Sposta i messaggi nella directory [Partner {0}, {1}]."},
		{"label.movetodir.remotedir.select", "Selezionare la directory di destinazione sul server"},
		{"label.movetopartner.info", "<HTML>Selezionare l''interlocutore remoto a cui inoltrare il messaggio.</HTML>"},
		{"process.movetopartner", "Inoltro ai partner"},
		{"process.movetopartner.description", "Inoltro a un partner, ad esempio dalla DMZ al sistema ERP."},
		{"label.movetodir.info", "<HTML>Impostare la directory lato server in cui il messaggio deve essere spostato.</HTML>"},
		{"type.3", "dopo il ricevimento"},
		{"type.2", "dopo l''invio (errore)"},
		{"button.cancel", "Demolizione"},
		{"label.shell.info", "<HTML>Impostare il comando di shell da eseguire in questo caso. Ricordate che si tratta di un comando specifico del sistema operativo, che verrà reindirizzato alla shell predefinita del sistema operativo.</HTML>"},
		{"type.1", "dopo l''invio (successo)"},
		{"label.movetodir.targetdir", "Directory di destinazione ({0}):"},
		{"title.select.process", "Selezionare un nuovo processo come evento ({0})"},
		{"button.ok", "Ok"},
		{"process.movetodirectory", "Spostare nella directory"},
		{"title.configuration.movetopartner", "Inoltro dei dati ad un partner [Partner {0}, {1}]"},
		{"label.movetopartner.noroutingpartner", "<HTML>Non c''è nessun partner remoto disponibile nel sistema a cui inviare i messaggi. Si prega di aggiungere prima un partner a cui inviare i messaggi.</HTML>"},
		{"label.movetopartner", "Partner di riferimento:"},
	};
}
