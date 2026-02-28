//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleCEMOverview_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.cem.gui;

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
public class ResourceBundleCEMOverview_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.requestdate", "Data do pedido"},
		{"activity.waitingforanswer", "Esperar pela resposta"},
		{"button.responsedetails", "Pormenores da resposta"},
		{"label.certificate", "Certificado:"},
		{"header.receiver", "Para"},
		{"header.activity", "Atividade do sistema"},
		{"button.sendcem", "Nova troca"},
		{"tab.reasonforrejection", "Motivos de rejeição"},
		{"header.state", "Resposta"},
		{"title", "Gestão da troca de certificados"},
		{"activity.activated", "Nenhum - Ativado em {0}"},
		{"button.refresh", "Atualizar"},
		{"button.cancel", "Cancelar"},
		{"button.exit", "Fechar"},
		{"header.category", "Utilizado para"},
		{"header.alias", "Certificado"},
		{"activity.waitingforprocessing", "Aguardar o processamento"},
		{"activity.waitingfordate", "Esperar até à data de ativação ({0})"},
		{"tab.certificate", "Informações sobre o certificado"},
		{"button.remove", "Eliminar"},
		{"button.requestdetails", "Detalhes do inquérito"},
		{"header.initiator", "De"},
		{"activity.none", "Nenhum"},
	};
}
