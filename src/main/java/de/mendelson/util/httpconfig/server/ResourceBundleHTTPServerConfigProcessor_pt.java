//$Header: /as2/de/mendelson/util/httpconfig/server/ResourceBundleHTTPServerConfigProcessor_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.httpconfig.server;

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
public class ResourceBundleHTTPServerConfigProcessor_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"http.serverstateurl", "Apresentar o estado do servidor:"},
		{"webapp.as2api.war", "mendelson API REST AS2"},
		{"external.ip.error", "IP externo: -Não pode ser determinado-"},
		{"webapp._unknown", "Servlet desconhecido"},
		{"info.cipher.howtochange", "Para desativar determinadas cifras para as ligações de entrada, edite o ficheiro de configuração do seu servidor HTTP incorporado ({0}) com um editor de texto. Procure a cadeia de caracteres <Set name=\"ExcludeCipherSuites\">, adicione a cifra a excluir e reinicie o programa."},
		{"webapp.as4api.war", "mendelson API REST do AS4"},
		{"info.cipher", "As seguintes cifras são suportadas pelo servidor HTTP subjacente no lado da entrada.\nAs que são suportadas dependem da VM Java que está a utilizar (atualmente {1}).\nPode desativar cifras individuais no ficheiro de configuração\nFicheiro de configuração \"{0}\"."},
		{"http.receipturls", "URLs de receção completos da configuração atual"},
		{"webapp.oftp2api.war", "mendelson API REST do OFTP2"},
		{"http.server.config.tlskey.none", "Chave TLS: Não está definida nenhuma chave TLS, não são possíveis ligações TLS de entrada!"},
		{"external.ip", "IP externo: {0} / {1}"},
		{"webapp.webas2.war", "Monitorização Web do servidor AS2 mendelson"},
		{"webapp.as4.war", "mendelson AS4 recebendo servlet"},
		{"info.protocols", "Os seguintes protocolos são suportados pelo servidor HTTP subjacente para ligações de entrada.\nOs protocolos suportados dependem do Java VM que está a utilizar (atualmente {1}). O fornecedor de segurança TLS utilizado é {2}.\nÉ possível desativar protocolos individuais no ficheiro de configuração\nFicheiro de configuração \"{0}\"."},
		{"http.server.config.listener", "A porta {0} ({1}) está ligada ao adaptador de rede {2}"},
		{"webapp.as2.war", "mendelson AS2 recebendo servlet"},
		{"http.deployedwars", "WARs atualmente disponíveis no servidor HTTP (funcionalidade de servlet):"},
		{"webapp.as2-sample.war", "exemplos da API mendelson AS2"},
		{"webapp.as4-sample.war", "exemplos da API mendelson AS4"},
		{"http.server.config.clientauthentication", "O servidor requer autenticação de cliente TLS: {0}"},
		{"info.protocols.howtochange", "Para desativar determinados protocolos no lado da entrada, edite o ficheiro de configuração do seu servidor HTTP incorporado ({0}) com um editor de texto. Procure a cadeia de caracteres <Set name=\"ExcludeProtocols\">, adicione o protocolo a excluir e reinicie o programa."},
		{"http.server.config.tlskey.info", "Chave TLS:\n	Alias [{0}]\n	Impressão digital SHA1 [{1}]\n	Número de série [{2}]\n	Válido até [{3}]\n"},
	};
}
