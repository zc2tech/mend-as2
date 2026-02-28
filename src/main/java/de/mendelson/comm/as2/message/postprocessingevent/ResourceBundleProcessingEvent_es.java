//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent_es.java 2     9/12/24 16:02 Heller $
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
* @version $Revision: 2 $
*/
public class ResourceBundleProcessingEvent_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"eventtype.2", "Envío (defectuoso)"},
		{"eventtype.3", "Recepción"},
		{"event.enqueued", "El evento de postprocesamiento definido ({0}) se ha colocado en la cola y se ejecutará en unos segundos."},
		{"processtype.2", "Reenviar el mensaje al interlocutor"},
		{"processtype.1", "Ejecutar comando en el shell del sistema"},
		{"processtype.3", "Mover mensaje a directorio"},
		{"eventtype.1", "Envío (por orden)"},
	};
}
