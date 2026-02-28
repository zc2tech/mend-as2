//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_pt.java 3     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.sendorder;

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
public class ResourceBundleSendOrderReceiver_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"async.mdn.wait", "Aguardar a MDN assíncrona até {0}."},
		{"as2.send.disabled", "** O número de ligações de saída paralelas está definido para 0 - o sistema não enviará mensagens MDN nem AS2. Por favor, altere esta definição nas definições do servidor se pretender enviar **"},
		{"max.retry.reached", "O número máximo de tentativas ({0}) foi atingido, a transação é terminada."},
		{"outbound.connection.prepare.mdn", "Preparar a ligação MDN de saída para \"{0}\", ligações activas: {1}/{2}."},
		{"outbound.connection.prepare.message", "Preparar a ligação da mensagem AS2 de saída para \"{0}\", ligações activas: {1}/{2}."},
		{"send.connectionsstillopen", "Reduziu o número de ligações de saída para {0}, mas atualmente ainda existem {1} ligações de saída."},
		{"retry", "Tentar uma nova transmissão após {0}s, repetir {1}/{2}."},
	};
}
