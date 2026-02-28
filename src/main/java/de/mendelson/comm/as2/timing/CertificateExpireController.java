//$Header: /as2/de/mendelson/comm/as2/timing/CertificateExpireController.java 30    19/12/24 8:55 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Controlls the certificates and checks if they will expire soon
 *
 * @author S.Heller
 * @version $Revision: 30 $
 */
public class CertificateExpireController {

    private final int[] daysToExpire = new int[]{10, 3, 1};
    /**
     * Logger to log information to
     */
    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final CertificateManager managerSSL;
    private final CertificateManager managerEncSign;
    private final CertificationExpireThread expireThread;

    public CertificateExpireController(CertificateManager managerEncSign, CertificateManager managerSSL) {        
        this.managerEncSign = managerEncSign;
        this.managerSSL = managerSSL;
        this.expireThread = new CertificationExpireThread();
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void startCertExpireControl() {        
        TimingScheduledThreadPool.scheduleWithFixedDelay(this.expireThread, 0, 1, TimeUnit.DAYS);
    }

    /**
     * Computes and returns the number of days between the two passed dates
     *
     */
    private static int getDayDiff(Date firstDate, Date secondDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        int dayOfYear1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(secondDate);
        int dayOfYear2 = calendar.get(Calendar.DAY_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);
        return ((year2 - year1) * 365 + (dayOfYear2 - dayOfYear1));
    }

    public static int getCertificateExpireDuration(KeystoreCertificate certificate) {
        return (getDayDiff(new Date(), certificate.getNotAfter()));
    }

    public class CertificationExpireThread implements Runnable {

        public CertificationExpireThread() {
        }

        @Override
        public void run() {
            try {
                List<KeystoreCertificate> encSignList = managerEncSign.getKeyStoreCertificateList();
                this.checkCertificates(encSignList);
                List<KeystoreCertificate> sslList = managerSSL.getKeyStoreCertificateList();
                this.checkCertificates(sslList);
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e);
            }
        }

        /**
         * Checks if a certificate is expire or is up to expire
         */
        private void checkCertificates(List<KeystoreCertificate> list) {
            for (KeystoreCertificate certificate : list) {
                int certificateExpireDuration = CertificateExpireController.getCertificateExpireDuration(certificate);
                //The certificate has not been expired so far
                for (int expireDuration : daysToExpire) {
                    if (certificateExpireDuration == expireDuration) {                        
                        try {
                            SystemEventManagerImplAS2.instance().newEventCertificateWillExpire(certificate, certificateExpireDuration);
                        } catch (Exception e) {
                            String exceptionClass = "[" + e.getClass().getName() + "]";
                            logger.severe("CertificateExpireThread: " + exceptionClass + " " + e.getMessage());
                            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                        }
                    }
                }
                //The certificate has been already expired
                if (certificateExpireDuration <= 0) {                    
                    try {
                        SystemEventManagerImplAS2.instance().newEventCertificateWillExpire(certificate, certificateExpireDuration);
                    } catch (Exception e) {
                        String exceptionClass = "[" + e.getClass().getName() + "]";
                        logger.severe("CertificateExpireThread: " + exceptionClass + " " + e.getMessage());
                        SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                    }
                }
            }
        }

    }
}
