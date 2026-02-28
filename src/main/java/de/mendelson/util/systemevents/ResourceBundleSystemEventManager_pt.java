//$Header: /as4/de/mendelson/util/systemevents/ResourceBundleSystemEventManager_pt.java 3     15/01/25 10:18 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEventManager_pt extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"module.name", "[GESTOR EVENTOS SISTEMA]"},
        {"label.subject.login.success", "Início de sessão do utilizador bem sucedido [{0}]"},
        {"label.subject.logoff", "Terminar sessão do utilizador [{0}]"},
        {"error.createdir.subject", "Geração de diretórios"},
        {"label.body.clientversion", "Versão do cliente: {0}"},
        {"label.subject.login.failed", "Falha no início de sessão do utilizador [{0}]"},
        {"label.error.clientserver", "Problema na ligação cliente-servidor"},
        {"label.body.clientos", "Sistema operativo do cliente: {0}"},
        {"label.body.clientip", "Endereço IP: {0}"},
        {"label.body.processid", "Número do processo no sistema operativo do cliente: {0}"},
        {"label.body.tlsprotocol", "Protocolo TLS: {0}"},
        {"label.body.tlsciphersuite", "Cifra TLS: {0}"},
        {"error.createdir.body", "Ocorreu um problema ao criar um diretório: {0}\nProblema: {1}"},
        {"label.body.details", "Detalhes: {0}"},
        {"error.in.systemevent.registration", "Não foi possível registar um problema do sistema no gestor de eventos do sistema: {0}" },
    };
}
