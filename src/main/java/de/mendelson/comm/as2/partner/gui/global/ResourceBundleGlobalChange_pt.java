//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui.global;

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
public class ResourceBundleGlobalChange_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"partnersetting.changed", "As definições foram alteradas para {0} parceiros."},
		{"label.pollinterval", "Intervalo de sondagem do diretório de todos os parceiros"},
		{"button.ok", "Fechar"},
		{"partnersetting.notchanged", "As definições não foram alteradas - valor incorreto"},
		{"label.dirpoll", "Efetuar uma sondagem de diretório para todos os parceiros"},
		{"button.set", "Conjunto"},
		{"info.text", "<HTML>Com esta caixa de diálogo, pode ajustar simultaneamente os parâmetros de todos os parceiros para os valores definidos. Se tiver premido \"Definir\", o respetivo valor para <strong>Todos</strong> os parceiros é substituído.</HTML>"},
		{"title", "Alterações globais para todos os parceiros"},
		{"label.maxpollfiles", "Número máximo de ficheiros de todos os parceiros por processo de sondagem"},
	};
}
