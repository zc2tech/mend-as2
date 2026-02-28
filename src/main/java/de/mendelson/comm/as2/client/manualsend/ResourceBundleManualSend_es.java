//$Header: /as2/de/mendelson/comm/as2/client/manualsend/ResourceBundleManualSend_es.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.client.manualsend;

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
public class ResourceBundleManualSend_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"label.testdata", "Enviar datos de prueba"},
		{"label.partner", "Receptor"},
		{"send.success", "El archivo se ha transferido correctamente al proceso de envío."},
		{"button.cancel", "Cancelar"},
		{"label.selectfile", "Seleccione el archivo que desea enviar"},
		{"label.filename.hint", "Archivo para enviar a su pareja"},
		{"label.localstation", "Transmisor"},
		{"button.browse", "Visite"},
		{"button.ok", "Ok"},
		{"send.failed", "Debido a un error, el archivo no pudo ser transferido al proceso de envío."},
		{"title", "Envío manual de expedientes"},
		{"label.filename", "Enviar archivo"},
	};
}
