/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.mendelson.comm.as2.tracker;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import de.mendelson.util.systemevents.notification.NotificationData;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Utility class for sending tracker attack notification emails
 *
 * @author Julian Xu
 */
public class TrackerNotificationMailer {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private static final long SMTP_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long SMTP_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);

    /**
     * Send attack notification email to admin
     *
     * @param remoteAddr Attacker IP address
     * @param failedAttempts Number of failed attempts
     * @param reason Reason for notification
     * @param notificationData SMTP configuration
     */
    public static void sendAttackNotification(String remoteAddr, int failedAttempts,
                                             String reason, NotificationData notificationData) {
        try {
            if (notificationData == null || notificationData.getNotificationMail() == null
                    || notificationData.getNotificationMail().trim().isEmpty()) {
                LOGGER.warning("Cannot send tracker attack notification: No notification email configured");
                return;
            }

            Session session = createSession(notificationData);
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(notificationData.getReplyTo()));
            message.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(notificationData.getNotificationMail()));
            message.setSubject("[ALERT] Tracker Endpoint Attack Detected - " + remoteAddr);

            String emailBody = buildAttackEmailBody(remoteAddr, failedAttempts, reason);
            message.setText(emailBody, "UTF-8", "plain");

            Transport.send(message);

            LOGGER.info("Tracker attack notification sent successfully to: "
                    + notificationData.getNotificationMail());

        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_NOTIFICATION_SEND_FAILED,
                    "Failed to send tracker attack notification",
                    "Remote address: " + remoteAddr + "\nError: " + e.getMessage()
            );
            LOGGER.warning("Failed to send tracker attack notification: " + e.getMessage());
        }
    }

    /**
     * Build email body for attack notification
     */
    private static String buildAttackEmailBody(String remoteAddr, int failedAttempts, String reason) {
        StringBuilder body = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

        body.append("==========================================\n");
        body.append("SECURITY ALERT: Tracker Endpoint Attack\n");
        body.append("==========================================\n\n");

        body.append("Attack Details:\n");
        body.append("---------------\n");
        body.append("Remote IP Address: ").append(remoteAddr).append("\n");
        body.append("Failed Attempts: ").append(failedAttempts).append("\n");
        body.append("Reason: ").append(reason).append("\n");
        body.append("Timestamp: ").append(dateFormat.format(new Date())).append("\n\n");

        body.append("Actions Taken:\n");
        body.append("--------------\n");
        body.append("- Access from this IP has been temporarily blocked\n");
        body.append("- All requests from this IP will be rejected until block expires\n");
        body.append("- Failed authentication attempts have been logged to the database\n\n");

        body.append("Recommended Actions:\n");
        body.append("--------------------\n");
        body.append("1. Review tracker_auth_failure table for detailed attempt logs\n");
        body.append("2. Check if this IP belongs to a legitimate partner\n");
        body.append("3. Consider firewall rules if attacks continue\n");
        body.append("4. Review tracker endpoint authentication settings\n\n");

        body.append("System Information:\n");
        body.append("-------------------\n");
        body.append("Server: ").append(AS2ServerVersion.getFullProductName()).append("\n");
        body.append("Endpoint: /as2/tracker\n\n");

        body.append("This is an automated security alert from your AS2 server.\n");
        body.append("Do not reply to this email.\n");

        return body.toString();
    }

    /**
     * Create SMTP session with configuration
     */
    private static Session createSession(NotificationData notificationData) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", notificationData.getMailServer());
        properties.setProperty("mail.smtp.port", String.valueOf(notificationData.getMailServerPort()));
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.connectiontimeout", String.valueOf(SMTP_CONNECTION_TIMEOUT));
        properties.setProperty("mail.smtp.timeout", String.valueOf(SMTP_TIMEOUT));

        // TLS/SSL configuration
        if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_START_TLS) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.starttls.required", "true");
        } else if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_TLS) {
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        }

        // Authentication
        Authenticator authenticator = null;
        if (notificationData.usesSMTPAuthCredentials()) {
            properties.setProperty("mail.smtp.auth", "true");
            final String username = notificationData.getSMTPUser();
            final String password = String.valueOf(notificationData.getSMTPPass());

            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
        }

        return Session.getInstance(properties, authenticator);
    }
}
