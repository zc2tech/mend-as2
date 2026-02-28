//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui.event;

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
public class ResourceBundlePartnerEvent_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"process.executeshell", "Execução de um comando shell"},
		{"title.configuration.shell", "Configuração do comando Shell [Parceiro {0}, {1}]"},
		{"shell.hint.samples", "<HTML><strong>Exemplos</strong><br>"
			+"Windows: <i>cmd /c move \"$'{'filename}\" \"c:\\diretório de destino\"</i><br>"
			+"Linux: <i>mv \"$'{'filename}\" \"~/diretório de destino/\"</i></HTML>"},
		{"tab.newprocess", "Processos disponíveis para pós-processamento"},
		{"label.shell.command", "comando ({0}):"},
		{"shell.hint.replacement.3", "<HTML>As seguintes variáveis são substituídas por valores do sistema neste comando antes de ser executado:<br>"
			+"<i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
		{"shell.hint.replacement.2", "<HTML>As seguintes variáveis são substituídas por valores do sistema neste comando antes de ser executado:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"shell.hint.replacement.1", "<HTML>As seguintes variáveis são substituídas por valores do sistema neste comando antes de ser executado:<br>"
			+"<i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
		{"process.movetodirectory.description", "Mover os dados para outro diretório"},
		{"process.executeshell.description", "Executar um comando shell ou um script para pós-processamento de dados."},
		{"title.configuration.movetodir", "Mover mensagens para o diretório [Parceiro {0}, {1}]"},
		{"label.movetodir.remotedir.select", "Selecione o diretório de destino no servidor"},
		{"label.movetopartner.info", "<HTML>Selecione o parceiro remoto para o qual a mensagem deve ser encaminhada.</HTML>"},
		{"process.movetopartner", "Transmissão aos parceiros"},
		{"process.movetopartner.description", "Encaminhamento para um parceiro, por exemplo, da DMZ para o sistema ERP."},
		{"label.movetodir.info", "<HTML>Por favor, configure o diretório do lado do servidor para o qual a mensagem deve ser movida.</HTML>"},
		{"type.3", "após a receção"},
		{"type.2", "após o envio (erro)"},
		{"button.cancel", "Demolição"},
		{"label.shell.info", "<HTML>Por favor, configure o comando shell a ser executado neste caso. Lembre-se de que isso é específico do sistema operacional, ele será redirecionado para o shell padrão do seu sistema operacional.</HTML>"},
		{"type.1", "após o envio (sucesso)"},
		{"label.movetodir.targetdir", "Diretório de destino ({0}):"},
		{"title.select.process", "Selecionar um novo processo como um evento ({0})"},
		{"button.ok", "Ok"},
		{"process.movetodirectory", "Mover para o diretório"},
		{"title.configuration.movetopartner", "Reencaminhamento de dados para um parceiro [Parceiro {0}, {1}]"},
		{"label.movetopartner.noroutingpartner", "<HTML>Não existe nenhum parceiro remoto disponível no sistema para o qual enviar mensagens. Por favor, adicione primeiro um parceiro para o qual as mensagens devem ser enviadas.</HTML>"},
		{"label.movetopartner", "Parceiro-alvo:"},
	};
}
