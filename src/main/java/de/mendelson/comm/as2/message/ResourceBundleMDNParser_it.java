//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleMDNParser_it.java 3     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.message;

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
public class ResourceBundleMDNParser_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"invalid.mdn.nocontenttype", "L''MDN in arrivo non è valido: nessun tipo di contenuto definito."},
		{"structure.failure.mdn", "È stato riconosciuto un MDN in arrivo. Purtroppo la struttura dell''MDN non è corretta (\"{0}\") e quindi non è stato possibile elaborarlo. La transazione associata non ha cambiato stato."},
	};
}
