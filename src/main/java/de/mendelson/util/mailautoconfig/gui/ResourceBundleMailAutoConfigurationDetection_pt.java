//$Header: /oftp2/de/mendelson/util/mailautoconfig/gui/ResourceBundleMailAutoConfigurationDetection_pt.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.mailautoconfig.gui;

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
public class ResourceBundleMailAutoConfigurationDetection_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.port", "Porto"},
		{"label.email.hint", "Endereço de correio eletrónico válido para saber as definições do servidor"},
		{"header.service", "Serviço"},
		{"label.detectedprovider", "<HTML>O fornecedor de correio eletrónico reconhecido é <strong>{0}</strong></HTML>"},
		{"title", "Descobrir as definições do servidor de correio eletrónico"},
		{"detection.failed.title", "O reconhecimento falhou"},
		{"email.invalid.text", "A verificação não foi efectuada - o endereço de correio {0} é inválido."},
		{"email.invalid.title", "Endereço inválido"},
		{"button.cancel", "Demolição"},
		{"security.1", "IniciarTLS"},
		{"progress.detection", "Descobrir as definições do servidor de correio eletrónico"},
		{"security.0", "Nenhum"},
		{"header.host", "Anfitrião"},
		{"security.2", "TLS"},
		{"button.ok", "Utilizar a configuração selecionada"},
		{"detection.failed.text", "O sistema não conseguiu encontrar as definições do servidor de correio eletrónico para o endereço de correio {0}."},
		{"header.security", "Segurança"},
		{"button.start.detection", "Descobrir"},
		{"label.mailaddress", "Endereço postal"},
	};
}
