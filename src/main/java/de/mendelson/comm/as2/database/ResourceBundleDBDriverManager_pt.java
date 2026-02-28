//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBDriverManager_pt.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleDBDriverManager_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"database.creation.failed.1", "Ocorreu um erro ao criar a base de dados de configuração"},
		{"database.creation.failed.2", "Ocorreu um erro ao criar a base de dados de tempo de execução"},
		{"creating.database.1", "Criar base de dados de configuração"},
		{"creating.database.2", "Criar base de dados em tempo de execução"},
		{"creating.database.details", "Anfitrião: {0}, Porta: {1}, Utilizador: {2}, Nome da BD: {3}"},
		{"database.creation.success.1", "A base de dados de configuração foi criada com êxito"},
		{"database.creation.success.2", "A base de dados de tempo de execução foi criada com sucesso"},
		{"module.name", "[DATABASE]"},
	};
}
