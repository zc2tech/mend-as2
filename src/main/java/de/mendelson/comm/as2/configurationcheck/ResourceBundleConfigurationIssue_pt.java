//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ResourceBundleConfigurationIssue_pt.java 3     21/02/25 16:04 Heller $
package de.mendelson.comm.as2.configurationcheck;

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
public class ResourceBundleConfigurationIssue_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"1", "Nenhuma chave encontrada no repositório de chaves TLS"},
		{"10", "Certificado de assinatura em falta de um parceiro remoto"},
		{"11", "Falta de uma chave de encriptação de uma estação local"},
		{"12", "Chave de assinatura em falta de uma estação local"},
		{"13", "Utilização de uma chave de teste disponível publicamente como chave TLS"},
		{"14", "A utilização de uma VM Java de 32 bits não é recomendada para utilização produtiva, uma vez que a memória heap máxima é limitada a 1,3 GB."},
		{"15", "Serviço Windows iniciado com a conta de sistema local"},
		{"16", "Grande quantidade de monitorização de diretórios por unidade de tempo"},
		{"17", "Problema de lista de blocos (TLS)"},
		{"18", "Problema da lista de revogação (enc/sign)"},
		{"19", "Cliente e servidor são executados num único processo"},
		{"2", "Várias chaves encontradas no armazenamento de chaves TLS - pode ser apenas uma"},
		{"20", "Não há controlos suficientes para o processo do servidor"},
		{"3", "O certificado expirou (TLS)"},
		{"4", "O certificado expirou (enc/sign)"},
		{"5", "Ativar a eliminação automática - Existe um grande número de transacções no sistema"},
		{"6", "Atribuir pelo menos 4 núcleos de processador ao sistema"},
		{"7", "Reservar pelo menos 8 GB de memória principal para o processo do servidor"},
		{"8", "A quantidade de ligações de saída está definida para 0 - o sistema NÃO enviará"},
		{"9", "Certificado de encriptação em falta de um parceiro remoto"},
		{"hint.1", "<HTML>Não foi encontrada nenhuma chave no keystore TLS do seu sistema.<br>"
			+"As chaves podem ser reconhecidas pelo símbolo de chave à sua frente quando se abre a gestão de certificados.<br>"
			+"É necessária exatamente uma chave no armazenamento de chaves TLS para realizar o processo de aperto de mão para a segurança da linha.<br>"
			+"Sem esta chave, não é possível aceder ou sair de ligações seguras.</HTML>"},
		{"hint.10", "<HTML>Não foi atribuído um certificado de assinatura a um parceiro de ligação na sua configuração.<br>"
			+"Neste caso, não é possível verificar as assinaturas digitais do seu parceiro. Abra a administração do parceiro e atribua um certificado de assinatura ao parceiro.</HTML>"},
		{"hint.11", "<HTML>A sua estação local não atribuiu uma chave de encriptação.<br>"
			+"Nesta configuração, não é possível desencriptar as mensagens recebidas, independentemente do parceiro.<br>"
			+"Abra a administração do parceiro e atribua uma chave privada à estação local.</HTML>"},
		{"hint.12", "<HTML>A sua estação local não atribuiu uma chave de assinatura.<br>"
			+"Não é possível assinar digitalmente mensagens de saída nesta configuração, independentemente do parceiro.<br>"
			+"Abra a administração do parceiro e atribua uma chave privada à estação local.</HTML>"},
		{"hint.13", "<HTML>Na entrega, mendelson fornece algumas chaves de teste.<br>"
			+"Estes estão disponíveis ao público no sítio Web do mendelson.<br>"
			+"Se utilizar estas chaves de forma produtiva para tarefas criptográficas no âmbito da sua transferência de dados, elas oferecem, portanto, <strong>NENHUMA</strong> segurança.<br>"
			+"Aqui também pode enviar mensagens não seguras e não encriptadas.<br>"
			+"Se necessitar de uma chave certificada, contacte a assistência técnica da mendelson.</HTML>"},
		{"hint.14", "<HTML>Os processos Java de 32 bits não podem reservar memória suficiente para manter o sistema estável em operação produtiva. Por favor, use uma JVM de 64 bits.</HTML>"},
		{"hint.15", "<HTML>Você configurou o servidor mendelson AS2 como um serviço do Windows e o iniciou através de uma conta de sistema local (\"{0}\").<br>"
			+"Infelizmente, é possível que este utilizador perca os direitos sobre os seus ficheiros previamente escritos após uma atualização do Windows, o que pode levar a vários problemas de sistema.<br><br>"
			+"Configure um utilizador separado para o serviço e inicie o serviço com esse utilizador.</HTML>"},
		{"hint.16", "<HTML>Definiu um grande número de relações de parceria no seu sistema e monitoriza os diretórios de saída correspondentes em intervalos demasiado curtos.<br>"
			+"Atualmente, são activadas {0} listas de observação de diretórios por minuto e o sistema não consegue acompanhar este ritmo elevado.<br>"
			+"Reduza este valor aumentando os intervalos de monitorização dos respectivos diretórios de parceiros e desactivando também a monitorização para os parceiros em que tal não é necessário. Com um grande número de parceiros, recomenda-se que desactive toda a monitorização de diretórios e crie as tarefas de envio a partir do seu backend utilizando os comandos <i>AS2Send.exe</i> ou <i>as2send.sh</i>, conforme necessário.</HTML>"},
		{"hint.17", "<HTML>Os certificados autenticados contêm uma ligação a uma lista de revogação que pode ser utilizada para declarar este certificado inválido. Por exemplo, se o certificado tiver sido comprometido.<br>"
			+"Ocorreu um problema ao verificar a lista de revogação do seguinte certificado TLS ou o certificado foi revogado:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Informações adicionais sobre este certificado<br><br>"
			+"Pseudónimo: {1}<br>"
			+"Emissor: {2}<br>"
			+"Impressão digital (SHA-1): {3}<br><br><br>"
			+"Tenha em atenção que a verificação automática da CRL pode ser desactivada nas definições.</HTML>"},
		{"hint.18", "<HTML>Os certificados autenticados contêm uma ligação a uma lista de revogação de certificados (CRL), que pode ser utilizada para declarar este certificado inválido. Por exemplo, se o certificado tiver sido comprometido.<br>"
			+"Ocorreu um problema ao verificar a lista de revogação do seguinte certificado enc/sign ou o certificado foi revogado:<br>"
			+"<strong>{0}</strong><br><br>"
			+"Informações adicionais sobre este certificado<br><br>"
			+"Pseudónimo: {1}<br>"
			+"Emissor: {2}<br>"
			+"Impressão digital (SHA-1): {3}<br><br><br>"
			+"Tenha em atenção que a verificação automática da CRL pode ser desactivada nas definições.</HTML>"},
		{"hint.19", "<HTML>Iniciou o cliente e o servidor do produto num único processo. Não é recomendável fazer isto numa operação produtiva. Uma vez que os recursos são atribuídos estaticamente aos programas, tem menos recursos para o funcionamento do servidor e do cliente neste caso.<br><br>"
			+"Inicie primeiro o processo do servidor e depois ligue-se ao cliente separadamente.</HTML>"},
		{"hint.2", "<HTML>O keystore TLS do seu sistema contém várias chaves. No entanto, só pode haver uma - esta é usada como a chave TLS quando o servidor é iniciado.<br>"
			+"Elimine as chaves do armazenamento de chaves TLS até restar apenas uma chave.<br>"
			+"Pode reconhecer as chaves na gestão de certificados pelo símbolo de chave na primeira coluna.<br>"
			+"Após esta alteração, é necessário reiniciar o servidor.</HTML>"},
		{"hint.20", "<HTML>É possível limitar o número de portas e ficheiros abertos por utilizador no seu sistema operativo.<br>"
			+"O utilizador do processo atual só pode utilizar {0} identificadores, o que é demasiado pouco para o funcionamento do servidor. O processo do servidor utiliza atualmente {1} identificadores.<br>"
			+"No Linux, pode ver este valor com \"ulimit -n\".<br><br>"
			+"Por favor, alargue o valor máximo dos identificadores disponíveis para este processo para, pelo menos, {2}.</HTML>"},
		{"hint.3", "<HTML>Os certificados têm um prazo de validade limitado. Normalmente é de um, três ou cinco anos.<br>"
			+"Um certificado que utiliza no seu sistema para segurança da linha TLS já não é válido.<br>"
			+"Não é possível efetuar operações criptográficas com um certificado expirado - por isso, tenha o cuidado de renovar o certificado ou de mandar criar ou autenticar um novo certificado.<br><br>"
			+"<strong>Informações adicionais sobre o certificado:</strong<br><br>"
			+"Pseudónimo: {0}<br>"
			+"Emissor: {1}<br>"
			+"Impressão digital (SHA-1): {2}<br>"
			+"Válido a partir de: {3}<br>"
			+"Válido até: {4}<br><br>"
			+"</HTML>"},
		{"hint.4", "<HTML>Os certificados têm um prazo de validade limitado. Normalmente é de um, três ou cinco anos.<br>"
			+"Um certificado que utiliza no seu sistema para um parceiro para encriptar/desencriptar dados, para assinatura digital ou para verificar uma assinatura digital já não é válido.<br>"
			+"Não é possível efetuar operações criptográficas com um certificado expirado - por isso, tenha o cuidado de renovar o certificado ou de mandar criar ou autenticar um novo certificado.<br><br>"
			+"<strong>Informações adicionais sobre o certificado:</strong<br><br>"
			+"Pseudónimo: {0}<br>"
			+"Emissor: {1}<br>"
			+"Impressão digital (SHA-1): {2}<br>"
			+"Válido a partir de: {3}<br>"
			+"Válido até: {4}<br><br>"
			+"</HTML>"},
		{"hint.5", "<HTML>Nas configurações, é possível definir o tempo que as transacções devem permanecer no sistema.<br>"
			+"Quanto mais transacções permanecerem no sistema, mais recursos serão necessários para a administração.<br>"
			+"Por conseguinte, é necessário utilizar as definições para garantir que nunca haja mais de 30000 transacções no sistema.<br>"
			+"Note-se que não se trata de um sistema de arquivo, mas sim de um adaptador de comunicação.<br>"
			+"O utilizador tem acesso a todos os registos de transacções anteriores através da função de pesquisa integrada do registo do servidor.</HTML>"},
		{"hint.6", "<HTML>Para melhorar o rendimento, é necessário que diferentes tarefas sejam executadas em paralelo no sistema.<br>"
			+"Por conseguinte, é necessário reservar um número correspondente de núcleos de CPU para o processo.</HTML>"},
		{"hint.7", "<HTML>Este programa foi escrito em Java.<br>"
			+"Independentemente da configuração física do computador, é necessário reservar uma quantidade adequada de memória para o processo do servidor. No seu caso, a memória reservada é insuficiente.<br>"
			+"Consulte a ajuda (secção Instalação) - explica como reservar a memória correspondente para cada método de arranque.<br><br>"
			+"Em qualquer caso, certifique-se de que não reserva mais memória para o processo do servidor do que a memória principal do seu sistema. Caso contrário, o software tornar-se-á quase inutilizável porque o sistema está constantemente a trocar memória para o disco rígido.</HTML>"},
		{"hint.8", "<HTML>Fez alterações na configuração de modo a que as ligações de saída não sejam atualmente possíveis.<br>"
			+"Se pretender estabelecer ligações de saída com parceiros, o número de ligações possíveis deve ser definido para pelo menos 1.</HTML>"},
		{"hint.9", "<HTML>Um parceiro de ligação não atribuiu um certificado de encriptação na sua configuração.<br>"
			+"Neste caso, não é possível encriptar as mensagens para o parceiro. Abra a administração do parceiro e atribua um certificado de encriptação ao parceiro.</HTML>"},
	};
}
