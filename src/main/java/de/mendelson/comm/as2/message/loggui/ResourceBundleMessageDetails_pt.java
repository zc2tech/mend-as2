//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_pt.java 3     17/01/25 10:06 Heller $
package de.mendelson.comm.as2.message.loggui;

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
* @version $Revision: 3 $
*/
public class ResourceBundleMessageDetails_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transactionstate.error.connectionrefused", "<HTML>Tentou contactar o sistema do parceiro. A tentativa falhou ou o seu parceiro não respondeu com uma confirmação dentro do tempo definido.</HTML>"},
		{"header.timestamp", "data"},
		{"transactiondetails.outbound.insecure", "Esta é uma ligação de saída não segura, está a enviar dados para o parceiro \"{0}\"."},
		{"transactiondetails.outbound.sync", " A confirmação é recebida diretamente como uma resposta no canal de retorno da ligação de saída (MDN síncrona)."},
		{"header.useragent", "Servidor AS2"},
		{"transactionstate.error.authentication-failed", "<HTML>O destinatário da mensagem não conseguiu verificar com êxito a assinatura do remetente nos dados. Trata-se normalmente de um problema de configuração, uma vez que o remetente e o destinatário devem utilizar o mesmo certificado. Por favor, dê também uma vista de olhos aos detalhes MDN no registo - isto pode conter mais informações.</HTML>"},
		{"title", "Detalhes da mensagem"},
		{"transactionstate.error.messagecreation.details", "<HTML>O sistema não conseguiu gerar a estrutura de mensagem necessária devido a um problema do seu lado. Isto não tem nada a ver com o sistema do seu parceiro, não foi estabelecida qualquer ligação.</HTML>"},
		{"message.raw.decrypted", "Dados de transmissão (não encriptados)"},
		{"transactionstate.error.asyncmdnsend", "<HTML>Uma mensagem com um pedido assíncrono de MDN foi recebida e processada com êxito, mas o seu sistema não pôde devolver a MDN assíncrona ou esta não foi aceite pelo sistema parceiro.</HTML>"},
		{"transactionstate.error.connectionrefused.details", "<HTML>Pode tratar-se de um problema de infraestrutura, o sistema do seu parceiro não está a funcionar ou introduziu o URL de receção errado na configuração? Se os dados foram transmitidos e o seu parceiro não os confirmou, poderá ter definido um período de tempo demasiado curto para a confirmação?</HTML>"},
		{"transactionstate.ok.receive", "<HTML>A mensagem {0} foi recebida com êxito pelo parceiro \"{1}\". Foi enviada uma confirmação correspondente para o parceiro.</HTML>"},
		{"title.cem", "Detalhes da mensagem da troca de certificados (CEM)"},
		{"header.encryption", "Encriptação"},
		{"header.messageid", "Número de referência"},
		{"transactionstate.error.unexpected-processing-error", "<HTML>Esta é uma mensagem de erro muito genérica. Por uma razão desconhecida, o destinatário não conseguiu processar a mensagem.</HTML>"},
		{"transactionstate.error.in", "<HTML>Recebeu com sucesso a mensagem {0} do seu parceiro \"{1}\" - mas o seu sistema não a conseguiu processar e respondeu com o erro [{2}]</HTML>"},
		{"transactionstate.ok.details", "<HTML>Os dados foram transferidos e a transação foi concluída com êxito</HTML>"},
		{"message.payload.multiple", "Dados do utilizador ({0})"},
		{"transactionstate.ok.send", "<HTML>A mensagem {0} foi enviada com sucesso para o parceiro \"{1}\" - este enviou uma confirmação correspondente.</HTML>"},
		{"transactiondetails.outbound.secure", "Esta é uma ligação segura de saída, está a enviar dados para o parceiro \"{0}\"."},
		{"transactionstate.error.unknown", "Ocorreu um erro desconhecido."},
		{"transactiondetails.inbound.async", " A confirmação é enviada através do estabelecimento de uma nova ligação com o parceiro (MDN assíncrona)."},
		{"transactionstate.error.decryption-failed", "<HTML>O destinatário da mensagem não conseguiu desencriptar a mensagem. Isto é normalmente um problema de configuração, o remetente está a utilizar o certificado correto para encriptação?</HTML>"},
		{"message.header", "Dados do cabeçalho"},
		{"transactionstate.error.messagecreation", "<HTML>Ocorreu um problema durante a geração de uma mensagem AS2 de saída</HTML>"},
		{"header.senderhost", "Transmissor"},
		{"transactiondetails.inbound.secure", "Esta é uma ligação segura de entrada, está a receber dados do parceiro \"{0}\"."},
		{"transactionstate.error.insufficient-message-security", "<HTML>O destinatário da mensagem esperava um nível de segurança mais elevado para os dados recebidos (por exemplo, dados encriptados em vez de dados não encriptados)</HTML>"},
		{"transactiondetails.outbound.async", " O seu parceiro estabelece uma nova ligação consigo para a confirmação (MDN assíncrona)."},
		{"transactionstate.error.asyncmdnsend.details", "<HTML>O remetente da mensagem AS2 transmite o URL para o qual deve devolver a MDN - ou este sistema não está acessível (problema de infraestrutura ou o sistema parceiro está em baixo?) ou o sistema parceiro não aceitou a MDN assíncrona e respondeu com um HTTP 400.</HTML>"},
		{"transactionstate.pending", "Esta transação está em estado de espera."},
		{"transactionstate.error.decompression-failed", "<HTML>O destinatário da mensagem não conseguiu descomprimir a mensagem recebida</HTML>"},
		{"button.ok", "Ok"},
		{"transactionstate.error.unknown-trading-partner", "<HTML>O utilizador e o seu parceiro têm identificadores AS2 diferentes para os dois parceiros da transmissão na configuração. Foram utilizados os seguintes identificadores: \"{0}\" (remetente da mensagem), \"{1}\" (destinatário da mensagem)</HTML>"},
		{"transactiondetails.inbound.insecure", "Esta é uma ligação de entrada não segura, está a receber dados do parceiro \"{0}\"."},
		{"message.payload", "Dados do utilizador"},
		{"transactiondetails.inbound.sync", " A confirmação é enviada diretamente como resposta no canal de retorno da ligação de entrada (MDN síncrona)."},
		{"transactionstate.error.out", "<HTML>Enviou com sucesso a mensagem {0} ao seu parceiro \"{1}\" - mas ele não a conseguiu processar e respondeu com o erro [{2}]</HTML>"},
		{"tab.log", "Registo desta instância de mensagem"},
		{"header.signature", "Assinatura digital"},
	};
}
