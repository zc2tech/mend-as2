//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundlePartnerTLSCertificateChangedController_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

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
public class ResourceBundlePartnerTLSCertificateChangedController_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"autoimport.tls.check.started", "A importação automática de certificados TLS de parceiros modificados foi activada."},
		{"import.success.event.header", "Importação automática de um certificado TLS"},
		{"import.success.event.body", "O sistema está configurado de modo a verificar regularmente se os parceiros ligados por TLS alteraram o seu certificado TLS. Se for esse o caso e o certificado TLS não existir no seu gestor local de certificados TLS, é importado automaticamente.\nO sistema encontrou um novo certificado para o parceiro \"{0}\" sob o URL \"{1}\" e importou-o com sucesso para o seu gestor de certificados TLS com o pseudónimo \"{2}\"."},
		{"import.failed", "O certificado TLS do parceiro {0} não pôde ser importado automaticamente: {1}"},
		{"autoimport.tls.check.stopped", "A importação automática de certificados TLS de parceiros modificados foi desactivada."},
		{"module.name", "Exame do certificado TLS"},
		{"import.success", "O certificado TLS \"{0}\" para o parceiro [{1}] foi importado automaticamente."},
	};
}
