//$Header: /oftp2/de/mendelson/util/systemevents/gui/ResourceBundleDialogSystemEvent_es.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleDialogSystemEvent_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.close", "Cerrar"},
		{"header.timestamp", "Marca de tiempo"},
		{"label.id", "Número del acontecimiento"},
		{"label.category", "Categoría"},
		{"label.enddate", "Fin"},
		{"label.freetext", "Buscar texto"},
		{"category.all", "-- Todos --"},
		{"label.type", "Tipo"},
		{"title", "Vista de los eventos del sistema"},
		{"label.search", "Búsqueda de eventos"},
		{"label.resetfilter", "Restablecer"},
		{"label.host", "Anfitrión"},
		{"header.category", "Categoría"},
		{"label.startdate", "Inicio"},
		{"label.freetext.hint", "Búsqueda por número de evento o texto"},
		{"label.user", "Propietario"},
		{"header.type", "Tipo"},
		{"no.data", "No hay ningún evento del sistema que coincida con la selección de fecha/tipo actual."},
		{"user.server.process", "Proceso de servidor"},
		{"label.date", "fecha"},
	};
}
