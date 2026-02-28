//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.util.clientserver.log.search.gui;

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
public class ResourceBundleDialogSearchLogfile_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"no.data.uid", "**Non ci sono dati di log per il numero definito dall''utente \"{0}\" nel periodo di tempo selezionato. Selezionare il numero completo definito dall''utente che è stato assegnato alla trasmissione come stringa di ricerca."},
		{"label.uid", "Identificazione definita dall''utente"},
		{"label.enddate", "Fine"},
		{"problem.serverside", "Si è verificato un problema sul lato server durante la ricerca nei file di log: [{0}] {1}"},
		{"no.data.mdnid", "**Non ci sono dati di log per il numero MDN \"{0}\" nel periodo di tempo selezionato. Utilizzare il numero MDN completo, che si trova nel log di una trasmissione, come stringa di ricerca."},
		{"title", "Ricerca nelle voci di registro del server"},
		{"label.search", "<html><div style=\"text-align:center\">Log<br>search</div></html>"},
		{"label.startdate", "Inizio"},
		{"textfield.preset", "Numero di messaggio AS2, numero MDN o identificazione definita dall''utente"},
		{"button.close", "Chiudere"},
		{"label.messageid", "Numero del messaggio"},
		{"no.data.messageid", "**Non ci sono dati di log per il messaggio AS2 numero \"{0}\" nel periodo di tempo selezionato. Utilizzare il numero completo del messaggio come stringa di ricerca."},
		{"label.info", "<html>Definire un periodo di tempo, inserire il numero completo di un messaggio AS2 o il numero completo di un MDN per trovare tutte le voci di registro sul server - quindi premere il pulsante \"Cerca nel registro\". È possibile definire il numero definito dall''utente per ogni transazione quando si inviano i dati al server in esecuzione tramite la riga di comando.</html"},
		{"label.mdnid", "Numero MDN"},
	};
}
