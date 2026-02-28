//$Header: /oftp2/de/mendelson/util/security/ResourceBundleKeyStoreUtil_pt.java 2     9/12/24 15:51 Heller $
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
public class ResourceBundleKeyStoreUtil_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"alias.exist", "Já existe uma entrada com o alias \"{0}\" no keystore subjacente."},
		{"privatekey.notfound", "O keystore não contém uma chave privada com o alias \"{0}\"."},
		{"readerror.zipcert", "Não se trata de um certificado válido, mas de um ficheiro zip."},
		{"readerror.invalidcert", "Este não é um certificado válido ou utiliza uma codificação não suportada."},
		{"alias.rename.new.equals.old", "Mudar o nome de uma entrada do keystore: O alias novo e o antigo são idênticos."},
		{"ssh2.algorithmn.not.supported", "A codificação SSH2 não é suportada para chaves do algoritmo \"{0}\". Os algoritmos suportados são: DSA, RSA"},
	};
}
