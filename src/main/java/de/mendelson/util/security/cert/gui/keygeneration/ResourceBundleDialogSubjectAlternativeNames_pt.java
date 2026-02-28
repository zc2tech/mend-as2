//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames_pt.java 2     9/12/24 15:51 H $
package de.mendelson.util.security.cert.gui.keygeneration;

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
public class ResourceBundleDialogSubjectAlternativeNames_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.cancel", "Demolição"},
		{"label.add", "Adicionar"},
		{"header.name", "Tipo"},
		{"label.del", "Eliminar"},
		{"button.ok", "Ok"},
		{"title", "Gerir nomes alternativos de candidatos"},
		{"header.value", "Valor"},
		{"info", "É possível utilizar esta caixa de diálogo para gerir os nomes alternativos de candidatos para o processo de geração de chaves (nome alternativo do sujeito). Estes valores são uma extensão do certificado x.509. Se o seu parceiro suportar isto, pode introduzir aqui domínios adicionais para a sua chave, por exemplo. No OFTP2, dependendo do parceiro, pode ser necessário preencher alguns campos com dados de identificação, por exemplo, o Odette Id do seu sistema como um URL no formato \"oftp://OdetteId\" e novamente o seu domínio no campo DNS name."},
	};
}
