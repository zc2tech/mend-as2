//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.util.clientserver.log.search.gui;

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
public class ResourceBundleDialogSearchLogfile_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"no.data.uid", "**No hay datos de registro para el número definido por el usuario \"{0}\" en el periodo de tiempo seleccionado. Por favor, seleccione el número completo definido por el usuario que ha dado a la transmisión como cadena de búsqueda."},
		{"label.uid", "Identificación definida por el usuario"},
		{"label.enddate", "Fin"},
		{"problem.serverside", "Se ha producido un problema en el servidor al buscar en los archivos de registro: [{0}] {1}"},
		{"no.data.mdnid", "**No hay datos de registro para el número MDN \"{0}\" en el periodo de tiempo seleccionado. Utilice como cadena de búsqueda el número MDN completo, que puede encontrar en el registro de una transmisión."},
		{"title", "Buscar en las entradas de registro del servidor"},
		{"label.search", "<html><div style=\"text-align:center\">Registro<br>búsqueda</div></html>"},
		{"label.startdate", "Inicio"},
		{"textfield.preset", "Número de mensaje AS2, número MDN o identificación definida por el usuario"},
		{"button.close", "Cerrar"},
		{"label.messageid", "Número de mensaje"},
		{"no.data.messageid", "**No hay datos de registro para el mensaje AS2 número \"{0}\" en el periodo de tiempo seleccionado. Por favor, utilice el número de mensaje completo como cadena de búsqueda."},
		{"label.info", "<html>Por favor, defina un período de tiempo, introduzca un número completo de mensaje AS2 o el número completo de un MDN para encontrar todas las entradas de registro para ello en el servidor - a continuación, por favor, pulse el botón \"Buscar registro\". Puede definir el número definido por el usuario para cada transacción cuando envíe los datos al servidor en ejecución a través de la línea de comandos.</html"},
		{"label.mdnid", "Número MDN"},
	};
}
