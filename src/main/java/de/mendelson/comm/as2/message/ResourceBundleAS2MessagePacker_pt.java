//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker_pt.java 4     17/01/25 8:41 Heller $
package de.mendelson.comm.as2.message;

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
public class ResourceBundleAS2MessagePacker_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"mdn.signed", "A MDN de saída foi assinada com o algoritmo \"{0}\", o alias da chave é \"{1}\" da estação local \"{2}\"."},
		{"message.compressed.unknownratio", "Os dados de saída do utilizador foram comprimidos."},
		{"message.creation.error", "A mensagem com o ID de mensagem \"{0}\" não pôde ser criada: {1}. Este é um problema que já ocorreu quando a estrutura de mensagens de saída foi criada no seu sistema - não tem nada a ver com o sistema do seu parceiro e não foi feita qualquer tentativa de estabelecer uma ligação com o seu parceiro."},
		{"signature.no.aipa", "O processo de assinatura não utiliza o atributo Algorithm Identifier Protection na assinatura (tal como definido na configuração) - isto é inseguro!"},
		{"mdn.creation.start", "Criar MDN de saída, definir o ID da mensagem como \"{0}\"."},
		{"message.compressed", "Os dados do utilizador de saída foram comprimidos de {0} para {1}."},
		{"mdn.details", "Pormenores da MDN de saída: {0}"},
		{"mdn.created", "MDN de saída criado para a mensagem AS2 \"{0}\", estado definido para [{1}]."},
		{"mdn.notsigned", "A MDN de saída não foi assinada."},
		{"message.signed", "A mensagem de saída foi assinada digitalmente com o algoritmo \"{1}\", tendo sido utilizada a chave com o pseudónimo \"{0}\" da estação local \"{2}\"."},
		{"message.encrypted", "A mensagem de saída foi encriptada com o algoritmo {1}, foi utilizado o certificado com o pseudónimo \"{0}\" do parceiro remoto \"{2}\"."},
		{"message.creation.start", "Criar mensagem AS2 de saída, definir o ID da mensagem como \"{0}\"."},
		{"message.notsigned", "A mensagem de saída não foi assinada digitalmente."},
		{"message.notencrypted", "A mensagem enviada não foi encriptada."},
	};
}
