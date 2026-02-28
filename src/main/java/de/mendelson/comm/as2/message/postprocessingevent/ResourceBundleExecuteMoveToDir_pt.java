//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToDir_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleExecuteMoveToDir_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"executing.receipt", "[Pós-processamento] ({0} --> {1}) Executar o evento após a receção."},
		{"executing.send", "[Pós-processamento] ({0} --> {1}) Executar evento após o envio."},
		{"messageid.nolonger.exist", "[Pós-processamento] O evento para a mensagem \"{0}\" não pôde ser executado - já não existe. O processo é ignorado..."},
		{"executing.movetodir", "[Pós-processamento] Mover \"{0}\" para \"{1}\"."},
		{"executing.targetdir", "[Pós-processamento] Diretório de destino: \"{0}\"."},
		{"executing.movetodir.success", "[Pós-processamento] Ficheiro movido com sucesso"},
	};
}
