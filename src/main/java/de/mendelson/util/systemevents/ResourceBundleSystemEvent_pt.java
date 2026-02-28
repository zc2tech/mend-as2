//$Header: /as4/de/mendelson/util/systemevents/ResourceBundleSystemEvent_pt.java 3     15/01/25 10:18 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEvent_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"type.100000", "Não especificado"},
		{"category.400", "Certificado"},
		{"type.406", "Troca de certificados (pedido de entrada)"},
		{"type.802", "Quota atingida"},
		{"type.407", "Certificado (importação do Keystore)"},
		{"type.404", "Troca de certificados"},
		{"type.800", "Quota"},
		{"type.405", "O certificado expira"},
		{"type.801", "Quota atingida"},
		{"type.402", "Certificado (pseudónimo alterado)"},
		{"type.403", "Certificado (suprimido)"},
		{"category.800", "Contingente"},
		{"type.400", "Certificado"},
		{"type.401", "Certificado (acrescentado)"},
		{"type.300", "Transação"},
		{"category.1000", "Processamento de dados"},
		{"category.1400", "Interface XML"},
		{"type.1202", "Criar diretório"},
		{"type.1201", "Ficheiro (apagar)"},
		{"type.1200", "Operação de ficheiro"},
		{"type.1204", "Ficheiro (cópia)"},
		{"type.1203", "Ficheiro (mover)"},
		{"category.500", "Base de dados"},
		{"category.100", "Componente do servidor"},
		{"type.705", "Parceiro (adicionado)"},
		{"type.703", "Parceiro (modificado)"},
		{"origin.2", "Utilizadores"},
		{"type.704", "Parceiro (eliminado)"},
		{"origin.3", "Transação"},
		{"type.701", "Alteração da configuração"},
		{"type.305", "Transação (cancelar)"},
		{"type.702", "Verificação da configuração"},
		{"type.306", "Transação (reenvio)"},
		{"origin.1", "Sistema"},
		{"type.303", "Transação (mensagem duplicada)"},
		{"category.900", "Notificação"},
		{"category.100000", "Outros"},
		{"type.700", "Configuração"},
		{"type.304", "Transação (eliminar)"},
		{"type.301", "Erro de transação"},
		{"type.302", "Transação (reenvio rejeitado)"},
		{"type.200", "Ligação"},
		{"type.201", "Teste de ligação"},
		{"category.1100", "Ativação"},
		{"category.1500", "Interface REST"},
		{"type.1102", "Termo da licença"},
		{"type.1101", "Atualização da licença"},
		{"type.1100", "Licença"},
		{"type.1503", "Eliminar o certificado"},
		{"type.1502", "Configuração do certificado"},
		{"type.1501", "Adicionar certificado"},
		{"type.1500", "REST"},
		{"type.1507", "Enviar encomenda"},
		{"type.1506", "Eliminar parceiro"},
		{"type.1505", "Configuração de parceiros"},
		{"type.1504", "Adicionar parceiro"},
		{"category.200", "Ligação"},
		{"type.101", "Início do servidor"},
		{"type.102", "Servidor em funcionamento"},
		{"type.100", "Encerramento do servidor"},
		{"severity.1", "Informações"},
		{"severity.2", "Aviso"},
		{"severity.3", "Erro"},
		{"type.1000", "Processamento de dados"},
		{"category.1200", "Operação de ficheiro"},
		{"type.1400", "XML"},
		{"type.1002", "Pós-processamento"},
		{"type.1001", "Pré-processamento"},
		{"type.1402", "Configuração de parceiros"},
		{"type.1401", "Configuração do certificado"},
		{"type.112", "Encerramento do servidor TRFC"},
		{"type.113", "O programador é iniciado"},
		{"type.110", "Servidor TRFC em execução"},
		{"type.199", "Componente do servidor"},
		{"type.111", "Estado do servidor TRFC"},
		{"category.700", "Configuração"},
		{"category.300", "Transação"},
		{"type.901", "Notificação (envio bem sucedido)"},
		{"type.109", "Início do servidor TRFC"},
		{"type.902", "Notificação (falha na expedição)"},
		{"type.503", "Base de dados (inicialização)"},
		{"type.107", "Servidor HTTP em execução"},
		{"type.900", "Notificação"},
		{"type.108", "Encerramento do servidor HTTP"},
		{"type.501", "Criação da base de dados"},
		{"type.105", "Encerramento do servidor de BD"},
		{"type.502", "Base de dados (Atualização)"},
		{"type.106", "Início do servidor HTTP"},
		{"type.103", "Início do servidor DB"},
		{"type.500", "Base de dados"},
		{"type.104", "Servidor DB em execução"},
		{"category.1300", "Funcionamento do cliente"},
		{"type.1301", "Início de sessão do utilizador (sucesso)"},
		{"type.1300", "Cliente"},
		{"type.1303", "Separação de utilizadores"},
		{"type.1302", "Início de sessão do utilizador (falhou)"},
		{"type.116", "Monitorização de diretórios (estado alterado)"},
		{"type.117", "Porta de receção"},
		{"type.114", "Programador em execução"},
		{"type.115", "Encerramento do programador"},
                {"type." + SystemEvent.TYPE_DATABASE_ROLLBACK, "Reversão transação"},
	};
}
