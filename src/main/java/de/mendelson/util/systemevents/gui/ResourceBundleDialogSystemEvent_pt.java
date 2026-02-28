//$Header: /oftp2/de/mendelson/util/systemevents/gui/ResourceBundleDialogSystemEvent_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.systemevents.gui;

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
public class ResourceBundleDialogSystemEvent_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.close", "Fechar"},
		{"header.timestamp", "Carimbo de data/hora"},
		{"label.id", "Número do evento"},
		{"label.category", "Categoria"},
		{"label.enddate", "Fim"},
		{"label.freetext", "Texto de pesquisa"},
		{"category.all", "-- Todos"},
		{"label.type", "Tipo"},
		{"title", "Visualização de eventos do sistema"},
		{"label.search", "Pesquisa de eventos"},
		{"label.resetfilter", "Reiniciar"},
		{"label.host", "Anfitrião"},
		{"header.category", "Categoria"},
		{"label.startdate", "Início"},
		{"label.freetext.hint", "Pesquisa de número de evento ou de texto"},
		{"label.user", "Proprietário"},
		{"header.type", "Tipo"},
		{"no.data", "Não existe nenhum evento do sistema que corresponda à seleção de data/tipo atual."},
		{"user.server.process", "Processo do servidor"},
		{"label.date", "data"},
	};
}
