//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames_es.java 2     9/12/24 15:51 H $
package de.mendelson.util.security.cert.gui.keygeneration;

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
public class ResourceBundleDialogSubjectAlternativeNames_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"button.cancel", "Demolición"},
		{"label.add", "Añadir"},
		{"header.name", "Tipo"},
		{"label.del", "Borrar"},
		{"button.ok", "Ok"},
		{"title", "Gestionar nombres alternativos de candidatos"},
		{"header.value", "Valor"},
		{"info", "Puede utilizar este cuadro de diálogo para gestionar los nombres alternativos del solicitante para el proceso de generación de claves (nombre alternativo del sujeto). Estos valores son una extensión del certificado x.509. Si su interlocutor lo admite, puede introducir aquí dominios adicionales para su clave, por ejemplo. En OFTP2, dependiendo del socio, puede ser necesario rellenar algunos campos con datos de identificación, por ejemplo el Odette Id de su sistema como URL en el formato \"oftp://OdetteId\" y de nuevo su dominio en el campo de nombre DNS."},
	};
}
