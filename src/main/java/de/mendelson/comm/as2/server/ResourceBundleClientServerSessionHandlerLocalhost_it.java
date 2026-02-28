//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleClientServerSessionHandlerLocalhost_it.java 3     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleClientServerSessionHandlerLocalhost_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"allowallclients.true", "**Il server AS22 accetta richieste di client AS2 da altri host."},
		{"allowallclients.false", "**Il server AS2 accetta solo richieste di client locali**"},
		{"only.localhost.clients", "Il server remoto può accettare solo connessioni da localhost. Per modificare questo comportamento, avviarlo con l''opzione \"-allowallclients\"."},
	};
}
