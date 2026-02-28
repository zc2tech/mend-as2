//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.datasheet.gui;

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
public class ResourceBundleCreateDataSheet_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.usessl", "Utilizzare TLS"},
		{"label.newpartner", "Nuovo partner - non ancora inserito nel sistema"},
		{"label.receipturl", "Il vostro URL per la ricezione AS2"},
		{"label.remotepartner", "Partner remoto"},
		{"label.usedatasignature", "Utilizzare dati firmati"},
		{"title", "Scheda tecnica per il nuovo collegamento di comunicazione"},
		{"label.usedataencryption", "Utilizzare la crittografia dei dati"},
		{"label.localpartner", "Partner locale"},
		{"label.requestsignedeerp", "Aspettativa firmata EERP"},
		{"button.cancel", "Annullamento"},
		{"label.comment", "Commento"},
		{"label.signedmdn", "Firmato MDN"},
		{"label.compression", "Compressione dei dati"},
		{"label.usesessionauth", "Utilizzare l''autorizzazione di sessione"},
		{"button.ok", ">> Creare la scheda tecnica"},
		{"label.syncmdn", "MDN sincrono"},
		{"file.written", "La scheda tecnica (PDF) è stata scritta dopo \"{0}\". Si prega di inviarlo al nuovo partner per scambiare i dati di confine della comunicazione."},
		{"progress", "Creare PDF"},
		{"label.signature", "Firma digitale"},
		{"label.encryption", "Crittografia"},
		{"label.info", "<HTML><strong>È possibile utilizzare questo dialogo per creare un foglio dati che faciliti la connessione di un nuovo partner.</strong></HTML>"},
	};
}
