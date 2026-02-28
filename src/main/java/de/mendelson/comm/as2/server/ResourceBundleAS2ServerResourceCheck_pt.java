//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerResourceCheck_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleAS2ServerResourceCheck_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"port.in.use", "A porta {0} está ocupada por outro processo."},
		{"warning.low.maxheap", "O sistema encontrou apenas cerca de {0} memória heap disponível alocada para o processo do servidor mendelson AS2. (Não se preocupe, isso é cerca de 10% menos do que você especificou no script de inicialização). Por favor, aloque pelo menos 1GB de memória heap para o processo do servidor mendelson AS2."},
		{"warning.few.cpucores", "O sistema reconheceu apenas {0} núcleo(s) de processador atribuído(s) ao processo do servidor mendelson AS2. Com este número baixo de núcleos de processador, a velocidade de execução pode ser muito baixa e algumas funções podem funcionar apenas de forma limitada. Por favor, atribua pelo menos 4 núcleos de processador ao processo do servidor mendelson AS2."},
	};
}
