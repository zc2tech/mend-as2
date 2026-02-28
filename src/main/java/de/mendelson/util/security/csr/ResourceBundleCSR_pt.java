//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSR_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.csr;

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
public class ResourceBundleCSR_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"csr.generation.failure.message", "{0}"},
		{"cancel", "Cancelar"},
		{"label.selectcsrfile", "Selecione o ficheiro para guardar o CSR"},
		{"csr.message.storequestion.renew", "Deseja que a chave seja renovada na AC mendelson\nou guardar o pedido num ficheiro?"},
		{"csr.option.1.renew", "Renovar em mendelson CA"},
		{"csr.title.renew", "Renovar certificado: Pedido de assinatura de certificado (CSR)"},
		{"csrresponse.import.failure.title", "Problema ao aplicar o patch na chave"},
		{"ca.connection.problem", "HTTP {0}: A CA mendelson não está atualmente disponível. Por favor, tente novamente mais tarde."},
		{"csr.message.storequestion", "Deseja que a chave seja autenticada pela CA mendelson\nou guardar o pedido num ficheiro?"},
		{"csr.generation.success.title", "A CSR foi criada com sucesso"},
		{"csr.generation.failure.title", "Erros durante a criação de CSR"},
		{"csr.generation.success.message", "O pedido de autenticação gerado foi guardado no ficheiro\nficheiro \"{0}\".\nEnvie estes dados para a sua AC.\nRecomendamos a CA mendelson (http://ca.mendelson-e-c.com)."},
		{"label.selectcsrrepsonsefile", "Selecionar o ficheiro de resposta da AC"},
		{"csrresponse.import.success.title", "Sucesso"},
		{"csrresponse.import.failure.message", "{0}"},
		{"csr.title", "Autenticar certificado: Pedido de assinatura de certificado (CSR)"},
		{"csr.option.2", "Guardar no ficheiro"},
		{"csr.option.1", "Notarização em mendelson CA"},
		{"csrresponse.import.success.message", "A chave foi corrigida com sucesso com a resposta da CA."},
	};
}
