//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames_it.java 3     9/12/24 15:51 H $
package de.mendelson.util.security.cert.gui.keygeneration;

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
public class ResourceBundleDialogSubjectAlternativeNames_it extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	static final Object[][] CONTENTS = {
		{"button.cancel", "Demolizione"},
		{"label.add", "Aggiungi"},
		{"header.name", "Tipo"},
		{"label.del", "Cancellare"},
		{"button.ok", "Ok"},
		{"title", "Gestire i nomi alternativi dei richiedenti"},
		{"header.value", "Valore"},
		{"info", "È possibile utilizzare questa finestra di dialogo per gestire i nomi alternativi dei richiedenti per il processo di generazione delle chiavi (nome alternativo del soggetto). Questi valori sono un''estensione del certificato x.509. Se il partner lo supporta, è possibile inserire qui, ad esempio, domini aggiuntivi per la chiave. In OFTP2, a seconda del partner, potrebbe essere necessario compilare alcuni campi con dati identificativi, ad esempio l''Id Odette del vostro sistema come URL nel formato \"oftp://OdetteId\" e ancora il vostro dominio nel campo del nome DNS."},
	};
}
