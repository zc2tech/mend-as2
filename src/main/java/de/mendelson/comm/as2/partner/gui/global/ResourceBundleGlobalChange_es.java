//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange_es.java 2     9/12/24 16:02 Heller $
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
public class ResourceBundleGlobalChange_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"partnersetting.changed", "Se han modificado los ajustes para {0} interlocutores."},
		{"label.pollinterval", "Intervalo de sondeo del directorio de todos los socios"},
		{"button.ok", "Cerrar"},
		{"partnersetting.notchanged", "No se ha modificado la configuración - valor incorrecto"},
		{"label.dirpoll", "Realizar un sondeo de directorio para todos los socios"},
		{"button.set", "Establecer"},
		{"info.text", "<HTML>Puede utilizar este cuadro de diálogo para ajustar los parámetros de todos los interlocutores a los valores definidos al mismo tiempo. Si ha pulsado \"Establecer\", se sobrescribirá el valor respectivo para <strong>TODOS</strong> los interlocutores.</HTML>"},
		{"title", "Cambios globales para todos los socios"},
		{"label.maxpollfiles", "Número máximo de archivos de todos los socios por proceso de sondeo"},
	};
}
