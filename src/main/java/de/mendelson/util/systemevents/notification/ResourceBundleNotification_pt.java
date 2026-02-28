//$Header: /oftp2/de/mendelson/util/systemevents/notification/ResourceBundleNotification_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.systemevents.notification;

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
public class ResourceBundleNotification_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"test.message.debug", "\nO envio da mensagem falhou.\n"},
		{"notification.about.event", "Esta notificação refere-se ao evento de sistema {0}.\nUrgência: {1}\nOrigem: {2}\nTipo: {3}\nId: {4}"},
		{"notification.summary", "Resumo dos eventos do sistema {0}"},
		{"authorization.oauth2.authorizationcode", "Código de autorização"},
		{"authorization.oauth2.clientcredentials", "Credenciais do cliente"},
		{"authorization.credentials", "Utilizador/palavra-passe"},
		{"test.message.send", "Foi enviada uma mensagem de teste para {0}."},
		{"authorization.oauth2", "OAUTH2"},
		{"notification.summary.info", "O utilizador recebe esta mensagem de resumo porque definiu um número limitado de\nde notificações por unidade de tempo.\nPara obter detalhes sobre os eventos individuais, inicie o cliente\no cliente e navegue para \"Eventos do sistema de ficheiros\".\nAí, introduza o número único do evento na máscara de pesquisa.\ndo evento na máscara de pesquisa."},
		{"misc.message.summary.send", "Foi enviada uma mensagem eletrónica de notificação de resumo para {0}"},
		{"authorization.none", "NENHUM"},
		{"misc.message.summary.failed", "O envio de uma mensagem de correio eletrónico de notificação de resumo para {0} falhou"},
		{"misc.message.send", "Foi enviada uma mensagem de correio eletrónico de notificação para {0} ({1}-{2}-{3})."},
		{"module.name", "[NOTIFICAÇÃO POR CORREIO ELECTRÓNICO]"},
		{"misc.message.send.failed", "O envio de uma mensagem de correio eletrónico de notificação para {0} falhou"},
		{"do.not.reply", "Por favor, não responda a esta mensagem."},
	};
}
