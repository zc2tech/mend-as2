//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent_it.java 3     9/12/24 16:02 Heller $
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
public class ResourceBundleProcessingEvent_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"eventtype.2", "Spedizione (difettosa)"},
		{"eventtype.3", "Ricezione"},
		{"event.enqueued", "L''evento di post-elaborazione definito ({0}) è stato messo in coda e sarà eseguito tra qualche secondo."},
		{"processtype.2", "Inoltrare il messaggio al partner"},
		{"processtype.1", "Eseguire il comando nella shell di sistema"},
		{"processtype.3", "Spostare il messaggio nella directory"},
		{"eventtype.1", "Spedizione (in ordine)"},
	};
}
