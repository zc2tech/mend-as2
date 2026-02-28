//$Header: /oftp2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleCSRUtil_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"response.verification.failed", "A cadeia de confiança da resposta CSR não pôde ser verificada: {0}"},
		{"verification.failed", "A verificação do pedido de assinatura de certificado (CSR) criado falhou."},
		{"missing.cert.in.trustchain", "Os certificados da cadeia de autenticação no sistema (certificado raiz e intermédio) estão em falta para esta operação.\nReceberá estes certificados da sua CA.\nPrimeiro, importe o certificado com os dados-chave (emissor)\n{0}."},
		{"no.certificates.in.reply", "A chave não pôde ser corrigida, não foram encontrados certificados na resposta da AC."},
		{"response.chain.incomplete", "A cadeia de confiança da resposta CSR está incompleta."},
		{"response.public.key.does.not.match", "Esta resposta da CA não corresponde a esta chave."},
	};
}
