//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerResourceCheck_fr.java 3     9/12/24 16:03 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleAS2ServerResourceCheck_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"port.in.use", "Le port {0} est occupé par un autre processus."},
		{"warning.low.maxheap", "Le système n''a trouvé qu''environ {0} de mémoire de tas disponible allouée au processus serveur mendelson AS2. (Ne vous inquiétez pas, c''est environ 10% de moins que ce que vous avez indiqué dans le script de démarrage). Veuillez allouer au moins 1 Go de mémoire de tas au processus serveur AS2 de mendelson."},
		{"warning.few.cpucores", "Le système n''a détecté que {0} coeur(s) de processeur associé(s) au processus serveur mendelson AS2. Avec ce petit nombre de coeurs de processeur, la vitesse d''exécution peut être très faible et certaines fonctions pourraient ne fonctionner que de manière limitée. Veuillez attribuer au moins 4 coeurs de processeur au processus serveur mendelson AS2."},
	};
}
