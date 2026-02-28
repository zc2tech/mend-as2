//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToPartner_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

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
public class ResourceBundleExecuteMoveToPartner_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"executing.targetpartner", "[Partner di destinazione: \"{0}\"."},
		{"executing.receipt", "({0} --> {1}) Eseguire l''evento dopo la ricezione."},
		{"executing.send", "({0} --> {1}) Eseguire l''evento dopo la spedizione."},
		{"targetpartner.does.not.exist", "[Postprocessing] Il partner di destinazione con identificazione AS2 \"{0}\" non esiste nel sistema..saltare l''esecuzione dell''evento"},
		{"messageid.nolonger.exist", "[Postelaborazione] Non è stato possibile eseguire l''evento di postelaborazione - il messaggio \"{0}\" non esiste più nel sistema..saltare l''esecuzione dell''evento"},
		{"executing.movetopartner", "[Inoltrare il messaggio dal file \"{0}\" al partner di destinazione \"{1}\"."},
		{"executing.movetopartner.success", "[L''ordine di spedizione è stato creato con successo (\"{0}\")."},
	};
}
