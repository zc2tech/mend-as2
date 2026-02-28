//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleDialogSendCEM_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem.gui;

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
public class ResourceBundleDialogSendCEM_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.certificate", "Certificado:"},
		{"purpose.encryption", "Encriptação"},
		{"cem.not.informed", "Os seguintes parceiros não foram informados através do CEM, por favor efectue a troca de certificados por correio eletrónico ou similar: {0}"},
		{"purpose.signature", "Assinatura digital"},
		{"title", "Trocar certificados com parceiros (CEM)"},
		{"label.receiver", "Beneficiário:"},
		{"cem.informed", "Foi feita uma tentativa de informar os seguintes parceiros através do CEM. Verifique o sucesso na administração do CEM: {0}"},
		{"label.activationdate", "Data de ativação:"},
		{"cem.request.failed", "O pedido CEM não pôde ser executado:\n{0}"},
		{"purpose.ssl", "TLS"},
		{"button.cancel", "Cancelar"},
		{"cem.request.title", "Troca de certificados via CEM"},
		{"button.ok", "Ok"},
		{"label.initiator", "Estação local:"},
		{"partner.cem.hint", "Os sistemas parceiros devem suportar CEM para serem incluídos aqui"},
		{"cem.request.success", "O pedido CEM foi executado com sucesso."},
		{"partner.all", "--Todos os parceiros..."},
	};
}
