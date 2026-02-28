//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui_pt.java 3     8/01/25 16:18 Heller $
package de.mendelson.comm.as2.client;

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
* @version $Revision: 3 $
*/
public class ResourceBundleAS2Gui_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dbconnection.failed.message", "Não foi possível estabelecer uma ligação ao servidor da base de dados AS2: {0}"},
		{"dialog.msg.delete.title", "Eliminar mensagens"},
		{"buy.license", "Comprar licença"},
		{"filter.partner", "Restrição de parceiros"},
		{"uploading.to.server", "Transferir para o servidor"},
		{"dialog.resend.title", "Reenviar dados"},
		{"menu.file.certificate.ssl", "TLS"},
		{"menu.file.certificates", "Certificados"},
		{"menu.file.cem", "Gestão de Intercâmbio de Certificados (CEM)"},
		{"dialog.resend.message", "Pretende realmente reenviar a transação selecionada?"},
		{"tab.welcome", "Notícias e actualizações"},
		{"server.answer.timeout.title", "Tempo limite na ligação cliente-servidor"},
		{"details", "Detalhes da mensagem"},
		{"filter.showstopped", "Mostrar parado"},
		{"menu.help.about", "Sobre"},
		{"welcome", "Bem-vindo, {0}"},
		{"resend.failed.unknown.receiver", "Reenvio falhado: destinatário desconhecido {0} - verifique se este parceiro ainda existe no sistema."},
		{"menu.file.serverinfo", "Mostrar a configuração do servidor HTTP"},
		{"delete.msg", "Eliminar"},
		{"dialog.resend.message.multiple", "Pretende mesmo reenviar as {0} transacções selecionadas?"},
		{"tab.transactions", "Transacções"},
		{"filter.localstation", "Restrição da estação local"},
		{"dbconnection.failed.title", "Não é possível estabelecer ligação"},
		{"menu.file.ha", "Instâncias de alta disponibilidade"},
		{"login.failed.client.incompatible.title", "O login foi rejeitado"},
		{"menu.file.certificate.signcrypt", "Assinatura/encriptação"},
		{"fatal.error", "Erro"},
		{"filter.showfinished", "Mostrar acabado"},
		{"menu.help", "Ajuda"},
		{"menu.file.systemevents", "Eventos do sistema"},
		{"menu.file.resend", "Enviar como uma nova transação"},
		{"filter.direction.outbound", "A partir de"},
		{"menu.file.resend.multiple", "Enviar como novas transacções"},
		{"menu.file.migrate.hsqldb", "Migrar de HSQLDB"},
		{"menu.file.cemsend", "Trocar certificados com parceiros (CEM)"},
		{"dialog.msg.delete.message", "Pretende mesmo apagar permanentemente as mensagens selecionadas?"},
		{"menu.help.forum", "Fórum"},
		{"filter.direction", "Restrição direcional"},
		{"filter.showpending", "Mostrar espera"},
		{"filter.to", "Até"},
		{"filter.use", "Restrição temporal"},
		{"keyrefresh", "Atualizar certificados"},
		{"menu.help.supportrequest", "Pedido de apoio"},
		{"filter.none", "-- Nenhum"},
		{"msg.delete.success.single", "{0} A mensagem foi apagada"},
		{"new.version.logentry.2", "Pode descarregá-los em {0}."},
		{"resend.failed.unknown.sender", "Reenvio falhado: Remetente desconhecido {0} - verifique se este parceiro ainda existe no sistema."},
		{"new.version.logentry.1", "Está disponível uma nova versão."},
		{"logputput.disabled", "** A saída do registo foi suprimida"},
		{"menu.file.partner", "Parceiro"},
		{"menu.file.statistic", "Estatísticas"},
		{"menu.file.certificate", "Certificados"},
		{"login.failed.client.incompatible.message", "O servidor informa que este cliente não tem a versão correta.\nUtilize o cliente que corresponde ao servidor."},
		{"filter.from", "De"},
		{"configurecolumns", "Colunas"},
		{"server.answer.timeout.details", "O servidor não está a responder dentro do período de tempo definido - a carga é demasiado elevada?"},
		{"menu.help.helpsystem", "Sistema de ajuda"},
		{"menu.file.datasheet", "Folha de dados para ligação"},
		{"refresh.overview", "Atualizar a lista de transacções"},
		{"warning.refreshstopped", "A atualização da interface do utilizador está desactivada."},
		{"resend.failed.nopayload", "Falha ao reenviar como nova transação: A transação selecionada {0} não tem dados do utilizador."},
		{"msg.delete.success.multiple", "{0} As mensagens foram eliminadas"},
		{"menu.file.quota", "Contingentes"},
		{"menu.file", "Ficheiro"},
		{"menu.file.send", "Enviar ficheiro ao parceiro"},
		{"filter", "Filtros"},
		{"new.version", "Está disponível uma nova versão. Clique aqui para a descarregar."},
		{"logputput.enabled", "** A saída de registo foi activada"},
		{"stoprefresh.msg", "Atualizar ligado/desligado"},
		{"menu.help.shop", "mendelson Loja virtual"},
		{"filter.direction.inbound", "A chegar"},
		{"menu.file.preferences", "Definições"},
		{"menu.file.exit", "Sair"},
		{"menu.file.searchinserverlog", "Procurar no registo do servidor"},
                {"no.helpset.for.language", "Lamentamos, mas não existe um sistema de ajuda disponível para o seu idioma, será utilizado o sistema de ajuda em inglês." },
	};
}
