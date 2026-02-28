//$Header: /as2/de/mendelson/comm/as2/cem/ResourceBundleCEM_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem;

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
public class ResourceBundleCEM_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"TLS.cert.already.imported", "O certificado CEM transmitido já existe no sistema [TLS] (alias {0}), a importação foi ignorada."},
		{"cemtype.response", "A mensagem MCE é do tipo \"Resposta ao certificado\""},
		{"category.3", "SSL"},
		{"cemtype.request", "A mensagem MCE é do tipo \"Pedido de certificado\""},
		{"category.2", "Assinatura"},
		{"state.999", "Erros de processamento"},
		{"category.1", "Encriptação"},
		{"cem.response.relatedrequest.found", "A mensagem CEM refere-se ao pedido \"{0}\""},
		{"ENC_SIGN.cert.imported.success", "O certificado CEM submetido foi importado com sucesso para o sistema [enc/sign] (alias {0})."},
		{"cem.response.prepared", "Mensagem de resposta CEM criada para o pedido {0}"},
		{"cem.structure.info", "Número de pedidos de confiança na estrutura das MCE recebidos: {0}"},
		{"transmitted.certificate.info", "O certificado transmitido tem os parâmetros IssuerDN=\"{0}\" e número de série \"{1}\"."},
		{"cem.validated.schema", "A mensagem CEM recebida foi validada com sucesso."},
		{"state.99", "Processo cancelado"},
		{"TLS.cert.imported.success", "O certificado CEM transmitido foi importado com sucesso para o sistema [TLS] (alias {0})."},
		{"trustrequest.certificates.found", "Número de certificados transferidos: {0}."},
		{"cem.created.request", "O pedido CEM foi gerado para a relação \"{0}\"-\"{1}\". O certificado com os parâmetros issuerDN \"{2}\" e serial number \"{3}\" foi incorporado. A utilização definida é {4}."},
		{"trustrequest.working.on", "Processar o pedido de confiança {0}."},
		{"ENC_SIGN.cert.already.imported", "O certificado CEM transmitido já existe no sistema [enc/sign] (alias {0}), a importação foi ignorada."},
		{"trustrequest.rejected", "A resposta ao pedido de confiança recebido foi definida com o estado \"Rejeitado\"."},
		{"trustrequest.accepted", "A resposta ao pedido de confiança recebido foi definida com o estado \"Aceite\"."},
		{"state.3", "Aceite por {0}"},
		{"state.2", "Rejeitado por {0}"},
		{"state.1", "Ainda não há resposta de {0}"},
	};
}
