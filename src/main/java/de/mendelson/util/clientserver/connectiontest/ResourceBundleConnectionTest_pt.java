//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/ResourceBundleConnectionTest_pt.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver.connectiontest;

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
public class ResourceBundleConnectionTest_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"test.connection.proxy.auth", "A ligação utiliza o proxy {0} com autenticação (utilizador \"{1}\")"},
		{"certificates.found.details", "Certificado [{0}/{1}]: {2}"},
		{"sni.extension.set", "O nome do anfitrião para a extensão TLS SNI foi definido como \"{0}\""},
		{"test.connection.proxy.noauth", "A ligação utiliza o proxy {0} sem autenticação"},
		{"wrong.protocol.hint", "O seu parceiro está à espera de uma ligação não segura, existe um problema de protocolo ou é necessária a autenticação do cliente"},
		{"certificate.ca", "Certificado CA"},
		{"certificate.does.exist.local", "Este certificado já existe no seu repositório de chaves TLS local, o alias é \"{0}\""},
		{"connection.problem", "{0} não pode ser alcançado - pode ser um problema de infraestrutura ou foram introduzidos dados incorrectos"},
		{"service.found.failure", "Erro: Não foi encontrado nenhum serviço OFTP em execução em {0}"},
		{"certificate.selfsigned", "Assinado pelo próprio"},
		{"remote.service.identification", "Identificação do serviço do servidor remoto: \"{0}\""},
		{"test.start.plain", "Iniciar o controlo da ligação para {0}, PLAIN..."},
		{"connection.success", "A ligação a {0} foi estabelecida com êxito"},
		{"wrong.protocol", "O protocolo encontrado é \"{0}\", não se trata de uma ligação segura. Tentou ligar-se a este parceiro utilizando um dos protocolos [{1}]. No entanto, o seu parceiro não oferece nenhum destes protocolos de segurança de linha na porta e endereço indicados."},
		{"tag", "Teste de ligação a {0}"},
		{"certificate.does.not.exist.local", "Este certificado ainda não existe no seu repositório de chaves TLS local - importe-o"},
		{"result.exception", "Ocorreu o seguinte erro durante o teste: {0}."},
		{"local.station", "Estação local"},
		{"certificates.found", "{0} Os certificados foram encontrados e descarregados"},
		{"protocol.information", "O protocolo utilizado foi identificado como \"{0}\""},
		{"info.securityprovider", "Fornecedor de segurança TLS utilizado: {0}"},
		{"certificate.enduser", "Certificado de utilizador final"},
		{"exception.occured", "Ocorreu um problema durante o teste de ligação: [{0}] {1}"},
		{"requesting.certificates", "Os certificados do servidor remoto são descarregados"},
		{"test.start.ssl", "Iniciar verificação de ligação a {0}, TLS. Tenha em atenção que este teste confia em todos os certificados do servidor - por isso, mesmo que este teste seja bem sucedido, isso não significa que o seu armazenamento de chaves TLS esteja configurado corretamente."},
		{"info.protocols", "O cliente permite a negociação através dos seguintes protocolos TLS: {0}"},
		{"timeout.set", "Definir o tempo limite para {0}ms"},
		{"test.connection.direct", "É utilizada uma ligação IP direta"},
		{"exception.occured.oftpservice", "Não foi possível identificar nenhum servidor OFTP2 em execução no endereço e porta indicados. Pode tratar-se de um problema temporário, por exemplo, o servidor OFTP2 remoto não está a funcionar, mas os dados do endereço estão corretos. Ocorreu o seguinte problema: [{0}] {1}"},
		{"service.found.success", "Sucesso: Foi encontrado um serviço OFTP em execução em {0}"},
		{"check.for.service.oftp2", "Verificar se o serviço OFTP2 está a funcionar..."},
	};
}
