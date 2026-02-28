//$Header: /oftp2/de/mendelson/util/clientserver/ResourceBundleGUIClient_pt.java 2     9/12/24 15:50 Heller $
package de.mendelson.util.clientserver;

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
public class ResourceBundleGUIClient_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"error", "Problema: {0}"},
		{"logout.from.server", "Foi efectuado um logout do servidor"},
		{"password.required", "Erro de início de sessão, é necessária uma palavra-passe para o utilizador {0}."},
		{"login.failure", "O início de sessão como utilizador \"{0}\" falhou"},
		{"client.received.unprocessed.message", "O servidor enviou uma mensagem que não foi processada pelo cliente: {0}"},
		{"connectionrefused.message", "{0}: Não é possível estabelecer ligação. Certifique-se de que o servidor está a funcionar."},
		{"login.failed.client.incompatible.title", "O login foi rejeitado"},
		{"connection.closed.title", "Desconexão local"},
		{"connection.closed.message", "A ligação cliente-servidor local foi desligada do servidor"},
		{"connection.closed", "A ligação cliente-servidor local foi desligada do servidor"},
		{"connection.success", "Cliente ligado a {0}"},
		{"login.failed.client.incompatible.message", "O servidor informa que este cliente não tem a versão correta.\nUtilize o cliente que corresponde ao servidor."},
		{"login.success", "Iniciou sessão como utilizador \"{0}\""},
		{"connectionrefused.title", "Problema de ligação"},
	};
}
