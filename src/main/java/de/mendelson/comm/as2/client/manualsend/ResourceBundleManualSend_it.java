//$Header: /as2/de/mendelson/comm/as2/client/manualsend/ResourceBundleManualSend_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.client.manualsend;

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
public class ResourceBundleManualSend_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"label.testdata", "Inviare i dati di test"},
		{"label.partner", "Ricevitore"},
		{"send.success", "Il file è stato trasferito con successo al processo di spedizione."},
		{"button.cancel", "Annullamento"},
		{"label.selectfile", "Selezionare il file da inviare"},
		{"label.filename.hint", "File da inviare al partner"},
		{"label.localstation", "Trasmettitore"},
		{"button.browse", "Sfogliare"},
		{"button.ok", "Ok"},
		{"send.failed", "A causa di un errore, non è stato possibile trasferire il file al processo di spedizione."},
		{"title", "Invio manuale dei file"},
		{"label.filename", "Inviare il file"},
	};
}
