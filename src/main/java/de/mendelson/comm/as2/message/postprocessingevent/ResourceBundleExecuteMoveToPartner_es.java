//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToPartner_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleExecuteMoveToPartner_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"executing.targetpartner", "[Post-proceso] Socio objetivo: \"{0}\"."},
		{"executing.receipt", "[Post-procesamiento] ({0} --> {1}) Ejecutar evento tras su recepción."},
		{"executing.send", "[Post-proceso] ({0} --> {1}) Ejecutar evento después del envío."},
		{"targetpartner.does.not.exist", "[Postproceso] El socio de destino con la identificación AS2 \"{0}\" no existe en el sistema..omitir ejecución de evento"},
		{"messageid.nolonger.exist", "[Post-procesamiento] El evento de post-procesamiento no pudo ser ejecutado - el mensaje \"{0}\" ya no existe en el sistema..omitir ejecución de evento"},
		{"executing.movetopartner", "[Reenviar mensaje del fichero \"{0}\" al interlocutor de destino \"{1}\"."},
		{"executing.movetopartner.success", "[Postproceso] La orden de envío se ha creado correctamente (\"{0}\")."},
	};
}
