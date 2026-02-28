//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBServer_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.database;

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
public class ResourceBundleDBServer_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"update.successfully", "{0}: A base de dados foi modificada com sucesso para a versão necessária."},
		{"dbserver.shutdown", "O servidor da base de dados foi encerrado"},
		{"update.error.postgres", "FATAL: Não é possível modificar a base de dados da versão {0} para a versão {1}.\nInicie o pgAdmin e elimine a base de dados correspondente."},
		{"info.jdbc", "JDBC: {0}"},
		{"info.clientdriver", "Controlador de cliente: {0}"},
		{"update.error.futureversion", "O sistema encontrou uma versão futura de {0}. A versão da base de dados suportada com esta versão é {1}, mas a base de dados encontrada tem a versão {2}. Não é possível continuar a trabalhar com esta base de dados ou modificá-la."},
		{"upgrade.required", "É necessário efetuar uma atualização.\nExecute o ficheiro as2upgrade.bat ou as2upgrade.sh antes de iniciar o servidor."},
		{"update.progress.version.end", "A atualização de {1} para a versão {0} está pronta."},
		{"dbserver.running.external", "O servidor externo de BD {0} está disponível"},
		{"update.error.hsqldb", "FATAL: Não é possível modificar a base de dados da versão {0} para a versão {1}.\nPor favor, apague todos os ficheiros AS2_DB_*.* correspondentes no diretório de instalação.\nIsto fará com que todos os dados definidos pelo utilizador se percam."},
		{"database.1", "Base de dados de configuração"},
		{"update.progress.version.start", "Iniciar a atualização de {1} para a versão {0}..."},
		{"database.2", "Base de dados em tempo de execução"},
		{"dbserver.running.embedded", "Servidor DB integrado {0} em execução"},
		{"update.versioninfo", "Atualização automática da base de dados: A versão da base de dados encontrada é {0}, a versão necessária é {1}."},
		{"info.serveridentification", "Identificação do servidor: {0}"},
		{"dbserver.startup", "Iniciar o servidor de BD integrado..."},
		{"update.error.mysql", "FATAL: Não é possível modificar a base de dados da versão {0} para a versão {1}.\nPor favor, inicie o MySQLWorkbench e elimine a base de dados correspondente."},
		{"update.error.oracledb", "FATAL: Não é possível modificar a base de dados da versão {0} para a versão {1}.\nInicie o Oracle SQL Developer e elimine a base de dados."},
		{"update.notfound", "Para a atualização, o ficheiro update{0}to{1}.sql e/ou o ficheiro Update{0}to{1}.class têm de existir no diretório (de recursos) {2}."},
		{"info.host", "Anfitrião: {0}"},
		{"update.progress", "Atualização incremental da base de dados iniciada..."},
		{"info.user", "Utilizador: {0}"},
		{"module.name", "[DATABASE]"},
	};
}
