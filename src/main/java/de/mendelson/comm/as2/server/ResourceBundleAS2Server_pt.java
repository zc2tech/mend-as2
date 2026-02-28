//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2Server_pt.java 3     18/02/25 14:39 Heller $
package de.mendelson.comm.as2.server;

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
public class ResourceBundleAS2Server_pt extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"server.willstart", "{0} começa"},
        {"server.started.issue", "Aviso: Foi detectado 1 problema de configuração ao iniciar o servidor."},
        {"server.hello", "Isto é {0}"},
        {"server.shutdown", "{0} desliga-se."},
        {"bind.exception", "{0}\nDefiniu uma porta que está atualmente a ser utilizada por outro processo no seu sistema.\nEsta pode ser a porta cliente-servidor ou a porta HTTP/S que definiu na configuração HTTP.\nPor favor, altere a sua configuração ou pare o outro processo antes de utilizar o {1}."},
        {"server.already.running", "Uma instância do mendelson AS2 parece já estar a funcionar.\nNo entanto, também pode ser que uma instância anterior não tenha sido encerrada corretamente. Se tiver certeza de que nenhuma outra instância está em execução,\npor favor, apague o ficheiro de bloqueio \"{0}\"\n(data de início {1}) e reinicie o servidor."},
        {"server.start.details", "{0} Parâmetro:\n\nInicia o servidor HTTP integrado: {1}\nPermitir conexões cliente-servidor de outros hosts: {2}\nMemória Heap: {3}\nVersão de Java: {4}\nUtilizador do sistema: {5}\nIdentificação do sistema: {6}"},
        {"server.hello.licenseexpire.single", "A licença expira no dia {0} ({1}). É necessário renovar a licença através do suporte mendelson (service@mendelson.de) se desejar continuar a utilizá-la posteriormente."},
        {"server.hello.licenseexpire", "A licença expira em {0} dias ({1}). Deve renovar a licença através do suporte mendelson (service@mendelson.de) se desejar continuar a utilizá-la após este período."},
        {"server.started", "mendelson AS2 2024 build 613 iniciado em {0} ms."},
        {"server.startup.failed", "Houve um problema ao iniciar o servidor - o arranque foi cancelado"},
        {"server.started.issues", "Aviso: Foram detectados {0} problemas de configuração ao iniciar o servidor."},
        {"fatal.limited.strength", "Esta VM Java não suporta o comprimento de chave necessário. Por favor, instale os ficheiros \"Unlimited jurisdiction key strength policy\" antes de iniciar o servidor mendelson AS2."},
        {"server.nohttp", "O servidor HTTP integrado não foi iniciado."},
        {"server.started.usedlibs", "Bibliotecas usadas"},
    };
}
