//$Header: /oftp2/de/mendelson/util/security/ResourceBundleKeyStoreUtil_es.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security;

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
public class ResourceBundleKeyStoreUtil_es extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"alias.exist", "Ya existe una entrada con el alias \"{0}\" en el almacén de claves subyacente."},
		{"privatekey.notfound", "El almacén de claves no contiene una clave privada con el alias \"{0}\"."},
		{"readerror.zipcert", "Esto no es un certificado válido, sino un archivo zip."},
		{"readerror.invalidcert", "Este certificado no es válido o utiliza una codificación no admitida."},
		{"alias.rename.new.equals.old", "Renombra una entrada del almacén de claves: El alias nuevo y el antiguo son idénticos."},
		{"ssh2.algorithmn.not.supported", "La codificación SSH2 no es compatible con claves de algoritmo \"{0}\". Los algoritmos soportados son: DSA, RSA"},
	};
}
