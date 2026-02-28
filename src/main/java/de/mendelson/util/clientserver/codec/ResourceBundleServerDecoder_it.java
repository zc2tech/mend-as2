//$Header: /oftp2/de/mendelson/util/clientserver/codec/ResourceBundleServerDecoder_it.java 3     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.codec;

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
public class ResourceBundleServerDecoder_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"client.incompatible", "Non è stato possibile stabilire una connessione client-server. Il server non è in grado di deserializzare i dati in arrivo dal client. La causa principale è la presenza di versioni diverse del client e del server."},
	};
}
