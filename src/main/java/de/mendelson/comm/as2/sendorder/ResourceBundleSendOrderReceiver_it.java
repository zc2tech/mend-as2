//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_it.java 4     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.sendorder;

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
* @version $Revision: 4 $
*/
public class ResourceBundleSendOrderReceiver_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"async.mdn.wait", "Attendere l''MDN asincrono fino a {0}."},
		{"as2.send.disabled", "** Il numero di connessioni parallele in uscita è impostato su 0 - il sistema non invierà né messaggi MDN né AS2. Si prega di modificare questa impostazione nelle impostazioni del server se si desidera inviare messaggi **"},
		{"max.retry.reached", "Raggiunto il numero massimo di tentativi ({0}), la transazione viene terminata."},
		{"outbound.connection.prepare.mdn", "Preparare la connessione MDN in uscita a \"{0}\", connessioni attive: {1}/{2}."},
		{"outbound.connection.prepare.message", "Preparare la connessione di messaggi AS2 in uscita verso \"{0}\", connessioni attive: {1}/{2}."},
		{"send.connectionsstillopen", "Il numero di connessioni in uscita è stato ridotto a {0}, ma attualmente ci sono ancora {1} connessioni in uscita."},
		{"retry", "Provare una nuova trasmissione dopo {0}s, ripetere {1}/{2}."},
	};
}
