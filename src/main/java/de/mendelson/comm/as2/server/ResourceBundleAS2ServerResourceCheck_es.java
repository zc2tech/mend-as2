//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerResourceCheck_es.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleAS2ServerResourceCheck_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"port.in.use", "El puerto {0} está ocupado por otro proceso."},
		{"warning.low.maxheap", "El sistema ha encontrado sólo alrededor de {0} memoria heap disponible asignada al proceso servidor mendelson AS2. (No se preocupe, esto es aproximadamente 10% menos de lo que especificó en el script de inicio). Por favor, asigne al menos 1GB de memoria heap al proceso servidor mendelson AS2."},
		{"warning.few.cpucores", "El sistema sólo tiene reconocido {0} núcleo(s) de procesador asignado(s) al proceso servidor mendelson AS2. Con este bajo número de núcleos de procesador, la velocidad de ejecución puede ser muy baja y algunas funciones pueden trabajar sólo de forma limitada. Por favor, asigne al menos 4 núcleos de procesador al proceso del servidor mendelson AS2."},
	};
}
