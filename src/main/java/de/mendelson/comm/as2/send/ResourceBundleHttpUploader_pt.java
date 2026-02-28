//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleHttpUploader_pt.java 4     6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
* @version $Revision: 4 $
*/
public class ResourceBundleHttpUploader_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"strict.hostname.check.skipped.selfsigned", "TLS: A verificação rigorosa do nome do anfitrião foi ignorada - o servidor remoto utiliza um certificado auto-assinado."},
		{"sending.msg.sync", "Enviar mensagem AS2 para {0}, esperar MDN síncrona para acusar a receção."},
		{"sending.cem.sync", "Enviar mensagem CEM para {0}, esperar que a MDN síncrona confirme a receção."},
		{"answer.no.sync.empty", "O aviso de receção síncrono recebido está vazio. É provável que tenha havido um problema no processamento das mensagens AS2 do lado do seu parceiro - contacte-o em conformidade."},
		{"error.httpupload", "A transmissão falhou, o servidor AS2 remoto reporta \"{0}\"."},
		{"sending.cem.async", "Enviar mensagem CEM para {0}, esperar MDN assíncrona para confirmação de receção em {1}."},
		{"answer.no.sync.mdn", "A confirmação síncrona recebida não está no formato correto. Uma vez que os problemas de estrutura da MDN são invulgares, pode ser que esta não seja uma resposta do sistema AS2 que estava a tentar endereçar, mas talvez a resposta de um proxy ou a resposta de um site padrão? Os seguintes valores de cabeçalho HTTP estão em falta: [{0}].\nOs dados recebidos começam com as seguintes estruturas:\n{1}"},
		{"hint.SSLPeerUnverifiedException", "Nota:\nEste problema ocorreu durante o aperto de mão TLS. O sistema não conseguiu, portanto, estabelecer uma ligação segura com o seu parceiro, o problema não tem nada a ver com o protocolo AS2.\nPor favor, verifique o seguinte:\n*Importou todos os certificados do seu parceiro para o seu repositório de chaves TLS (para TLS, incluindo certificados intermédios/raiz)?\n*O seu parceiro importou todos os seus certificados (para TLS, incluindo certificados intermédios/raiz)?"},
		{"hint.ConnectTimeoutException", "Nota:\nTrata-se normalmente de um problema de infraestrutura que não tem nada a ver com o protocolo AS2. Não é possível estabelecer uma ligação de saída com o seu parceiro.\nPara resolver o problema, verifique os seguintes pontos:\n*Tem uma ligação à Internet ativa?\n*Por favor, verifique se introduziu o URL de receção correto do seu parceiro na administração de parceiros?\n*Por favor, contacte o seu parceiro, talvez o sistema AS2 dele não esteja disponível?"},
		{"trust.all.server.certificates", "A ligação TLS de saída confiará em todos os certificados do servidor remoto se os certificados raiz e intermédio estiverem disponíveis."},
		{"sending.mdn.async", "Envia um aviso de receção assíncrono (MDN) para {0}."},
		{"using.proxy", "Utilizar o proxy {0}:{1}."},
		{"returncode.ok", "Mensagem enviada com sucesso (HTTP {0}); {1} transmitida em {2} [{3} KB/s]."},
		{"connection.tls.info", "Ligação TLS de saída estabelecida [{0}, {1}]"},
		{"error.http502", "Problema de ligação, não foi possível transferir dados. (HTTP 502 - GATEWAY INCORRECTO)"},
		{"returncode.accepted", "Mensagem enviada com sucesso (HTTP {0}); {1} transmitida em {2} [{3} KB/s]."},
		{"error.http503", "Problema de ligação, não foi possível transferir dados. (HTTP 503 - SERVIÇO INDISPONÍVEL)"},
		{"strict.hostname.check", "Para a ligação TLS de saída, é efectuada uma verificação rigorosa do nome do anfitrião em relação ao certificado do servidor."},
		{"error.http504", "Problema de ligação, não foi possível transferir dados. (HTTP 504 - TEMPO LIMITE DO GATEWAY)"},
		{"using.proxy.auth", "Utilizar proxy {0}:{1} (autenticação como {2})."},
		{"error.noconnection", "Problema de ligação, não foi possível transferir dados."},
		{"hint.httpcode.signals.problem", "Nota:\nFoi estabelecida uma ligação ao seu anfitrião parceiro - está a ser executado um servidor Web.\nO servidor remoto está a sinalizar que algo está errado com o caminho ou porta do pedido e está a devolver o código HTTP {0}.\nUtilize um motor de busca na Internet se necessitar de mais informações sobre este código HTTP."},
		{"hint.SSLException", "Nota:\nTrata-se normalmente de um problema de negociação ao nível do protocolo. O seu parceiro rejeitou a sua ligação.\nOu o seu parceiro espera uma ligação segura (HTTPS) e você queria estabelecer uma ligação não segura ou vice-versa.\nTambém é possível que o seu parceiro exija uma versão TLS diferente ou um algoritmo de encriptação diferente do que oferece."},
		{"sending.msg.async", "Enviar mensagem AS2 para {0}, esperar MDN assíncrono para confirmação de receção em {1}."},
	};
}
