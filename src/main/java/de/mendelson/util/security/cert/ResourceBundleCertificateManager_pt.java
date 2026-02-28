//$Header: /oftp2/de/mendelson/util/security/cert/ResourceBundleCertificateManager_pt.java 2     9/12/24 15:51 Heller $
package de.mendelson.util.security.cert;

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
public class ResourceBundleCertificateManager_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"certificate.not.found.fingerprint.withinfo", "O certificado com a impressão digital SHA-1 \"{0}\" não existe no sistema. ({1})"},
		{"keystore.PKCS12", "Registo de chaves de encriptação/assinatura"},
		{"event.certificate.deleted.body", "O seguinte certificado foi eliminado do sistema:\n\n{0}"},
		{"certificate.not.found.subjectdn.withinfo", "O certificado com o subjectDN \"{0}\" não existe no sistema. ({1})"},
		{"certificate.not.found.ski.withinfo", "O certificado com o identificador de chave de assunto \"{0}\" não existe no sistema. ({1})"},
		{"keystore.PKCS11", "HSM/PKCS#11"},
		{"alias.hasno.key", "O ficheiro de certificado não contém uma chave com o alias \"{0}\"."},
		{"keystore.reloaded", "({0}) O ficheiro do certificado foi recarregado, todas as chaves e certificados foram actualizados."},
		{"event.certificate.added.subject", "{0}: Foi adicionado um novo certificado (pseudónimo \"{1}\")"},
		{"event.certificate.modified.subject", "{0}: O pseudónimo de um certificado foi alterado"},
		{"access.problem", "Problemas de acesso a {0}"},
		{"keystore.read.failure", "O sistema não consegue ler os certificados/chaves guardados. Mensagem de erro: \"{0}\". Certifique-se de que definiu a palavra-passe correta do armazenamento de chaves."},
		{"event.certificate.deleted.subject", "{0}: Um certificado foi eliminado (pseudónimo \"{1}\")"},
		{"alias.notfound", "O ficheiro de certificados não contém um certificado com o alias \"{0}\"."},
		{"certificate.not.found.fingerprint", "O certificado com a impressão digital SHA-1 \"{0}\" não existe."},
		{"event.certificate.added.body", "Um novo certificado foi adicionado ao sistema com os seguintes dados:\n\n{0}"},
		{"event.certificate.modified.body", "O alias de certificado \"{0}\" seria alterado para \"{1}\"\n\n\nEstes são os dados do certificado:\n\n{2}"},
		{"keystore.JKS", "Armazenamento de chaves TLS"},
		{"alias.hasno.privatekey", "O ficheiro do certificado não contém uma chave privada com o pseudónimo \"{0}\"."},
		{"certificate.not.found.issuerserial.withinfo", "É necessário o certificado com o emissor \"{0}\" e o número de série \"{1}\", mas este não existe no sistema ({2})"},
	};
}
