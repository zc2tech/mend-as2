//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerPanel_pt.java 4     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui;

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
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundlePartnerPanel_pt extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"label.httpauth.oauth2.authorizationcode.asyncmdn", "OAuth2 (Código de autorização)"},
        {"tab.httpheader", "cabeçalho HTTP"},
        {"label.as2version", "Versão AS2"},
        {"label.overwrite.security", "Substituir as definições de segurança da estação local"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "O identificador único (na sua rede de parceiros) que é utilizado no protocolo AS2 para identificar este parceiro. Pode escolher livremente - certifique-se apenas de que é único, a nível mundial.</HTML>"},
        {"label.httpauth.credentials.message.pass", "palavra-passe"},
        {"label.contact", "Contactar-nos"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Manter o nome original do ficheiro</strong><br><br>"
            + "Se esta opção estiver activada, o sistema tenta extrair o nome do ficheiro original das mensagens AS2 recebidas e gravar o ficheiro transferido com esse nome, para que possa ser processado em conformidade.<br>"
            + "Esta opção só funciona se o remetente tiver adicionado a informação original do nome do ficheiro. Se ativar esta opção, certifique-se de que o seu parceiro envia nomes de ficheiros únicos.<br><br>"
            + "Se o nome do ficheiro extraído não for um nome de ficheiro válido, será substituído por um nome de ficheiro válido, será acionado um aviso de evento de sistema POSTPROCESSING e o processamento continuará.</HTML>"},
        {"label.overwrite.crypt.help", "<HTML><strong>Decriptografar mensagens recebidas</strong><br><br>"
            + "Esta chave é utilizada para desencriptar as mensagens recebidas deste parceiro - em vez da chave definida da respectiva estação local.</HTML>"},
        {"label.name.help", "<HTML><strong>Nome</strong><br><br>"
            + "Este é o nome interno do parceiro tal como é utilizado no sistema. Não é um valor específico do protocolo, mas é utilizado para criar nomes de ficheiros ou estruturas de diretórios que se referem a este parceiro.</HTML>"},
        {"label.signedmdn", "Pedido de confirmação de receção assinado (MDN)"},
        {"label.pollinterval", "Intervalo de recolha"},
        {"label.compression.help", "<HTML><strong>Compressão de dados</strong><br><br>"
            + "Se esta opção estiver activada, as mensagens de saída são comprimidas utilizando o algoritmo ZLIB.<br>"
            + "A vantagem da compressão é que o tamanho da mensagem é normalmente reduzido, o que conduz a uma transmissão mais rápida. A estrutura da mensagem também é alterada, o que pode resolver problemas de compatibilidade.<br>"
            + "A desvantagem é que se trata de um passo de processamento adicional que se faz à custa do rendimento.<br><br>"
            + "Esta opção requer um sistema AS2 do outro lado que suporte pelo menos AS2 1.1.</HTML>"},
        {"label.keepfilenameonreceipt", "Manter o nome original do ficheiro"},
        {"label.pollignore.help", "<HTML><strong>Ignorar recolha para</strong><br><br>"
            + "A monitorização de diretórios irá obter e processar um número definido de ficheiros do diretório monitorizado em intervalos regulares.<br>"
            + "É necessário garantir que o ficheiro está completamente disponível nesse momento. Se copiar regularmente ficheiros para o diretório monitorizado, pode haver sobreposições de tempo, pelo que é recuperado um ficheiro que ainda não está totalmente disponível.<br>"
            + "Por conseguinte, se copiar os ficheiros para o diretório monitorizado utilizando uma operação não atómica, deve selecionar uma extensão de nome de ficheiro no momento do processo de cópia que seja ignorada pelo processo de monitorização.<br>"
            + "Quando o ficheiro completo estiver disponível no diretório monitorizado, pode remover a extensão do nome do ficheiro com uma operação atómica (mover, mv, renomear) e o ficheiro completo será recuperado.<br>"
            + "A lista de extensões de nomes de ficheiros é uma lista de extensões separada por vírgulas, por exemplo \"*.tmp, *.upload\".</HTML>"},
        {"label.maxpollfiles", "Número máximo de ficheiros/processos de recolha"},
        {"label.httpauth.credentials.asyncmdn.user", "Nome do utilizador"},
        {"label.notify.send", "Notificar se a quota de transmissão exceder o seguinte valor:"},
        {"label.httpversion", "Versão do protocolo HTTP"},
        {"label.localstation.help", "<HTML><strong>Estação local</strong><br><br>"
            + "Uma estação local representa o seu próprio sistema. Pode criar qualquer número de estações locais no seu sistema.<br>"
            + "As estações locais e os parceiros de ligação são configurados separadamente. A configuração geral da relação de parceria é então criada automaticamente a partir das configurações da estação local e do parceiro remoto.<br><br>"
            + "Existem dois tipos de parceiros:<br><br>"
            + "<table border=\"0\"><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/localstation.svg\" height=\"20\" width=\"20\"></td><td>Estações locais</td></tr><tr><td style=\"padding-left: 10px\"><img src=\"/de/mendelson/comm/as2/partner/gui/singlepartner.svg\" height=\"20\" width=\"20\"></td><td>Parceiro removido</td></tr></table></HTML>"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>Autenticação de acesso básico HTTP</strong><br><br>"
            + "Configure aqui a autenticação de acesso básico HTTP se esta estiver activada no lado do seu parceiro (definida no RFC 7617). O sistema do parceiro remoto deve devolver um estado <strong>HTTP 401 Unauthorised</strong> para pedidos não autenticados (dados de início de sessão incorrectos, etc.).<br>"
            + "Se a ligação ao seu parceiro exigir a autenticação do cliente TLS (através de certificados), não é necessária qualquer definição aqui.<br>"
            + "Neste caso, importe os certificados do parceiro através do gestor de certificados TLS.<br>"
            + "O sistema encarrega-se então da autenticação do cliente TLS.</HTML>"},
        {"label.subject", "Dados do utilizador Assunto"},
        {"label.cryptalias.key", "Chave privada (desencriptação de dados)"},
        {"label.url.help", "<HTML><strong>Registo do URL de receção</strong><br><br>"
            + "Este é o URL do seu parceiro através do qual o seu sistema AS2 pode ser acedido.<br>"
            + "Por favor, introduza este URL no formato <strong>PROTOCOL://HOST:PORT/PFAD</strong>, sendo que o <strong>PROTOCOL</strong> deve ser um de \"http\" ou \"https\". <strong>HOST</strong> indica o host do servidor AS2 do seu parceiro. <strong>PORT</strong> é a porta de receção do seu parceiro. <strong>PFAD</strong> indica o caminho de receção, por exemplo \"/as2/HttpReceiver\" Toda a entrada é marcada como inválida se o protocolo não for um dos \"http\" ou \"https\", se o URL tiver um formato incorreto ou se a porta não estiver definida no URL.<br><br>"
            + "Por favor, não introduza aqui um URL que se refira ao seu próprio sistema através de \"localhost\" ou \"127.0.0.1\" - estaria a tentar enviar as mensagens AS2 de saída para o seu próprio sistema.</HTML>"},
        {"label.keep.security", "Utilizar as definições de segurança da estação local"},
        {"label.cryptalias.cert.help", "<HTML><strong>Certificado de parceiro (encriptação de dados)</strong<br><br>"
            + "Selecione aqui um certificado que esteja disponível no gestor de certificados do sistema (assinatura/encriptação).<br>"
            + "Se pretender encriptar as mensagens enviadas para este parceiro, este certificado é utilizado para encriptar os dados.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.asyncmdn", "OAuth2 (credenciais do cliente)"},
        {"tab.receipt", "Receção"},
        {"label.contenttype", "Dados do utilizador Tipo de conteúdo"},
        {"label.httpauth.credentials.message.user", "Nome do utilizador"},
        {"label.enabledirpoll", "Monitorização de diretórios"},
        {"label.cryptalias.cert", "Certificado de parceiro (encriptação de dados)"},
        {"label.algorithmidentifierprotection", "Identificador do algoritmo Atributo de proteção"},
        {"label.httpauth.oauth2.authorizationcode.message", "OAuth2 (Código de autorização)"},
        {"partnerinfo", "Com cada mensagem AS2, o seu parceiro também envia informações sobre as funções do seu sistema AS2. Esta é a lista dessas funções."},
        {"label.asyncmdn", "Pedido de aviso de receção assíncrono (MDN)"},
        {"label.mdnurl", "URL DA MDN"},
        {"tab.security", "Segurança"},
        {"label.partnercomment", "Comentário"},
        {"tab.events", "Pós-processamento"},
        {"label.httpauth.credentials.asyncmdn.pass", "palavra-passe"},
        {"label.id", "AS2 id"},
        {"label.contenttype.help", "<HTML><strong>Tipo de conteúdo dos dados do utilizador</strong><br><br>"
            + "Os seguintes tipos de conteúdo são suportados com segurança no protocolo AS2:<br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "O RFC do AS2 afirma que todos os tipos de conteúdo MIME devem ser suportados no AS2.<br>"
            + "No entanto, este não é um requisito obrigatório.<br>"
            + "Por conseguinte, não se deve basear nesta informação,<br>"
            + "que o sistema do seu parceiro ou o processamento SMIME subjacente do mendelson AS2 pode lidar com tipos de conteúdo diferentes dos descritos.</HTML>"},
        {"tooltip.button.addevent", "Criar novo evento"},
        {"label.test.connection", "Verificar a ligação"},
        {"label.signtype", "Assinatura digital"},
        {"label.httpauth.credentials.asyncmdn", "Autenticação HTTP básica"},
        {"label.httpauth.message", "Autenticação de mensagens AS2 de saída"},
        {"label.notify.sendreceive", "Notificar se a quota de envio/receção exceder o seguinte valor:"},
        {"label.signtype.help", "<HTML><strong>Assinatura digital</strong><br><br>"
            + "Aqui seleciona o algoritmo de assinatura com o qual as mensagens enviadas para este parceiro devem ser assinadas.<br>"
            + "Se selecionou um algoritmo de assinatura aqui, também se espera uma mensagem assinada de entrada deste parceiro - no entanto, o algoritmo de assinatura é arbitrário.<br><br>"
            + "A mensagem de saída para este parceiro é assinada utilizando a chave privada da estação local que é o remetente da transação.</HTML>"},
        {"label.httpauth.oauth2.clientcredentials.message", "OAuth2 (credenciais do cliente)"},
        {"tab.send", "Expedição"},
        {"tooltip.button.editevent", "Editar evento"},
        {"tab.mdn", "MDN"},
        {"label.httpauth.asyncmdn", "Autenticação da MDN assíncrona de saída"},
        {"tab.notification", "Notificação"},
        {"label.mdnurl.help", "<HTML><strong>MDN</strong> (<strong>M</strong>mensagem <strong>D</strong>entrega <strong>N</strong>notificação) <strong>URL</strong><br><br>"
            + "Este é o URL que o seu parceiro irá utilizar para a MDN assíncrona de entrada para esta estação local. No caso síncrono, este valor não é utilizado, uma vez que a MDN é então enviada no canal de retorno da ligação de saída.<br>"
            + "Introduza este URL no formato <strong>PROTOCOL://HOST:PORT/PFAD</strong>.<br>"
            + "O <strong>PROTOCOLO</strong> deve ser um de \"http\" ou \"https\".<br>"
            + "<strong>HOST</strong> refere-se ao seu próprio host do servidor AS2.<br>"
            + "<strong>PORT</strong> é a porta de receção do seu sistema AS2.<strong>PFAD</strong> indica o caminho de receção, por exemplo \"/as2/HttpReceiver\".<strong>A entrada inteira é marcada como inválida se o protocolo não for um dos \"http\" ou \"https\", se o URL tiver um formato incorreto ou se a porta não estiver definida no URL.<br><br>"
            + "Por favor, não introduza aqui um URL que se refira ao seu próprio sistema através de \"localhost\" ou \"127.0.0.1\" - esta informação será avaliada pelo seu parceiro após a receção da mensagem AS2 e ele enviará então o MDN para si próprio.</HTML>"},
        {"label.features", "Funções"},
        {"label.httpversion.help", "<HTML><strong>Versão do protocolo HTTP</strong><br><br>"
            + "Existem versões do protocolo HTTP<ul><li>HTTP/1.0 (RFC 1945)</li><li>HTTP/1.1 (RFC 2616)</li><li>HTTP/2.0 (RFC 9113)</li><li>HTTP/3.0 (RFC 9114)</li></ul>HTTP/1.1 é geralmente utilizado para AS2.<br><br>"
            + "Nota: Esta <strong>não</strong> é a versão TLS!</HTML>"},
        {"label.signalias.key", "Chave privada (criar assinatura digital)"},
        {"label.encryptiontype.help", "<HTML><strong>Criptografia de mensagens</strong><br><br>"
            + "Aqui seleciona o algoritmo de encriptação com o qual as mensagens de saída para este parceiro devem ser encriptadas.<br>"
            + "Se tiver selecionado um algoritmo de encriptação aqui, espera-se também uma mensagem encriptada deste parceiro - no entanto, o algoritmo de encriptação é arbitrário.<br><br>"
            + "Para mais informações sobre o algoritmo de encriptação, consulte a Ajuda (secção Parceiro) - todos os algoritmos são aí explicados.</HTML>"},
        {"label.compression", "Compressão de dados"},
        {"label.overwrite.crypt", "Desencriptar mensagens recebidas"},
        {"label.email", "Endereço postal"},
        {"header.httpheadervalue", "Valor"},
        {"httpheader.add", "Adicionar"},
        {"tab.misc", "Geral"},
        {"partnersystem.noinfo", "Não há informações disponíveis - já houve uma transação?"},
        {"label.httpauth.credentials.message", "Autenticação HTTP básica"},
        {"label.usecommandonreceipt", "Receção"},
        {"label.features.cem", "Troca de certificados via CEM"},
        {"label.usecommandonsendsuccess", "Expedição (com êxito)"},
        {"label.signalias.cert.help", "<HTML><strong>Certificado de parceiro (verificar assinatura digital)</strong<br><br>"
            + "Selecione aqui um certificado que esteja disponível no gestor de certificados do sistema (assinatura/encriptação).<br>"
            + "Se as mensagens recebidas deste parceiro forem assinadas digitalmente para uma estação local, este certificado é utilizado para verificar esta assinatura.</HTML>"},
        {"label.mdn.description", "<HTML>O MDN (Message Delivery Notification) é a confirmação da mensagem AS2. Esta secção define o comportamento do seu parceiro para as suas mensagens AS2 de saída.</HTML>"},
        {"label.encryptiontype", "Encriptação de mensagens"},
        {"label.cryptalias.key.help", "<HTML><strong>Chave privada (desencriptação de dados)</strong<br><br>"
            + "Selecione aqui uma chave privada que esteja disponível no gestor de certificados do sistema (assinatura/encriptação).<br>"
            + "Se as mensagens recebidas de qualquer parceiro forem encriptadas para esta estação local, esta chave é utilizada para a desencriptação.<br><br>"
            + "Como só o utilizador está na posse da chave privada aqui definida, só ele pode desencriptar os dados que os seus parceiros encriptaram com o seu certificado.<br>"
            + "Isto significa que qualquer parceiro pode encriptar dados para si - mas só você pode desencriptá-los.</HTML>"},
        {"label.name", "Nome"},
        {"label.signalias.cert", "Certificado de parceiro (verificar assinatura digital)"},
        {"label.email.help", "<HTML><strong>Endereço de correio eletrónico</strong><br><br>"
            + "Este valor faz parte da descrição do protocolo AS2, mas atualmente não é utilizado de todo.</HTML>"},
        {"label.httpauth.none", "Nenhum"},
        {"label.id.hint", "Identificação do parceiro (protocolo AS2)"},
        {"label.url", "Receber URL"},
        {"label.subject.help", "<HTML><strong>Sujeito dos dados do utilizador</strong><br><br>"
            + "$'{'filename} é substituído pelo nome do ficheiro enviado.<br>"
            + "Este valor é transmitido no cabeçalho HTTP, aplicam-se restrições!<br>"
            + "Utilize ISO-8859-1 como codificação de caracteres, apenas caracteres imprimíveis, sem caracteres especiais.<br>"
            + "CR, LF e TAB são substituídos por \"\r\", \"\n\" e \"\t\".</HTML>"},
        {"label.pollignore", "Ignorar a recolha para"},
        {"label.productname", "Nome do produto"},
        {"label.usecommandonsenderror", "Envio (com defeito)"},
        {"tab.dirpoll", "Monitorização de diretórios"},
        {"label.name.hint", "Nome do parceiro interno"},
        {"label.overwrite.sign.help", "<HTML><strong>Assinar mensagens de saída</strong><br><br>"
            + "Esta chave é utilizada para assinar mensagens de saída para este parceiro - em vez da chave definida da respectiva estação local.</HTML>"},
        {"title", "Configuração de parceiros"},
        {"label.polldir", "Diretório controlado"},
        {"tab.httpauth", "Autenticação HTTP"},
        {"label.algorithmidentifierprotection.help", "<HTML><strong>Atributo de proteção do identificador de algoritmo</strong><br><br>"
            + "Se ativar esta opção (o que é recomendado), o atributo Algorithm Identifier Protection é utilizado na assinatura AS2. Esse atributo é definido na RFC 6211.<br><br>"
            + "A assinatura AS2 utilizada é suscetível de ataques de substituição de algoritmos.<br>"
            + "Num ataque de substituição de algoritmo, o atacante altera o algoritmo utilizado ou os parâmetros do algoritmo de modo a alterar o resultado de um procedimento de verificação de assinaturas.<br>"
            + "Este atributo contém agora uma cópia dos identificadores de algoritmos relevantes da assinatura, de modo a que não possam ser alterados, impedindo assim um ataque de substituição de algoritmos à assinatura.<br><br>"
            + "Há sistemas AS2 que não conseguem lidar com este atributo (embora o RFC seja de 2011) e reportam um erro de autorização.<br>"
            + "Neste caso, o atributo pode ser desativado aqui.</HTML>"},
        {"label.pollignore.hint", "Lista de extensões de ficheiros a ignorar, separada por vírgulas (são permitidos wildcards)."},
        {"label.signalias.key.help", "<HTML><strong>Chave privada (criar assinatura digital)</strong<br><br>"
            + "Selecione aqui uma chave privada que esteja disponível no gestor de certificados do sistema (assinatura/encriptação).<br>"
            + "Esta chave é utilizada para criar uma assinatura digital para as mensagens enviadas a todos os parceiros remotos.<br><br>"
            + "Como só o utilizador está na posse da chave privada aqui definida, só ele pode assinar os dados.<br>"
            + "Os seus parceiros podem verificar esta assinatura com o certificado, o que garante que os dados não foram alterados e que é o remetente.</HTML>"},
        {"label.notes.help", "<HTML><strong>Notas</strong><br><br>"
            + "Aqui encontrará a possibilidade de fazer anotações sobre este parceiro para seu próprio uso.</HTML>"},
        {"label.syncmdn", "Pedido de aviso de receção síncrono (MDN)"},
        {"label.signedmdn.help", "<HTML><strong>Confirmação de receção assinada</strong><br><br>"
            + "Com esta definição, é possível informar o sistema parceiro das mensagens AS2 de saída de que se pretende um aviso de receção assinado (MDN).<br>"
            + "Embora isto pareça sensato à primeira vista, o cenário é, infelizmente, problemático.<br>"
            + "Isto porque, uma vez recebida a MDN do parceiro, a transação está concluída.<br>"
            + "Se a verificação da assinatura da MDN for efectuada e falhar, já não existe qualquer forma de informar o parceiro deste problema.<br>"
            + "A anulação de uma transação já não é possível - a transação já foi concluída. Isto significa que a verificação da assinatura da MDN em modo automático é inútil.<br>"
            + "O protocolo AS2 estipula que a aplicação deve resolver este problema lógico, mas tal não é possível.<br>"
            + "A solução mendelson AS2 apresenta um aviso no caso de uma verificação de assinatura MDN falhada.<br><br>"
            + "Há uma outra caraterística especial deste cenário:<br>"
            + "Se houver um problema durante o processamento no lado do parceiro, o MDN pode ser sempre não assinado - independentemente desta definição.</HTML>"},
        {"label.notify.receive", "Notificar se a quota de receção exceder o seguinte valor:"},
        {"label.features.ma", "Vários anexos"},
        {"label.email.hint", "Não utilizado ou validado no protocolo AS2"},
        {"label.localstation", "Estação local"},
        {"httpheader.delete", "Remover"},
        {"tab.partnersystem", "Informações"},
        {"label.features.compression", "Compressão de dados"},
        {"label.asyncmdn.help", "<HTML><strong>Acusação de receção assíncrona</strong><br><br>"
            + "O parceiro estabelece uma nova ligação ao seu sistema para enviar a confirmação da sua mensagem de saída.<br>"
            + "A assinatura é verificada e os dados desencriptados no lado do parceiro depois de a ligação de entrada ter sido encerrada.<br>"
            + "Por este motivo, este método requer menos recursos do que o método com MDN síncrona.</HTML>"},
        {"label.address", "Endereço"},
        {"label.enabledirpoll.help", "<HTML><strong>Monitorização de diretórios</strong><br><br>"
            + "Se esta opção for activada, o sistema procurará automaticamente no diretório de origem novos ficheiros para este parceiro.<br>"
            + "Se for encontrado um novo ficheiro, é gerada uma mensagem AS2 que é enviada ao parceiro.<br>"
            + "Note-se que este método de monitorização de diretórios só pode utilizar parâmetros gerais para todas as criações de mensagens.<br>"
            + "Se pretender definir parâmetros especiais para cada mensagem individualmente, utilize o processo de envio através da linha de comandos.<br>"
            + "No caso de funcionamento em cluster (HA), é necessário desativar toda a monitorização de diretórios, uma vez que este processo não pode ser sincronizado.</HTML>"},
        {"header.httpheaderkey", "Nome"},
        {"label.overwrite.sign", "Assinar mensagens de saída"},
        {"label.syncmdn.help", "<HTML><strong>Acusação síncrona de receção</strong><br><br>"
            + "O parceiro envia o aviso de receção (MDN) no canal de retorno da sua ligação de saída.<br>"
            + "A ligação de saída é mantida aberta enquanto o parceiro desencripta os dados e verifica a assinatura.<br>"
            + "Por este motivo, este método requer mais recursos do que o processamento assíncrono da MDN.</HTML>"},};
}
