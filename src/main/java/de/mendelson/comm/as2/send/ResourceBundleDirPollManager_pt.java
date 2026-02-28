//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_pt.java 4     6/02/25 8:23 Heller $
package de.mendelson.comm.as2.send;

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
* @version $Revision: 4 $
*/
public class ResourceBundleDirPollManager_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"warning.ro", "[Monitorização de diretórios] O ficheiro de saída {0} está protegido contra escrita, este ficheiro é ignorado."},
		{"warning.notcomplete", "[Monitorização de diretórios] O ficheiro fonte {0} ainda não está completamente disponível, o ficheiro é ignorado."},
		{"title.list.polls.stopped", "Foram encerradas as seguintes actividades de acompanhamento"},
		{"processing.file", "Processar o ficheiro \"{0}\" para a relação \"{1}/{2}\"."},
		{"none", "Nenhum"},
		{"warning.noread", "[Monitorização de diretórios] Não é possível o acesso de leitura ao ficheiro de origem {0}, o ficheiro é ignorado."},
		{"poll.started", "A monitorização do diretório para a relação \"{0}/{1}\" foi iniciada. Ignorar: \"{2}\". Intervalo: {3}s"},
		{"poll.modified", "[Monitorização de diretórios] As definições de parceiro para a relação \"{0}/{1}\" foram alteradas."},
		{"title.list.polls.started", "Foram iniciados os seguintes programas de monitorização"},
		{"poll.stopped.notscheduled", "[Monitorização do diretório] O sistema tentou parar a monitorização do diretório para \"{0}/{1}\" - mas não houve monitorização."},
		{"processing.file.error", "Erro de processamento do ficheiro \"{0}\" para a relação \"{1}/{2}\": \"{3}\"."},
		{"title.list.polls.running", "Resumo dos diretórios monitorizados:"},
		{"poll.log.polling", "[Monitorização de diretórios] {0}->{1}: Verificar o diretório \"{2}\" para novos ficheiros"},
		{"manager.status.modified", "A monitorização do diretório foi alterada monitorização do diretório, {0} diretórios são monitorizados"},
		{"poll.stopped", "A monitorização do diretório para a relação \"{0}/{1}\" foi interrompida."},
		{"poll.log.wait", "[Monitorização de diretórios] {0}->{1}: Próximo processo de sondagem em {2}s ({3})"},
		{"messagefile.deleted", "O ficheiro \"{0}\" foi eliminado e transferido para a fila de processamento do servidor."},
	};
}
