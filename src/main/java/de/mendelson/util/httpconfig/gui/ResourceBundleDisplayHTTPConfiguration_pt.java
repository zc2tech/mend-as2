//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.httpconfig.gui;

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
public class ResourceBundleDisplayHTTPConfiguration_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.info.configfile", "Esta caixa de diálogo mostra-lhe a configuração HTTP/S do lado do servidor. O servidor HTTP fornecido tem a versão <strong>jetty {0}</strong>. Pode configurar as cifras e os protocolos no ficheiro \"{1}\" do servidor. Efectue as configurações básicas no ficheiro \"{2}\" ou diretamente através das configurações do servidor. Reinicie o servidor para que as alterações se tornem efectivas."},
		{"no.ssl.enabled", "O suporte TLS não foi ativado no servidor HTTP subjacente.\nModifique o ficheiro de configuração {0}\nde acordo com a documentação e reinicie o servidor."},
		{"reading.configuration", "Ler a configuração HTTP..."},
		{"tab.protocols", "Protocolos TLS"},
		{"button.ok", "Fechar"},
		{"tab.cipher", "Cifras TLS"},
		{"no.embedded.httpserver", "O servidor HTTP subjacente não foi iniciado.\nNão há informações disponíveis."},
		{"title", "Configuração HTTP do lado do servidor"},
		{"tab.misc", "Geral"},
	};
}
