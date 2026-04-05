package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.timing.TimingScheduledThreadPool;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.KeystoreStorageImplDB;
import de.mendelson.util.security.keydata.KeydataAccessDB;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks if there are external changes in the TLS keystore, then it is reloaded
 * in the jetty SSL context.
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class JettyCertificateRefreshController {

    private final List<TLSKeystoreChangedCheckThread> checkThreadList = new ArrayList<TLSKeystoreChangedCheckThread>();
    private final IDBDriverManager dbDriverManager;

    public JettyCertificateRefreshController(Logger logger, IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Starts the embedded task that guards keystores if there are any changes
     * for one port
     */
    public void addRefreshControl(SslContextFactory sslContextFactory) {
        TLSKeystoreChangedCheckThread checkThread = new TLSKeystoreChangedCheckThread(sslContextFactory);
        this.checkThreadList.add(checkThread);
        TimingScheduledThreadPool.scheduleWithFixedDelay(checkThread, 15, 30, TimeUnit.SECONDS);
    }

    /**
     * Checks if there is an external change in the TLS jetty keystore(s) - then
     * a reload is required in the jetty SslContextFactory
     */
    public class TLSKeystoreChangedCheckThread implements Runnable {

        private long lastModificationTimeTLS = -1;
        private final SslContextFactory sslContextFactory;

        public TLSKeystoreChangedCheckThread(SslContextFactory sslContextFactory) {
            this.sslContextFactory = sslContextFactory;
        }

        @Override
        public void run() {
            try {
                KeydataAccessDB keydataAccess = new KeydataAccessDB(dbDriverManager, SystemEventManagerImplAS2.instance());
                long foundModificationTimeTLS = keydataAccess.getLastChanged(KeystoreStorageImplDB.KEYSTORE_USAGE_TLS);
                if (this.lastModificationTimeTLS == -1) {
                    //first run: take the change value
                    this.lastModificationTimeTLS = foundModificationTimeTLS;
                } else {
                    if (foundModificationTimeTLS != this.lastModificationTimeTLS) {
                        this.lastModificationTimeTLS = foundModificationTimeTLS;
                        //perform reload - there has been detected a change in the keystore storage
                        this.sslContextFactory.reload(this::reloadPerformed);
                    }
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e);
            }
        }

        public void reloadPerformed(SslContextFactory sslContextFactory) {
            //logger.info(MODULE_NAME + " " + rb.getResourceString("tls.keystore.reloaded"));
        }

    }

}
