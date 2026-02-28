//$Header: /as2/de/mendelson/comm/as2/message/store/ResourceBundleMessageStoreHandler_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message.store;

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
public class ResourceBundleMessageStoreHandler_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"dir.createerror", "Non è stato possibile creare la directory \"{0}\"."},
		{"comm.success", "La comunicazione AS2 è riuscita, i dati utente {0} sono stati spostati in \"{1}\". ({2})"},
		{"outboundstatus.written", "Il file di stato della transazione in uscita è stato scritto in \"{0}\"."},
		{"message.error.raw.stored", "I dati di trasmissione sono stati salvati sotto \"{0}\"."},
		{"message.error.stored", "Il messaggio incorporato è stato salvato sotto \"{0}\"."},
	};
}
