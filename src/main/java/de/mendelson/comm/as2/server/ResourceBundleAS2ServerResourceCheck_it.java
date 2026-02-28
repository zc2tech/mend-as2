//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerResourceCheck_it.java 3     9/12/24 16:03 Heller $
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
public class ResourceBundleAS2ServerResourceCheck_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"port.in.use", "La porta {0} è occupata da un altro processo."},
		{"warning.low.maxheap", "Il sistema ha trovato solo circa {0} memoria heap disponibile allocata al processo server AS2 di mendelson. (Non preoccupatevi, è circa il 10% in meno di quanto specificato nello script di avvio). Si prega di allocare almeno 1 GB di memoria heap al processo server mendelson AS2."},
		{"warning.few.cpucores", "Il sistema ha riconosciuto solo {0} core di processore assegnati al processo server mendelson AS2. Con un numero così basso di core del processore, la velocità di esecuzione potrebbe essere molto bassa e alcune funzioni potrebbero funzionare solo in modo limitato. Assegnare almeno 4 core di processore al processo server mendelson AS2."},
	};
}
