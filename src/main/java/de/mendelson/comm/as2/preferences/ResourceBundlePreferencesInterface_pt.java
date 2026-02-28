//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesInterface_pt.java 2     9/12/24 16:03 Heller $
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
public class ResourceBundlePreferencesInterface_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.outboundstatusfiles.help", "<HTML><strong> Ficheiros de estado para transacções de saída</strong><br><br>"
			+"Se esta opção for activada, é gravado um ficheiro de estado no diretório \"outboundstatus\" para cada transação de saída.<br>"
			+"Este ficheiro é utilizado para fins de integração e contém informações sobre a respectiva transação. Inclui, por exemplo, o estado da transação, o número da mensagem, a identificação do remetente e do destinatário.<br>"
			+"O nome do ficheiro de estado contém o número da mensagem e termina com \".sent.state\". Depois de enviar dados, pode analisar este ficheiro e ver o estado da transação.</HTML>"},
		{"label.checkrevocationlists.help", "<HTML><strong>Certificados: Verificar listas de revogação</strong><br><br>"
			+"Uma lista de revogação é uma lista de certificados que foram declarados inválidos devido a várias preocupações ou problemas de segurança. Estes problemas podem ser, por exemplo, o comprometimento da chave privada, a perda do certificado ou a suspeita de atividade fraudulenta. As listas de revogação são geridas por autoridades de certificação ou outras entidades de confiança que estão autorizadas a emitir certificados. A verificação das listas de revogação é importante para garantir que os certificados utilizados numa ligação ou para uma operação criptográfica são válidos e fiáveis. Um certificado que conste de uma lista de revogação não deve continuar a ser utilizado para operações criptográficas, uma vez que é potencialmente inseguro e pode colocar em risco a integridade da comunicação.<br><br>"
			+"Esta definição pode ser utilizada para determinar se o sistema também verifica as listas de revogação na verificação da configuração.</HTML>"},
		{"label.showsecurityoverwrite", "Gestão de parceiros: Substituir as definições de segurança da estação local"},
		{"label.showquota", "Gestão de parceiros: visualização da configuração da notificação (quota)"},
		{"label.cem", "Permitir a troca de certificados (CEM)"},
		{"label.outboundstatusfiles", "Ficheiros de estado para transacções de saída"},
		{"label.showhttpheader", "Gestão de parceiros: visualização da configuração do cabeçalho HTTP"},
		{"autoimport.tls.help", "<HTML><strong>Certificados TLS: Importação automática se forem alterados</strong><br><br>"
			+"Se uma ligação de parceiro for efectuada através de HTTPS (TLS, o URL começa por \"https\"), pode verificar regularmente se o certificado TLS do parceiro foi alterado. Se tiver sido alterado e ainda não estiver no seu sistema, é importado automaticamente com toda a cadeia de autenticação.<br>"
			+"O sistema verifica os certificados dos parceiros de 15 em 15 minutos. Por conseguinte, pode demorar algum tempo até que uma alteração ao certificado TLS de um parceiro seja reconhecida.<br><br>"
			+"Também é possível efetuar este processo manualmente, realizando um teste de ligação a um parceiro e, em seguida, importando os certificados TLS em falta.<br><br>"
			+"Tenha em atenção que esta é uma definição problemática a nível de segurança, porque confia automaticamente num certificado que é encontrado - sem perguntar.</HTML>"},
		{"label.checkrevocationlists", "Certificados: verificar listas de revogação"},
		{"autoimport.tls", "Certificados TLS: importação automática se forem alterados"},
		{"label.showsecurityoverwrite.help", "<HTML><strong>Substituir as definições de segurança da estação local</strong><br><br>"
			+"Se ativar esta opção, é apresentado um separador adicional para cada parceiro na gestão de parceiros.<br>"
			+"Isto permite-lhe definir as chaves privadas que são utilizadas para este parceiro na entrada e na saída em qualquer caso - independentemente das definições da respectiva estação local.<br>"
			+"Esta opção permite-lhe utilizar chaves privadas diferentes para cada parceiro na mesma estação local.<br><br>"
			+"Esta é uma opção para compatibilidade com outros produtos AS2 - alguns sistemas têm exatamente estes requisitos, mas requerem uma configuração de relações de parceiros e não de parceiros individuais.</HTML>"},
		{"label.showhttpheader.help", "<HTML><strong>Apresentação da configuração do cabeçalho HTTP</strong><br><br>"
			+"Se esta opção for activada, é apresentado um separador adicional na administração de parceiros para cada parceiro, no qual é possível definir cabeçalhos HTTP definidos pelo utilizador para enviar dados para este parceiro.</HTML>"},
	};
}
