//$Header: /as2/de/mendelson/comm/as2/timing/PartnerTLSCertificateChangedController.java 6     11/02/25 13:39 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.connectiontest.ConnectionTest;
import de.mendelson.util.clientserver.connectiontest.ConnectionTestProxy;
import de.mendelson.util.clientserver.connectiontest.ConnectionTestResult;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks from time to time if a partner TLS certificate is new and
 * automatically imports this into the TLS certificate manager
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class PartnerTLSCertificateChangedController {

    public static final int CHECK_DELAY_IN_MIN = 15;
    
    /**
     * Logger to log information to
     */
    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final TLSCertificateChangedThread tlsCertificateChangedThread;
    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("partner-tls-cert-changed-check"));
    private final IDBDriverManager dbDriverManager;
    private final CertificateManager certificateManagerTLS;
    private ScheduledFuture future = null;
    private final static MecResourceBundle rb;
    private final static String MODULE_NAME;
    private final static String MODULE_EVENT_SUBJECT_NAME;
    private final PreferencesAS2 preferences;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerTLSCertificateChangedController.class.getName());
            MODULE_NAME = "[" + rb.getResourceString("module.name") + "]";
            MODULE_EVENT_SUBJECT_NAME = rb.getResourceString("module.name");
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public PartnerTLSCertificateChangedController(IDBDriverManager dbDriverManager,
            CertificateManager certificateManagerTLS) {
        this.dbDriverManager = dbDriverManager;
        this.certificateManagerTLS = certificateManagerTLS;
        this.tlsCertificateChangedThread = new TLSCertificateChangedThread();
        //Remove all threads from the scheduler queue once they are canceled. 
        //The threads are canceled by canceling their future.
        this.scheduledExecutor.setRemoveOnCancelPolicy(true);
        this.preferences = new PreferencesAS2(dbDriverManager);
    }

    /**
     * Starts the embedded task
     */
    public void startTLSCertificateChangedControl(boolean logThis) {
        if (this.future == null) {
            this.future = this.scheduledExecutor.scheduleWithFixedDelay(this.tlsCertificateChangedThread, 
                    CHECK_DELAY_IN_MIN, CHECK_DELAY_IN_MIN, TimeUnit.MINUTES);
            if (logThis) {
                logger.log(Level.INFO, MODULE_NAME
                        + " " + rb.getResourceString("autoimport.tls.check.started"));
            }
        }
    }

    /**
     * Stops the embedded task
     */
    public void stopTLSCertificateChangedControl() {
        if (this.future != null) {
            synchronized (this.future) {
                this.future.cancel(true);
            }
            this.future = null;
            logger.log(Level.INFO, MODULE_NAME
                    + " " + rb.getResourceString("autoimport.tls.check.stopped"));

        }
    }

    public class TLSCertificateChangedThread implements Runnable {

        public TLSCertificateChangedThread() {
        }

        @Override
        public void run() {
            ConnectionTestProxy proxy = null;
            if (preferences.getBoolean(PreferencesAS2.PROXY_USE)) {
                proxy = new ConnectionTestProxy();
                proxy.setAddress(preferences.get(PreferencesAS2.PROXY_HOST));
                proxy.setPort(preferences.getInt(PreferencesAS2.PROXY_PORT));
                String proxyUserName = preferences.get(PreferencesAS2.AUTH_PROXY_USER);
                if (proxyUserName != null && !proxyUserName.trim().isEmpty()) {
                    proxy.setUserName(proxyUserName);
                    proxy.setPassword(preferences.get(PreferencesAS2.AUTH_PROXY_PASS));
                }
            }
            PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
            List<Partner> partnerList = partnerAccess.getNonLocalStations();
            boolean certificateAdded = false;
            for (Partner partner : partnerList) {
                try {
                    if (partner.getURL() != null && partner.getURL().toLowerCase().startsWith("https")) {
                        ConnectionTest connectionTest = new ConnectionTest(logger,
                                ConnectionTest.CONNECTION_TEST_AUTOMATIC_CERTIFICATE_DOWNLOAD);
                        if (proxy != null) {
                            connectionTest.setProxy(proxy);
                        }
                        URL url = new URL(partner.getURL());
                        String host = url.getHost();
                        int port = url.getPort();
                        ConnectionTestResult result = connectionTest.checkConnectionTLS(host, port,
                                TimeUnit.SECONDS.toMillis(45),
                                certificateManagerTLS,
                                "[local]", partner.getName(), ConnectionTest.PARTNER_ROLE_REMOTE_PARTNER);
                        if (result.getException() != null) {
                            logger.log(Level.WARNING,
                                    MODULE_NAME + " " + rb.getResourceString("import.failed"),
                                    new Object[]{partner.getName(),
                                        "[" + result.getException().getClass().getSimpleName() + "] "
                                        + result.getException().getMessage()});
                            SystemEventManagerImplAS2.instance().newEvent(
                                    SystemEvent.SEVERITY_WARNING,
                                    SystemEvent.ORIGIN_SYSTEM,
                                    SystemEvent.TYPE_CERTIFICATE_ANY,
                                    MODULE_EVENT_SUBJECT_NAME,
                                    rb.getResourceString("import.failed",
                                            new Object[]{partner.getName(),
                                                "[" + result.getException().getClass().getSimpleName() + "] "
                                                + result.getException().getMessage()
                                            })
                            );
                        } else {
                            X509Certificate[] certificates = result.getFoundCertificates();
                            if (certificates != null) {
                                for (X509Certificate certificate : certificates) {
                                    KeystoreCertificate keystoreCertificate = new KeystoreCertificate();
                                    keystoreCertificate.setCertificate(certificate, null);
                                    String fingerprint = keystoreCertificate.getFingerPrintSHA1();
                                    if (certificateManagerTLS.getKeystoreCertificateByFingerprintSHA1(fingerprint) == null) {
                                        try {
                                            String alias = KeyStoreUtil.importX509Certificate(certificateManagerTLS.getKeystore(), certificate);
                                            certificateAdded = true;
                                            logger.log(Level.FINE,
                                                    MODULE_NAME + " " + rb.getResourceString("import.success"),
                                                    new Object[]{alias, partner.getName()});
                                            SystemEventManagerImplAS2.instance().newEvent(
                                                    SystemEvent.SEVERITY_INFO,
                                                    SystemEvent.ORIGIN_SYSTEM,
                                                    SystemEvent.TYPE_CERTIFICATE_ADD,
                                                    rb.getResourceString("import.success.event.header"),
                                                    rb.getResourceString("import.success.event.body",
                                                            new Object[]{
                                                                partner.getName(),
                                                                partner.getURL(),
                                                                alias
                                                            }));
                                        } catch (Throwable e) {
                                            logger.log(Level.WARNING,
                                                    MODULE_NAME + " " + rb.getResourceString("import.failed"),
                                                    new Object[]{partner.getName(),
                                                        "[" + e.getClass().getSimpleName() + "] "
                                                        + e.getMessage()});
                                            SystemEventManagerImplAS2.instance().newEvent(
                                                    SystemEvent.SEVERITY_WARNING,
                                                    SystemEvent.ORIGIN_SYSTEM,
                                                    SystemEvent.TYPE_CERTIFICATE_ANY,
                                                    MODULE_EVENT_SUBJECT_NAME,
                                                    rb.getResourceString("import.failed",
                                                            new Object[]{partner.getName(),
                                                                "[" + result.getException().getClass().getSimpleName() + "] "
                                                                + result.getException().getMessage()
                                                            })
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (certificateAdded) {
                        try {
                            certificateManagerTLS.saveKeystore();
                            certificateManagerTLS.rereadKeystoreCertificates();
                        } catch (Throwable e) {
                            SystemEventManagerImplAS2.instance().systemFailure(e);
                        }
                    }
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e);
                }
            }
        }
    }
}
