//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleDialogSendCEM_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleDialogSendCEM_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.certificate", "Certificado:"},
		{"purpose.encryption", "Cifrado"},
		{"cem.not.informed", "Los siguientes socios no fueron informados vía CEM, por favor realice el intercambio de certificados vía correo electrónico o similar: {0}"},
		{"purpose.signature", "Firma digital"},
		{"title", "Intercambiar certificados con socios (CEM)"},
		{"label.receiver", "Beneficiario:"},
		{"cem.informed", "Se ha intentado informar a los siguientes socios a través de CEM, compruebe el éxito en la administración de CEM: {0}"},
		{"label.activationdate", "Fecha de activación:"},
		{"cem.request.failed", "No se ha podido ejecutar la solicitud CEM:\n{0}"},
		{"purpose.ssl", "TLS"},
		{"button.cancel", "Cancelar"},
		{"cem.request.title", "Intercambio de certificados a través de CEM"},
		{"button.ok", "Ok"},
		{"label.initiator", "Estación local:"},
		{"partner.cem.hint", "Los sistemas asociados deben ser compatibles con CEM para ser incluidos aquí"},
		{"cem.request.success", "La solicitud CEM se ha ejecutado correctamente."},
		{"partner.all", "--Todos los socios..."},
	};
}
