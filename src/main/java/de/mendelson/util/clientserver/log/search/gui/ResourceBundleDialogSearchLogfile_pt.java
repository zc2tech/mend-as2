//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.clientserver.log.search.gui;

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
public class ResourceBundleDialogSearchLogfile_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"no.data.uid", "**Não existem dados de registo para o número definido pelo utilizador \"{0}\" no período de tempo selecionado. Por favor, selecione o número completo definido pelo utilizador que atribuiu à transmissão como cadeia de pesquisa."},
		{"label.uid", "Identificação definida pelo utilizador"},
		{"label.enddate", "Fim"},
		{"problem.serverside", "Houve um problema no lado do servidor ao pesquisar os ficheiros de registo: [{0}] {1}"},
		{"no.data.mdnid", "**Não existem dados de registo para o número MDN \"{0}\" no período de tempo selecionado. Utilize o número MDN completo, que pode ser encontrado no registo de uma transmissão, como cadeia de pesquisa."},
		{"title", "Pesquisar as entradas de registo do servidor"},
		{"label.search", "<html><div style=\"text-align:centre\">Log<br>search</div></html>"},
		{"label.startdate", "Início"},
		{"textfield.preset", "Número de mensagem AS2, número MDN ou identificação definida pelo utilizador"},
		{"button.close", "Fechar"},
		{"label.messageid", "Número da mensagem"},
		{"no.data.messageid", "**Não existem dados de registo para a mensagem AS2 número \"{0}\" no período de tempo selecionado. Utilize o número completo da mensagem como cadeia de pesquisa."},
		{"label.info", "<html>Defina um período de tempo, introduza o número completo de uma mensagem AS2 ou o número completo de um MDN para encontrar todas as entradas de registo no servidor - depois prima o botão \"Search log\". Pode definir o número definido pelo utilizador para cada transação quando envia os dados para o servidor em execução através da linha de comando."},
		{"label.mdnid", "Número MDN"},
	};
}
