//$Header: /oftp2/de/mendelson/util/modulelock/ResourceBundleModuleLock_fr.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.modulelock;

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
public class ResourceBundleModuleLock_fr extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"Partner management", "Gestion des partenaires"},
		{"TLS keystore", "Gestion des certificats (TLS)"},
		{"configuration.locked.otherclient", "Le module {0} est ouvert exclusivement par un autre client,\nVous ne pouvez actuellement pas y apporter de modifications.\nDétails de l''autre client :\nIP : {1}\nUtilisateur : {2}\nProcessus id : {3}"},
		{"ENC/SIGN keystore", "Gestion des certificats (cryptage/signature)"},
		{"configuration.changed.otherclient", "Un autre client a peut-être effectué des modifications dans le module {0}.\nVeuillez ouvrir à nouveau cette interface de configuration pour recharger la configuration actuelle."},
		{"Server settings", "Paramètres du serveur"},
		{"modifications.notallowed.message", "Les modifications ne sont pas possibles pour le moment"},
	};
}
