//$Header: /as2/de/mendelson/comm/as2/client/manualsend/ResourceBundleManualSend_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.client.manualsend;

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
public class ResourceBundleManualSend_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.testdata", "Enviar dados de teste"},
		{"label.partner", "Recetor"},
		{"send.success", "O ficheiro foi transferido com sucesso para o processo de expedição."},
		{"button.cancel", "Cancelar"},
		{"label.selectfile", "Selecione o ficheiro a enviar"},
		{"label.filename.hint", "Ficheiro para enviar ao seu parceiro"},
		{"label.localstation", "Transmissor"},
		{"button.browse", "Navegar"},
		{"button.ok", "Ok"},
		{"send.failed", "Devido a um erro, o ficheiro não pôde ser transferido para o processo de envio."},
		{"title", "Envio manual de ficheiros"},
		{"label.filename", "Enviar ficheiro"},
	};
}
