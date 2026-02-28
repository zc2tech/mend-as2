//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender_pt.java 3     17/01/25 8:41 Heller $
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
public class ResourceBundleSendOrderSender_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"sendoder.sendfailed", "Ocorreu um problema ao processar um pedido de envio: [{0}] \"{1}\" - os dados não foram transmitidos ao parceiro."},
		{"message.packed", "Mensagem AS2 de saída de \"{0}\" para o destinatário \"{1}\" criada em {3}, tamanho dos dados brutos: {2}, id definido pelo utilizador: \"{4}\""},
	};
}
