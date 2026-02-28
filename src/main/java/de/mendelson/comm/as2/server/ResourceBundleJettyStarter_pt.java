//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleJettyStarter_pt.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundleJettyStarter_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"httpserver.startup.problem", "Problema no início ({0})"},
		{"userconfiguration.readerror", "Problema ao ler a configuração do utilizador de {0}: {1} ... Ignorar a configuração do utilizador e iniciar o servidor Web utilizando os valores predefinidos definidos"},
		{"httpserver.running", "Servidor HTTP integrado em execução ({0})"},
		{"deployment.failed", "[{0}] NÃO foi fornecido: {1}"},
		{"userconfiguration.setvar", "Definir o valor definido pelo utilizador [{0}] para [{1}]"},
		{"userconfiguration.reading", "Ler a configuração definida pelo utilizador a partir de {0}"},
		{"httpserver.willstart", "Início do servidor HTTP integrado"},
		{"tls.keystore.reloaded", "As alterações foram registadas no repositório de chaves TLS e os dados do repositório de chaves do servidor HTTP foram actualizados"},
		{"module.name", "[JETTY]"},
		{"httpserver.stopped", "Servidor HTTP integrado parado"},
		{"deployment.success", "[{0}] foi implantado com sucesso"},
		{"listener.started", "Aguardar ligações de entrada {0}"},
	};
}
