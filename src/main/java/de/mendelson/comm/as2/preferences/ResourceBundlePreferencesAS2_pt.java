//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesAS2_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.preferences;

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
public class ResourceBundlePreferencesAS2_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"country", "País"},
		{"showquotaconf", "Mostrar quota na gestão de parceiros"},
		{"proxyport", "Porta de proxy HTTP"},
		{"retrycount", "Número de tentativas de ligação"},
		{"colorblindness", "Apoio ao daltonismo"},
		{"set.to", "foi definido como"},
		{"notification.setting.updated", "As definições de notificação foram alteradas."},
		{"showhttpheaderconf", "Exibir a gestão do cabeçalho HTTP no cliente"},
		{"checkrevocationlist", "Verificar listas de revogação de certificados"},
		{"autologdirdeleteolderthan", "Limpar o diretório de registo (mais antigo que)"},
		{"autoimportpartnertlscertificates", "Importação automática de certificados TLS de parceiros modificados"},
		{"stricthostcheck", "(TLS) Verificar anfitrião"},
		{"autostatsdelete", "Eliminar automaticamente dados estatísticos antigos"},
		{"cem", "Utilizar CEM"},
		{"language", "Língua do cliente"},
		{"setting.updated", "A definição foi actualizada"},
		{"jetty.http.port", "Porta de entrada HTTP"},
		{"proxypass", "Dados de acesso ao proxy HTTP (palavra-passe)"},
		{"commed", "Edição comunitária"},
		{"embeddedhttpserverrequestlog", "Registo de pedidos do servidor HTTP integrado"},
		{"proxyuser", "Dados de acesso ao proxy HTTP (utilizador)"},
		{"maxoutboundconnections", "Número máximo de ligações de saída simultâneas"},
		{"proxyuseauth", "Utilizar dados de acesso de proxy HTTP"},
		{"outboundstatusfile", "Criar um ficheiro de estado para cada transação"},
		{"module.name", "[DEFINIÇÕES]"},
		{"proxyuse", "Utilizar proxy HTTP para ligação de saída"},
		{"automsgdeletelog", "Eliminar transacções antigas (entrada de registo)"},
		{"retrywaittime", "Restabelecer a ligação a cada n segundos"},
		{"automsgdeleteolderthanmults", "Eliminar transacções antigas (unidade de tempo em s)"},
		{"autostatsdeleteolderthan", "Eliminar estatísticas (mais antigas que)"},
		{"autologdirdelete", "Limpar automaticamente o diretório de registo"},
		{"logpollprocess", "Documentar o processo de sondagem no registo"},
		{"showoverwritelocalstationsecurity", "Mostrar: Substituir a segurança da estação local"},
		{"TRUE", "ligado"},
		{"asyncmdntimeout", "Tempo limite para MDN assíncrono em min"},
		{"httpsendtimeout", "Tempo limite de envio (HTTP/S)"},
		{"jetty.connectionlimit.maxConnections", "Número máximo de ligações de entrada simultâneas"},
		{"lastupdatecheck", "Última verificação da nova versão (hora unix)"},
		{"receiptpartnersubdir", "Utilizar subdiretório por parceiro"},
		{"setting.reset", "A definição do servidor [{0}] foi reposta para o valor predefinido."},
		{"dirmsg", "Diretório de base para mensagens"},
		{"jetty.ssl.port", "Porta de entrada HTTPS"},
		{"FALSE", "desligado"},
		{"automsgdeleteolderthan", "Eliminar transacções antigas (mais antigas que)"},
		{"automsgdelete", "Eliminar automaticamente transacções antigas"},
		{"datasheetreceipturl", "Receber URL da ficha de dados"},
		{"proxyhost", "Anfitrião do proxy HTTP"},
		{"trustallservercerts", "(TLS) Confiar em todos os certificados do servidor remoto"},
	};
}
