//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.datasheet.gui;

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
public class ResourceBundleCreateDataSheet_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.usessl", "Utilizar TLS"},
		{"label.newpartner", "Novo parceiro - ainda não está no sistema"},
		{"label.receipturl", "O seu URL para receção AS2"},
		{"label.remotepartner", "Parceiro remoto"},
		{"label.usedatasignature", "Utilizar dados assinados"},
		{"title", "Folha de dados para a nova ligação de comunicação"},
		{"label.usedataencryption", "Utilizar a encriptação de dados"},
		{"label.localpartner", "Parceiro local"},
		{"label.requestsignedeerp", "Esperar a assinatura do PEER"},
		{"button.cancel", "Cancelar"},
		{"label.comment", "Comentário"},
		{"label.signedmdn", "Assinado MDN"},
		{"label.compression", "Compressão de dados"},
		{"label.usesessionauth", "Utilizar a autenticação de sessão"},
		{"button.ok", ">> Criar ficha de dados"},
		{"label.syncmdn", "MDN síncrona"},
		{"file.written", "A folha de dados (PDF) foi escrita depois de \"{0}\". Envie-a ao seu novo parceiro para trocar os dados de fronteira da comunicação."},
		{"progress", "Criar PDF"},
		{"label.signature", "Assinatura digital"},
		{"label.encryption", "Encriptação"},
		{"label.info", "<HTML><strong>Pode utilizar esta caixa de diálogo para criar uma folha de dados que facilite a ligação de um novo parceiro.</strong></HTML>"},
	};
}
