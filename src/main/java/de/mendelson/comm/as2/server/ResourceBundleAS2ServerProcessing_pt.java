//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleAS2ServerProcessing_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"invalid.request.to", "Foi recebido um pedido inválido. Não será processado porque não existe um cabeçalho as2-to."},
		{"local.station", "Estação local"},
		{"send.failed", "A expedição falhou"},
		{"message.resend.title", "Envio manual de dados numa nova transação"},
		{"server.shutdown", "O utilizador {0} desliga o servidor."},
		{"sync.mdn.sent", "MDN síncrona enviada em resposta a {0}."},
		{"unable.to.process", "Erro durante o processamento no servidor: {0}"},
		{"event.download.not.allowed.subject", "Descarregamento não permitido"},
		{"invalid.request.messageid", "Foi recebido um pedido inválido. Não será processado porque não existe um cabeçalho message-id."},
		{"info.mdn.inboundfiles", "Não foi possível determinar a mensagem AS2 referenciada para a MDN de entrada.\n[MDN de entrada (dados): {0}]\n[MDN de entrada (cabeçalho): {1}]"},
		{"message.resend.oldtransaction", "Esta transação foi novamente enviada manualmente com o novo número de transação [{0}]."},
		{"invalid.request.from", "Foi recebido um pedido inválido. Não será processado porque não existe um cabeçalho as2-from."},
		{"message.resend.newtransaction", "Esta transação é um reenvio da transação [{0}]."},
		{"event.download.not.allowed.body", "Um cliente tentou descarregar um ficheiro, mas tal foi impedido.\nCaminho do pedido de descarregamento: {0}\nDiretórios permitidos: {1}\nUtilizador: {2}\nAnfitrião: {3}"},
	};
}
