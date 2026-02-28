//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleCEMOverview_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleCEMOverview_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"header.requestdate", "Fecha de solicitud"},
		{"activity.waitingforanswer", "Esperar respuesta"},
		{"button.responsedetails", "Detalles de la respuesta"},
		{"label.certificate", "Certificado:"},
		{"header.receiver", "A"},
		{"header.activity", "Actividad del sistema"},
		{"button.sendcem", "Nuevo intercambio"},
		{"tab.reasonforrejection", "Motivos del rechazo"},
		{"header.state", "Respuesta"},
		{"title", "Gestión del intercambio de certificados"},
		{"activity.activated", "Ninguno - Activado en {0}"},
		{"button.refresh", "Actualizar"},
		{"button.cancel", "Cancelar"},
		{"button.exit", "Cerrar"},
		{"header.category", "Utilizado para"},
		{"header.alias", "Certificado"},
		{"activity.waitingforprocessing", "Esperar a la tramitación"},
		{"activity.waitingfordate", "Esperar hasta la fecha de activación ({0})"},
		{"tab.certificate", "Información sobre el certificado"},
		{"button.remove", "Borrar"},
		{"button.requestdetails", "Detalles de la consulta"},
		{"header.initiator", "En"},
		{"activity.none", "Ninguno"},
	};
}
