//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToPartner_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleExecuteMoveToPartner_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"executing.targetpartner", "[Pós-processamento] Parceiro de destino: \"{0}\"."},
		{"executing.receipt", "[Pós-processamento] ({0} --> {1}) Executar o evento após a receção."},
		{"executing.send", "[Pós-processamento] ({0} --> {1}) Executar evento após o envio."},
		{"targetpartner.does.not.exist", "[Pós-processamento] O parceiro de destino com a identificação AS2 \"{0}\" não existe no sistema...ignorar a execução do evento"},
		{"messageid.nolonger.exist", "[Pós-processamento] O evento de pós-processamento não pôde ser executado - a mensagem \"{0}\" já não existe no sistema...ignorar execução do evento"},
		{"executing.movetopartner", "[Reencaminhar mensagem do ficheiro \"{0}\" para o parceiro de destino \"{1}\"."},
		{"executing.movetopartner.success", "[Pós-processamento] A ordem de expedição foi criada com êxito (\"{0}\")."},
	};
}
