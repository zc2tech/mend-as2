//$Header: /mec_as2/de/mendelson/comm/as2/ha/ServerCertificateRefreshControllerHA.java 2     28/11/23 17:25 Heller $
package de.mendelson.comm.as2.ha;

import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreStorageImplDB;
import de.mendelson.util.security.keydata.KeydataAccessDB;
import de.mendelson.util.systemevents.SystemEventManager;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks if there are external changes in the keystore, then it is reloaded.
 * This is required if there are multiple HA nodes that might change the
 * certificates in the keystores
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ServerCertificateRefreshControllerHA {

    public ServerCertificateRefreshControllerHA(IDBDriverManager dbDriverManager,
            SystemEventManager systemEventManager) {

    }

    /**
     * Starts the embedded task that guards the files to reload
     */
    public void startRefreshControl(CertificateManager certificateManagerEncSign,
            CertificateManager certificateManagerTLS) {
    }

}
