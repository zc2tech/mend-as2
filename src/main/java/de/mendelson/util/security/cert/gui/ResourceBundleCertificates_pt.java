//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates_pt.java 3     11/03/25 16:42 Heller $
package de.mendelson.util.security.cert.gui;

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
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleCertificates_pt extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"cert.delete.impossible", "A entrada não pode ser eliminada, está a ser utilizada.\nUtilize \"Mostrar utilização\" para obter mais informações."},
        {"label.keystore.export", "Exportar todas as entradas como um ficheiro keystore (apenas para efeitos de cópia de segurança!)"},
        {"certificate.import.alias", "Nome alternativo para este certificado:"},
        {"tab.info.basic", "pormenores"},
        {"warning.deleteallexpired.expired.but.used.text", "{0} As chaves/certificados expirados são utilizados na configuração e, por conseguinte, não são eliminados"},
        {"filechooser.certificate.import", "Selecione o ficheiro de certificado para a importação"},
        {"button.import", "Importação"},
        {"menu.export", "Exportação"},
        {"certificate.ca.import.success.message", "O certificado da AC foi importado com sucesso com o pseudónimo \"{0}\"."},
        {"button.keycopy", "Copiar para {0} gestão"},
        {"button.delete", "Eliminar chave/certificado"},
        {"label.cert.valid", "Este certificado é válido"},
        {"display.ca.certs", "Mostrar certificados CA ({0})"},
        {"keystore.readonly.message", "Protegido contra escrita. Não é possível efetuar alterações."},
        {"keycopy.success.text", "A entrada [{0}] foi copiada com êxito"},
        {"menu.tools.generatekey", "Gerar nova chave (auto-assinada)"},
        {"label.key.export.pkcs12", "Chave de exportação (PKCS#12, PEM) (apenas para efeitos de cópia de segurança!)"},
        {"button.keycopy.signencrypt", "Encriptação/assinatura"},
        {"label.key.invalid", "Esta chave é inválida"},
        {"dialog.cert.delete.title", "Eliminar o certificado"},
        {"success.deleteallexpired.title", "Eliminar certificados/chaves expirados e não utilizados"},
        {"success.deleteallexpired.text", "{0} chaves/certificados expirados e não utilizados foram eliminados"},
        {"module.locked", "Esta gestão de certificados está atualmente aberta exclusivamente por outro cliente, não é possível efetuar quaisquer alterações!"},
        {"button.edit", "Mudar o nome do alias"},
        {"label.keystore", "Local de armazenamento"},
        {"warning.deleteallexpired.noneavailable.title", "Não disponível"},
        {"dialog.cert.delete.message", "Quer mesmo apagar o certificado com o pseudónimo \"{0}\"?"},
        {"button.cancel", "Cancelar"},
        {"title.cert.in.use", "O certificado é utilizado"},
        {"label.cert.invalid", "Este certificado é inválido"},
        {"certificate.import.error.message", "Ocorreu um erro durante a importação:\n{0}"},
        {"button.delete.all.expired", "Eliminar todas as chaves/certificados expirados"},
        {"button.keycopy.tls", "TLS"},
        {"button.ok", "Ok"},
        {"generatekey.error.message", "{0}"},
        {"menu.file.close", "Sair"},
        {"label.selectcsrfile", "Selecione o ficheiro para guardar o pedido de autenticação"},
        {"warning.deleteallexpired.noneavailable.text", "Não existem entradas expiradas ou não utilizadas"},
        {"button.export", "Exportação"},
        {"label.key.valid", "Esta chave é válida"},
        {"certificate.import.success.message", "O certificado foi importado com sucesso com o pseudónimo \"{0}\"."},
        {"title.signencrypt", "Chaves e certificados (encriptação, assinaturas)"},
        {"keycopy.target.ro.text", "A operação falhou - o ficheiro chave do destino está protegido contra escrita."},
        {"label.cert.export", "Certificado de exportação (para o parceiro)"},
        {"warning.deleteallexpired.expired.but.used.title", "Chaves/certificados utilizados"},
        {"tab.info.trustchain", "Percurso de certificação"},
        {"warning.deleteallexpired.text", "Pretende mesmo eliminar {0} entradas expiradas e não utilizadas?"},
        {"module.locked.title", "O módulo está a ser utilizado"},
        {"title.ssl", "Chaves e certificados (TLS)"},
        {"tab.info.extension", "Extensões"},
        {"menu.tools", "Alargado"},
        {"button.reference", "Mostrar utilização"},
        {"menu.tools.importcsr", "Autenticar certificado: Importar resposta da AC ao pedido de autenticação"},
        {"module.locked.text", "O módulo {0} é utilizado exclusivamente por outro cliente ({1})."},
        {"modifications.notalllowed.message", "Não é possível efetuar modificações"},
        {"keycopy.target.ro.title", "O objetivo é só de leitura"},
        {"label.key.import", "Importar a sua própria chave privada (do Keystore PKCS#12, JKS)"},
        {"menu.tools.importcsr.renew", "Renovar certificado: Importar resposta da CA ao pedido de autenticação"},
        {"keycopy.target.exists.title", "Já existe uma entrada no destino"},
        {"generatekey.error.title", "Erro durante a geração da chave"},
        {"label.trustanchor", "Âncora de confiança"},
        {"menu.file", "Ficheiro"},
        {"button.newkey", "Chave de importação"},
        {"certificate.import.error.title", "Erro"},
        {"certificate.import.success.title", "Sucesso"},
        {"keystore.readonly.title", "Armazém de chaves protegido contra escrita - não é possível editar"},
        {"label.cert.import", "Certificado de importação (do parceiro)"},
        {"warning.deleteallexpired.title", "Eliminar chaves/certificados expirados e não utilizados"},
        {"menu.tools.generatecsr.renew", "Renovar certificado: Gerar pedido de autenticação (para a AC)"},
        {"menu.tools.verifyall", "Verificar as listas de revogação de todos os certificados (LCR)"},
        {"warning.testkey", "Chave do teste de mendelson publicamente disponível - não utilizar em operações produtivas!"},
        {"keycopy.target.exists.text", "Esta entrada já existe na administração de certificados de destino (pseudónimo {0})."},
        {"menu.tools.generatecsr", "Autenticar certificado: Gerar pedido de autenticação (para a AC)"},
        {"menu.import", "Importação"},};
}
