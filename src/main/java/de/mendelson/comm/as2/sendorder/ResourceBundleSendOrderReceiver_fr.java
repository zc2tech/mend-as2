//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_fr.java 11    17/01/25 8:41 Heller $
package de.mendelson.comm.as2.sendorder;

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
* @version $Revision: 11 $
*/
public class ResourceBundleSendOrderReceiver_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"async.mdn.wait", "Attendre MDN asynchrone jusqu''à {0}."},
		{"as2.send.disabled", "** Le nombre de connexions sortantes parallèles est réglé sur 0 - le système n''enverra pas de messages MDN ou AS2. Veuillez modifier ce paramètre dans les réglages du serveur si vous souhaitez envoyer **."},
		{"max.retry.reached", "Le nombre maximal de tentatives ({0}) a été atteint, la transaction est terminée."},
		{"outbound.connection.prepare.mdn", "Préparer une connexion MDN sortante vers \"{0}\", connexions actives : {1}/{2}."},
		{"outbound.connection.prepare.message", "Préparer une connexion de message AS2 sortante vers \"{0}\", connexions actives : {1}/{2}."},
		{"send.connectionsstillopen", "Vous avez réduit le nombre de connexions sortantes à {0}, mais il y a encore {1} connexions sortantes à l''heure actuelle."},
		{"retry", "Essayez une nouvelle transmission après {0}s, répétez {1}/{2}."},
	};
}
