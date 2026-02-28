//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleKeystoreStorage_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleKeystoreStorage_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error.delete.notloaded", "A entrada não pôde ser eliminada, o keystore subjacente ainda não foi carregado."},
		{"error.nodata", "Não foi possível ler o registo de chaves: Não há dados disponíveis"},
		{"moved.keystore.to.db.title", "Importação de um ficheiro keystore ({0})"},
		{"error.readaccess", "Não foi possível ler o registo de chaves: Não é possível o acesso de leitura a \"{0}\"."},
		{"moved.keystore.reason.commandline", "A importação foi acionada por um parâmetro da linha de comandos quando o servidor foi iniciado."},
		{"error.save.notloaded", "O Keystore não pode ser guardado, ainda não foi carregado."},
		{"moved.keystore.to.db", "Importar os dados do repositório de chaves de \"{0}\" para o sistema - a utilização pretendida é {1}. Todas as chaves/certificados existentes foram eliminados."},
		{"error.save", "Não foi possível guardar os dados do keystore."},
		{"error.empty", "Não foi possível ler o registo de chaves: Os dados do registo de chaves devem ser superiores a 0."},
		{"keystore.read.failure", "O sistema não conseguiu ler os certificados subjacentes. Mensagem de erro: \"{0}\". Verifique se está a utilizar a palavra-passe correta para o armazenamento de chaves."},
		{"moved.keystore.reason.initial", "A importação foi efectuada porque não existe atualmente uma memória de chaves interna do sistema. Este é um processo inicial."},
		{"error.filexists", "Não foi possível ler o registo de chaves: O ficheiro do keystore \"{0}\" não existe."},
		{"error.notafile", "Não foi possível ler o registo de chaves: O ficheiro do keystore \"{0}\" não é um ficheiro."},
	};
}
