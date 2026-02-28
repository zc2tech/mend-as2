//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult_pt.java 2     9/12/24 15:50 Hell $
package de.mendelson.util.clientserver.connectiontest.gui;

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
public class ResourceBundleDialogConnectionTestResult_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"description.2", "O sistema efectuou um teste de ligação ao endereço {0}, porta {1}. O resultado seguinte mostra se o estabelecimento da ligação foi bem sucedido e se está a ser executado um servidor HTTP neste endereço. Mesmo que o teste seja bem sucedido, não é certo que se trate de um servidor HTTP normal ou de um servidor AS2. Se tiver de ser usada uma ligação TLS (HTTPS) e isso tiver sido possível com sucesso, pode descarregar os certificados do seu parceiro e importá-los para o seu keystore."},
		{"header.plain", "{0} [Ligação não segura]"},
		{"description.3", "O sistema efectuou um teste de ligação ao endereço {0}, porta {1}. O resultado seguinte mostra se o estabelecimento da ligação foi bem sucedido e se está a ser executado um servidor HTTP neste endereço. Mesmo que o teste seja bem sucedido, não é certo que se trate de um servidor HTTP normal ou de um servidor AS4. Se tiver de ser usada uma ligação TLS (HTTPS) e isso tiver sido possível com sucesso, pode descarregar os certificados do seu parceiro e importá-los para o seu keystore."},
		{"header.ssl", "{0} [ligação TLS]"},
		{"title", "Resultado do teste de ligação"},
		{"button.viewcert", "<HTML>Importar certificado(s);</HTML>"},
		{"AVAILABLE", "[PRESENTE]"},
		{"label.connection.established", "A ligação IP simples foi estabelecida"},
		{"label.certificates.available.local", "Os certificados de parceiro (TLS) estão disponíveis no seu sistema"},
		{"no.certificate.plain", "Não disponível (ligação não garantida)"},
		{"FAILED", "[ERRO]"},
		{"label.running.oftpservice", "Foi encontrado um serviço OFTP em execução"},
		{"button.close", "Fechar"},
		{"description.1", "O sistema efectuou um teste de ligação ao endereço {0}, porta {1}. O resultado seguinte mostra se a ligação foi bem sucedida e se existe um servidor OFTP2 a funcionar neste endereço. Se foi usada uma ligação TLS e esta foi bem sucedida, pode descarregar os certificados do seu parceiro e importá-los para o seu keystore."},
		{"OK", "[SUCESSO]"},
		{"NOT_AVAILABLE", "[NÃO DISPONÍVEL]."},
		{"used.cipher", "Foi utilizado o seguinte algoritmo de encriptação para o teste: \"{0}\""},
	};
}
