//$Header: /mec_as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_pt.java 4     21/03/25 9:12 Heller $
package de.mendelson.comm.as2.message;

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
 * @version $Revision: 4 $
 */
public class ResourceBundleAS2MessageParser_pt extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"inbound.connection.syncmdn", "Foi recebida uma MDN síncrona no backchannel da sua ligação de saída"},
        {"original.filename.found", "O nome original do ficheiro foi transmitido pelo remetente como \"{0}\"."},
        {"msg.incoming.identproblem", "A transmissão de entrada é uma mensagem AS2. Não foi processada porque houve um problema com a identificação do parceiro."},
        {"mdn.incoming.relationship", "A transmissão de entrada é um aviso de receção (MDN) [{0}]"},
        {"msg.already.processed", "Uma mensagem com o número de mensagem [{0}] já foi processada"},
        {"message.signature.using.alias", "Utilizar o certificado \"{0}\" do parceiro remoto \"{1}\" para verificar a assinatura digital da mensagem AS2 recebida."},
        {"found.attachments", "Foram encontrados anexos com dados do utilizador {0} na mensagem AS2."},
        {"contentmic.match", "O Código de Integridade da Mensagem (MIC) corresponde à mensagem AS2 enviada."},
        {"mdn.details", "Pormenores do aviso de receção (MDN) recebido de {0}: \"{1}\""},
        {"msg.incoming.ha", "A transmissão de entrada é uma mensagem AS2 [{0}], tamanho dos dados brutos: {1}, processada por [{2}]."},
        {"mdn.signed.error", "O aviso de receção de entrada (MDN) é assinado digitalmente, contrariamente à configuração do parceiro \"{0}\"."},
        {"mdn.notsigned", "O aviso de receção recebido (MDN) não está assinado digitalmente."},
        {"mdn.signature.using.alias", "Utilizar o certificado \"{0}\" do parceiro remoto \"{1}\" para verificar a assinatura digital da MDN de entrada."},
        {"signature.analyzed.digest", "O remetente utilizou o algoritmo \"{0}\" para a assinatura digital."},
        {"mdn.signature.ok", "A assinatura digital da MDN recebida foi verificada com sucesso."},
        {"mdn.signature.failure", "A verificação da assinatura digital da MDN recebida falhou - {0}"},
        {"data.compressed.expanded", "Os dados do utilizador comprimidos da mensagem AS2 de entrada foram expandidos de {0} para {1}."},
        {"mdn.state", "O estado do aviso de receção recebido (MDN) é [{0}]."},
        {"msg.signed", "A mensagem AS2 de entrada é assinada digitalmente."},
        {"inbound.connection.tls", "Ligação TLS de entrada de [{0}] para a porta {1} [{2}, {3}]"},
        {"mdn.answerto", "O aviso de receção (MDN) de entrada com o número de mensagem \"{0}\" é a resposta à mensagem AS2 de saída \"{1}\"."},
        {"msg.notsigned", "A mensagem AS2 de entrada não é assinada digitalmente."},
        {"decryption.inforequired", "É necessária uma chave com os seguintes parâmetros para desencriptar a mensagem AS2 de entrada:\n{0}"},
        {"invalid.original.filename.log", "Nome de ficheiro original inválido detectado na transação. \"{0}\" é substituído por \"{1}\" e o processamento continua."},
        {"filename.extraction.error", "Não é possível extrair o nome do ficheiro original da mensagem AS2 recebida: \"{0}\" é ignorado."},
        {"mdn.incoming.ha", "A transmissão de entrada é um aviso de receção (MDN), processado por [{0}]."},
        {"mdn.incoming", "A transmissão recebida é um aviso de receção (MDN)."},
        {"mdn.incoming.relationship.ha", "A transmissão de entrada é um aviso de receção (MDN) [{0}], processado por [{1}]."},
        {"mdn.signed", "A confirmação de receção (MDN) é assinada digitalmente ({0})."},
        {"signature.analyzed.digest.failed", "O sistema não conseguiu descobrir o algoritmo de assinatura da mensagem AS2 recebida."},
        {"msg.encrypted", "A mensagem AS2 de entrada é encriptada."},
        {"contentmic.failure", "O Código de Integridade da Mensagem (MIC) não corresponde à mensagem AS2 enviada (esperado: {0}, recebido: {1})."},
        {"data.unable.to.process.content.transfer.encoding", "Foram recebidos dados que não puderam ser processados por conterem erros. A codificação de transferência de conteúdos \"{0}\" é desconhecida."},
        {"original.filename.undefined", "O nome do ficheiro original não foi transferido."},
        {"decryption.infoassigned", "Foi utilizada uma chave com os seguintes parâmetros (pseudónimo \"{0}\") para desencriptar a mensagem AS2 recebida:\n{1}"},
        {"data.not.compressed", "Os dados AS2 recebidos são descomprimidos."},
        {"inbound.connection.raw", "Ligação de entrada de [{0}] para a porta {1}"},
        {"mdn.unsigned.error", "O aviso de receção de entrada (MDN) NÃO é assinado digitalmente, contrariamente à configuração do parceiro \"{0}\"."},
        {"msg.incoming", "A transmissão de entrada é uma mensagem AS2 [{0}], tamanho dos dados brutos: {1}"},
        {"found.cem", "A mensagem recebida é um pedido de troca de certificados (CEM)."},
        {"msg.notencrypted", "A mensagem AS2 de entrada não está encriptada."},
        {"message.signature.ok", "A assinatura digital da mensagem AS2 recebida foi verificada com sucesso."},
        {"mdn.unexpected.messageid", "O aviso de receção recebido (MDN) faz referência à mensagem AS2 do número de referência \"{0}\", que não espera um MDN."},
        {"message.signature.failure", "A verificação da assinatura digital da mensagem AS2 de entrada falhou - {0}"},
        {"decryption.done.alias", "Os dados da mensagem AS2 de entrada foram desencriptados utilizando a chave \"{0}\" da estação local \"{3}\", o algoritmo de encriptação foi \"{1}\", o algoritmo de encriptação da chave foi \"{2}\"."},
        {"invalid.original.filename.title", "Nome de ficheiro original inválido detectado na transação"},
        {"invalid.original.filename.body", "O sistema extraiu um nome de ficheiro original inválido na transação {0} de {1} para {2}.\nO nome de ficheiro \"{3}\" encontrado foi substituído por \"{4}\" e o processamento continuou com este novo nome de ficheiro. Isto pode ter um impacto no seu fluxo de processamento, uma vez que os nomes de ficheiros contêm por vezes metadados."},};
}
