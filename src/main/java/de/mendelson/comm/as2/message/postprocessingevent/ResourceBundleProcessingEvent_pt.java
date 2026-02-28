//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleProcessingEvent_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"eventtype.2", "Envio (com defeito)"},
		{"eventtype.3", "Receção"},
		{"event.enqueued", "O evento de pós-processamento definido ({0}) foi colocado na fila de espera e será executado dentro de alguns segundos."},
		{"processtype.2", "Enviar mensagem ao parceiro"},
		{"processtype.1", "Executar comando na shell do sistema"},
		{"processtype.3", "Mover a mensagem para o diretório"},
		{"eventtype.1", "Expedição (por ordem)"},
	};
}
