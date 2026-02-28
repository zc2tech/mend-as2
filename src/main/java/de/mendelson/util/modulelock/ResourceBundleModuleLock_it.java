//$Header: /oftp2/de/mendelson/util/modulelock/ResourceBundleModuleLock_it.java 3     9/12/24 15:51 Heller $
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
* @version $Revision: 3 $
*/
public class ResourceBundleModuleLock_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"Partner management", "Gestione dei partner"},
		{"TLS keystore", "Gestione dei certificati (TLS)"},
		{"configuration.locked.otherclient", "Il modulo {0} è aperto esclusivamente da un altro cliente,\nAl momento non è possibile apportare alcuna modifica.\nDettagli dell''altro client:\nIP: {1}\nUtente: {2}\nId processo: {3}"},
		{"ENC/SIGN keystore", "Gestione dei certificati (crittografia/firma)"},
		{"configuration.changed.otherclient", "Un altro cliente potrebbe aver apportato modifiche al modulo {0}.\nAprire nuovamente questa interfaccia di configurazione per ricaricare la configurazione corrente."},
		{"Server settings", "Impostazioni del server"},
		{"modifications.notallowed.message", "Al momento non è possibile apportare modifiche"},
	};
}
