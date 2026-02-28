//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender_fr.java 10    17/01/25 8:41 Heller $
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
* @version $Revision: 10 $
*/
public class ResourceBundleSendOrderSender_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"sendoder.sendfailed", "Un problème s''est produit lors du traitement d''une demande d''envoi : [{0}] \"{1}\" - les données n''ont pas été transmises au partenaire."},
		{"message.packed", "Message AS2 sortant de \"{0}\" pour le destinataire \"{1}\" créé en {3}, taille des données brutes : {2}, id défini par l''utilisateur : \"{4}\"."},
	};
}
