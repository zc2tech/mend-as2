//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences_pt.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundlePreferences_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Apagar entradas de transacções antigas</strong><br><br>"
			+"Isso define o período de tempo em que as transações e os dados temporários associados permanecem no sistema e são exibidos na síntese de transações.<br>"
			+"Estas definições não afectam os dados/ficheiros recebidos, pois estes não são afectados.<br>"
			+"Para transacções eliminadas, o registo de transacções ainda está disponível através da funcionalidade de pesquisa de registos.</HTML>"},
		{"button.browse", "Navegar"},
		{"label.asyncmdn.timeout", "Tempo máximo de espera para MDNs assíncronas"},
		{"label.deletestatsolderthan", "A partir de dados estatísticos mais antigos que"},
		{"embedded.httpconfig.not.available", "Servidor HTTP não disponível ou problemas de acesso ao ficheiro de configuração"},
		{"label.loghttprequests", "Registo de pedidos HTTP do servidor HTTP integrado"},
		{"testmail.title", "Envio de uma mensagem de correio eletrónico de teste"},
		{"label.country", "País/Região"},
		{"label.httpport", "Porta de entrada HTTP"},
		{"tab.connectivity", "Ligações"},
		{"label.deletemsglog", "Apagamento automático de ficheiros e entradas de registo"},
		{"label.hicontrastmode", "Modo de alto contraste"},
		{"label.mailaccount", "Conta do servidor de correio eletrónico"},
		{"label.max.outboundconnections.help", "<HTML><strong>Máximo de conexões paralelas de saída</strong><br><br>"
			+"Este é o número máximo de ligações de saída paralelas que o seu sistema irá abrir.<br>"
			+"Este valor é utilizado principalmente para proteger o sistema do seu parceiro de ser sobrecarregado por ligações de entrada do seu lado.<br><br>"
			+"O valor predefinido é 9999.</HTML>"},
		{"label.httpsport", "Porta de entrada HTTPS"},
		{"label.keystore.https", "Keystore (para envio via Https):"},
		{"maintenancemultiplier.minute", "Minuto(s)"},
		{"button.mailserverdetection", "Descobrir o servidor de correio eletrónico"},
		{"label.mailpass", "Palavra-passe do servidor de correio eletrónico"},
		{"label.mailhost.hint", "IP ou domínio do servidor"},
		{"label.retry.waittime.help", "<HTML><strong>Tempo de espera entre novas tentativas de ligação</strong><br><br>"
			+"Este é o tempo em segundos que o sistema espera antes de voltar a ligar ao parceiro.<br>"
			+"Só é efectuada uma nova tentativa de ligação se não for possível estabelecer uma ligação a um parceiro (por exemplo, falha do sistema do parceiro ou problema de infraestrutura).<br>"
			+"O número de tentativas de ligação pode ser configurado na propriedade <strong>Número máximo de tentativas de ligação</strong>.<br><br>"
			+"O valor predefinido é 30s.</HTML>"},
		{"label.smtpauthorization.pass.hint", "Palavra-passe do servidor SMTP"},
		{"button.cancel", "Cancelar"},
		{"label.proxy.port.hint", "Porto"},
		{"label.deletemsgolderthan", "Entradas de transacções mais antigas que"},
		{"label.smtpauthorization.user.hint", "Nome do utilizador do servidor SMTP"},
		{"label.proxy.user", "Utilizadores"},
		{"label.smtpauthorization.header", "Autorização SMTP"},
		{"keystore.hint", "<HTML><strong>Atenção:</strong><br>"
			+"Altere estes parâmetros apenas se pretender integrar keystores externos. Alterar os caminhos pode causar problemas durante a atualização.</HTML>"},
		{"tab.log", "Protocolo"},
		{"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Apagar diretórios de registo antigos</strong><br><br>"
			+"Mesmo que as transacções antigas tenham sido eliminadas, os processos ainda podem ser rastreados através dos ficheiros de registo existentes.<br>"
			+"Esta definição elimina estes ficheiros de registo e também todos os ficheiros de eventos do sistema que se enquadram no mesmo período de tempo.</HTML>"},
		{"label.loghttprequests.help", "<HTML><strong>Protocolo de pedido HTTP</strong><br><br>"
			+"Se estiver ativado, o servidor HTTP incorporado (Jetty) escreve um registo de pedidos nos ficheiros <strong>log/yyyy_MM_dd.jetty.request.log</strong>. Estes ficheiros de registo não são eliminados pela manutenção do sistema - por favor, elimine-os manualmente.<br><br>"
			+"Reinicie o software para que as alterações a esta definição tenham efeito.</HTML>"},
		{"tab.maintenance", "Manutenção do sistema"},
		{"tab.interface", "Módulos"},
		{"label.smtpauthorization.credentials", "Utilizador/palavra-passe"},
		{"checkbox.notifypostprocessing", "Problemas com o pós-processamento"},
		{"label.replyto", "Endereço para resposta"},
		{"label.language.help", "<HTML><strong>Língua</strong><br><br>"
			+"Este é o idioma de apresentação do cliente. Se executar o cliente e o servidor em processos diferentes (o que é recomendado), o idioma do servidor pode ser diferente.<br>"
			+"A língua utilizada no protocolo é sempre a língua do servidor.</HTML>"},
		{"label.displaymode.help", "<HTML><strong>Apresentação</strong><br><br>"
			+"Aqui define-se um dos modos de visualização suportados pelo cliente.<br>"
			+"Isso também pode ser definido por meio de parâmetros de linha de comando durante a chamada.</HTML>"},
		{"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Apagar dados estatísticos antigos</strong><br><br>"
			+"O sistema recolhe dados de compatibilidade dos sistemas parceiros e pode apresentá-los como estatísticas.<br>"
			+"Isso determina o período de tempo em que esses dados são mantidos.</HTML>"},
		{"label.stricthostcheck", "TLS: Controlo rigoroso do nome do anfitrião"},
		{"label.httpsport.help", "<HTML><strong>Porta de entrada HTTPS</strong><br><br>"
			+"Esta é a porta para ligações encriptadas de entrada (TLS). Esta definição é transmitida ao servidor HTTP incorporado, é necessário reiniciar o servidor AS2 após uma alteração.<br>"
			+"A porta faz parte do URL para o qual o seu parceiro deve enviar mensagens AS2. Isto é https://Host:<strong>Port</strong>/as2/HttpReceiver<br><br>"
			+"O valor predefinido é 8443.</HTML>"},
		{"checkbox.notifycertexpire", "Antes da expiração dos certificados"},
		{"label.mailport.hint", "Porta SMTP"},
		{"label.country.help", "<HTML><strong>País/Região</strong><br><br>"
			+"Esta definição controla essencialmente apenas o formato da data utilizado para apresentar os dados da transação, etc. no cliente.</HTML>"},
		{"label.smtpauthorization.oauth2.clientcredentials", "OAuth2 (credenciais do cliente)"},
		{"event.preferences.modified.subject", "O valor {0} das definições do servidor foi modificado"},
		{"warning.changes.canceled", "O utilizador cancelou o diálogo de definições - não foram efectuadas quaisquer alterações às definições."},
		{"label.proxy.useauthentification", "Utilizar autenticação para proxy"},
		{"label.keystore.encryptionsign", "Armazenamento de chaves( encriptação, assinatura):"},
		{"label.darkmode", "Modo escuro"},
		{"label.smtpauthorization.none", "Nenhum"},
		{"label.retry.max.help", "<HTML><strong>Número máximo de tentativas para estabelecer uma ligação</strong><br><br>"
			+"Este é o número de tentativas utilizadas para repetir as ligações a um parceiro se não for possível estabelecer uma ligação.<br>"
			+"O tempo de espera entre estas tentativas pode ser definido na propriedade <strong>Tempo de espera entre tentativas de ligação</strong>.<br><br>"
			+"O valor predefinido é 10.</HTML>"},
		{"tab.security", "Segurança"},
		{"label.mailport", "Porto"},
		{"label.logpollprocess.help", "<HTML><strong>Informações sobre o processo de sondagem dos diretórios</strong><br><br>"
			+"Se ativar esta opção, cada operação de sondagem de um diretório de saída é anotada no registo.<br>"
			+"Como este pode ser um número muito grande de entradas, não use esta opção em nenhuma circunstância na operação produtiva, mas apenas para fins de teste.</HTML>"},
		{"label.keystore.https.pass", "Palavra-passe do Keystore (para envio via Https):"},
		{"info.restart.client", "É necessário reiniciar o cliente para que estas alterações tenham efeito!"},
		{"label.proxy.url", "URL de proxy"},
		{"label.proxy.pass", "palavra-passe"},
		{"label.notificationmail", "Destinatário da notificação Endereço de correio eletrónico"},
		{"button.testmail", "Enviar correio de teste"},
		{"maintenancemultiplier.day", "Dia(s)"},
		{"label.asyncmdn.timeout.help", "<HTML><strong>Tempo máximo de espera para MDNs assíncronas</strong><br><br>"
			+"O tempo que o sistema espera por uma MDN (Message Delivery Notification) assíncrona para uma mensagem AS2 enviada antes de colocar a transação no estado \"failed\".<br>"
			+"Este valor é válido em todo o sistema para todos os parceiros.<br><br>"
			+"O valor predefinido é 30 min.</HTML>"},
		{"label.keystore.pass", "Palavra-passe do Keystore (encriptação/assinatura digital):"},
		{"label.colorblindness", "Apoio ao daltonismo"},
		{"dirmsg", "Diretório de notícias"},
		{"label.deletelogdirolderthan", "Dados de registo mais antigos que"},
		{"receipt.subdir", "Criar subdirectórios por parceiro para a receção de mensagens"},
		{"label.proxy.user.hint", "Utilizador de início de sessão de proxy"},
		{"label.smtpauthorization.oauth2.authorizationcode", "OAuth2 (Código de autorização)"},
		{"tab.proxy", "Proxy"},
		{"checkbox.notifyclientserver", "Problemas com a ligação cliente-servidor"},
		{"label.proxy.pass.hint", "Palavra-passe de início de sessão do proxy"},
		{"label.maxmailspermin.help", "<HTML><strong>Número máximo de notificações/min</strong><br><br>"
			+"Para evitar demasiadas mensagens de correio eletrónico, pode resumir as notificações definindo o número máximo de notificações por minuto.<br>"
			+"Com esta função, receberá mensagens de correio eletrónico que contêm várias notificações.</HTML>"},
		{"tab.notification", "Notificação"},
		{"checkbox.notifycem", "Eventos de intercâmbio de certificados (CEM)"},
		{"label.maxmailspermin", "Número máximo de notificações/min"},
		{"label.logpollprocess", "Informações sobre o processo de sondagem de diretórios"},
		{"filechooser.selectdir", "Selecione o diretório a definir"},
		{"event.preferences.modified.body", "Valor antigo: {0}\nNovo valor: {1}"},
		{"label.proxy.use", "Utilizar o proxy HTTP para ligações HTTP/HTTPs de saída"},
		{"label.deletemsglog.help", "<HTML><strong>Apagamento automático de ficheiros e entradas de registo</strong><br><br>"
			+"Nas definições, tem a opção de apagar ficheiros antigos (manutenção do sistema).<br>"
			+"Se tiver configurado esta opção e a ativar, cada eliminação de um ficheiro antigo é registada.<br>"
			+"Também é gerado um evento de sistema, que pode informar o utilizador sobre este processo através da função de notificação.</HTML>"},
		{"tab.misc", "Geral"},
		{"label.language", "Língua"},
		{"warning.serverrestart.required", "Reinicie o servidor para que estas alterações tenham efeito."},
		{"remotedir.select", "Selecionar o diretório no servidor"},
		{"label.max.outboundconnections", "Máximo de ligações paralelas de saída"},
		{"label.retry.waittime", "Tempo de espera entre novas tentativas de ligação"},
		{"label.httpsend.timeout", "Tempo limite de envio HTTP/S"},
		{"receipt.subdir.help", "<HTML><strong>Subdirectórios de receção</strong><br><br>"
			+"Define se os dados devem ser recebidos no diretório <strong>&lt;Estação local&gt;/inbox</strong> ou <strong>&lt;Estação local&gt;/inbox/&lt;Nome do parceiro&gt;</strong>.</HTML>"},
		{"button.modify", "Editar"},
		{"testmail.message.error", "Erro ao enviar a mensagem de correio eletrónico de teste:\n{0}"},
		{"label.displaymode", "Representação"},
		{"label.smtpauthorization.pass", "palavra-passe"},
		{"label.security", "Segurança da ligação"},
		{"button.ok", "Ok"},
		{"checkbox.notifyfailure", "Após problemas no sistema"},
		{"checkbox.notifyresend", "Após reenvios rejeitados"},
		{"testmail.message.success", "Foi enviada com êxito uma mensagem de correio eletrónico de teste para {0}."},
		{"label.mailhost", "Servidor de correio eletrónico (SMTP)"},
		{"label.max.inboundconnections.help", "<HTML><strong>Máximo de ligações paralelas de entrada</strong><br><br>"
			+"Este é o número máximo de conexões de entrada paralelas que podem ser abertas do exterior para a sua instalação do mendelson AS2. Este valor se aplica a todo o software e não se limita a parceiros individuais.<br>"
			+"A definição é transmitida para o servidor HTTP incorporado, é necessário reiniciar o servidor AS2 após uma alteração.<br><br>"
			+"Embora seja possível limitar o número de ligações de entrada paralelas, é preferível definir esta opção na sua firewall ou no seu proxy a montante - isto aplica-se a todo o seu sistema e não apenas a uma única peça de software.<br><br>"
			+"O valor predefinido é 1000.</HTML>"},
		{"header.dirvalue", "Diretório"},
		{"filechooser.keystore", "Selecione o ficheiro do armazenamento de chaves (formato JKS)."},
		{"label.trustallservercerts", "TLS: Confie em todos os certificados de servidor final dos seus parceiros AS2"},
		{"header.dirname", "Tipo"},
		{"warning.clientrestart.required", "As definições do cliente foram alteradas - reinicie o cliente para as tornar válidas"},
		{"title", "Definições"},
		{"label.litemode", "Modo de luz"},
		{"label.httpsend.timeout.help", "<HTML><strong>Tempo limite de envio HTTP/S</strong><br><br>"
			+"Este é o valor do tempo limite da ligação de rede para as ligações de saída.<br>"
			+"Se, após este período, não tiver sido estabelecida qualquer ligação ao sistema do seu parceiro, a tentativa de ligação é cancelada e podem ser efectuadas mais tarde outras tentativas de ligação, de acordo com as definições de repetição.<br><br>"
			+"O valor predefinido é 5000ms.</HTML>"},
		{"label.retry.max", "Número máximo de tentativas para estabelecer uma ligação"},
		{"label.notificationmail.help", "<HTML><strong>Endereço de correio eletrónico do destinatário da notificação</strong><br><br>"
			+"O endereço de correio eletrónico do destinatário da notificação.<br>"
			+"Se a notificação tiver de ser enviada a vários destinatários, introduza aqui uma lista de endereços de destinatários separados por vírgulas.</HTML>"},
		{"label.proxy.url.hint", "IP ou domínio do proxy"},
		{"label.trustallservercerts.help", "<HTML><strong>TLS: Confie em todos os certificados de servidor final dos seus parceiros AS2</strong><br><br>"
			+"Normalmente, o TLS requer que todos os certificados da cadeia de confiança do sistema AS2 do seu parceiro sejam mantidos no seu gestor de certificados TLS.<br><br>"
			+"Se ativar esta opção, confia no certificado final do sistema do seu parceiro ao estabelecer uma ligação de saída se apenas detiver os certificados raiz e intermédio associados no gestor de certificados TLS.<br>"
			+"Note-se que esta opção só faz sentido se o seu parceiro utilizar um certificado autenticado.<br>"
			+"De qualquer forma, os certificados auto-assinados são sempre aceites.<br><br>"
			+"<strong>Aviso:</strong> A ativação desta opção reduz o nível de segurança, uma vez que são possíveis ataques man-in-the-middle.</HTML>"},
		{"tab.dir", "Diretórios"},
		{"label.sec", "s"},
		{"label.days", "Dias"},
		{"event.notificationdata.modified.subject", "As definições de notificação foram alteradas"},
		{"label.httpport.help", "<HTML><strong>Porta de entrada HTTP</strong><br><br>"
			+"Esta é a porta para ligações não encriptadas de entrada. Esta definição é transmitida ao servidor HTTP incorporado, é necessário reiniciar o servidor AS2 após uma alteração.<br>"
			+"A porta faz parte do URL para o qual o seu parceiro deve enviar mensagens AS2. Isto é http://Host:<strong>Port</strong>/as2/HttpReceiver.<br><br>"
			+"O valor predefinido é 8080.</HTML>"},
		{"label.min", "min"},
		{"checkbox.notifyconnectionproblem", "Para problemas de ligação"},
		{"label.mailport.help", "<HTML><strong>Porta SMTP</strong><br><br>"
			+"Regra geral, é um destes valores:<br>"
			+"<strong>25</strong> (porta padrão)<br>"
			+"<strong>465</strong> (porta TLS, valor obsoleto)<br>"
			+"<strong>587</strong> (porta TLS, valor predefinido)<br>"
			+"<strong>2525</strong> (porta TLS, valor alternativo, sem norma)</HTML>"},
		{"maintenancemultiplier.hour", "Hora(s)"},
		{"tab.language", "Cliente"},
		{"label.stricthostcheck.help", "<HTML><strong>TLS: Verificação rigorosa do nome do anfitrião</strong><br><br>"
			+"Aqui pode definir se o nome comum (CN) do certificado remoto deve corresponder ao anfitrião remoto no caso de uma ligação TLS de saída.<br>"
			+"Esta verificação só se aplica a certificados autenticados.</HTML>"},
		{"label.smtpauthorization.user", "Utilizadores"},
		{"label.autodelete", "Eliminação automática"},
		{"testmail", "Correio de teste"},
		{"event.notificationdata.modified.body", "Os dados da nota foram criados por\n\n{0}\n\npara\n\n{1}\n\n mudou."},
		{"checkbox.notifytransactionerror", "Após erros nas transacções"},
		{"label.max.inboundconnections", "Máximo de ligações paralelas de entrada"},
	};
}
