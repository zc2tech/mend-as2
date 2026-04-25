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

package de.mendelson.comm.as2.usermanagement;

import de.mendelson.util.systemevents.notification.NotificationData;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Utility class for sending user account creation emails
 *
 */
public class UserNotificationMailer {

    private static final Logger logger = Logger.getLogger("de.mendelson.as2.server");
    private static final long SMTP_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long SMTP_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);

    /**
     * Sends an email to a newly created user with their login credentials
     *
     * @param user The created user
     * @param plainPassword The generated password (plain text)
     * @param notificationData SMTP configuration
     * @param serverUrl Base URL of the server (e.g., "https://yourserver.com/as2")
     * @throws Exception if email sending fails
     */
    public static void sendUserCreationEmail(WebUIUser user, String plainPassword,
                                            NotificationData notificationData, String serverUrl) throws Exception {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new Exception("User has no email address configured");
        }

        Session session = createSession(notificationData);

        MimeMessage message = new MimeMessage(session);

        // Use SMTP username as From address (required by most email providers)
        // Many SMTP servers (Gmail, 163.com, etc.) require From address to match authenticated user
        String fromAddress = notificationData.usesSMTPAuthCredentials()
            ? notificationData.getSMTPUser()
            : notificationData.getReplyTo();
        message.setFrom(new InternetAddress(fromAddress));

        // Set Reply-To if different from From address
        if (notificationData.getReplyTo() != null && !notificationData.getReplyTo().equals(fromAddress)) {
            message.setReplyTo(new InternetAddress[]{new InternetAddress(notificationData.getReplyTo())});
        }

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        message.setSubject("Your AS2 Server Account Has Been Created");

        String emailBody = buildEmailBody(user, plainPassword, serverUrl);
        message.setText(emailBody, "UTF-8", "plain");

        // Send the email
        Transport.send(message);

        logger.info("Account creation email sent successfully to: " + user.getEmail());
    }

    private static String buildEmailBody(WebUIUser user, String plainPassword, String serverUrl) {
        StringBuilder body = new StringBuilder();

        body.append("Hello ").append(user.getFullName() != null ? user.getFullName() : user.getUsername()).append(",\n\n");
        body.append("Your AS2 server account has been created.\n\n");
        body.append("Login Details:\n");
        body.append("==============\n");
        body.append("Username: ").append(user.getUsername()).append("\n");
        body.append("Password: ").append(plainPassword).append("\n");
        body.append("WebUI URL: ").append(serverUrl).append("/webui/login\n\n");
        body.append("IMPORTANT SECURITY NOTICE:\n");
        body.append("For your security, you will be required to change your password upon first login.\n");
        body.append("Please keep this email secure and delete it after changing your password.\n\n");
        body.append("If you did not request this account, please contact your system administrator immediately.\n\n");
        body.append("Best regards,\n");
        body.append("AS2 Server Administration\n");

        return body.toString();
    }

    private static Session createSession(NotificationData notificationData) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", notificationData.getMailServer());
        properties.setProperty("mail.smtp.port", String.valueOf(notificationData.getMailServerPort()));
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.connectiontimeout", String.valueOf(SMTP_CONNECTION_TIMEOUT));
        properties.setProperty("mail.smtp.timeout", String.valueOf(SMTP_TIMEOUT));

        if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_START_TLS) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.starttls.required", "true");
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        } else if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_TLS) {
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            // For SSL/TLS on port 465, we need to use smtps protocol
            if (notificationData.getMailServerPort() == 465) {
                properties.setProperty("mail.transport.protocol", "smtps");
                properties.setProperty("mail.smtps.host", notificationData.getMailServer());
                properties.setProperty("mail.smtps.port", String.valueOf(notificationData.getMailServerPort()));
                properties.setProperty("mail.smtps.ssl.enable", "true");
                properties.setProperty("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3");
                properties.setProperty("mail.smtps.connectiontimeout", String.valueOf(SMTP_CONNECTION_TIMEOUT));
                properties.setProperty("mail.smtps.timeout", String.valueOf(SMTP_TIMEOUT));
            }
        }

        Session session;
        if (notificationData.usesSMTPAuthCredentials()) {
            final String username = notificationData.getSMTPUser();
            final String password = notificationData.getSMTPPass() != null
                ? String.valueOf(notificationData.getSMTPPass())
                : "";

            // Set authentication for both smtp and smtps protocols
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.debug.auth", "true");

            if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_TLS
                && notificationData.getMailServerPort() == 465) {
                // For SMTPS (port 465), also set smtps-specific auth properties
                properties.setProperty("mail.smtps.auth", "true");
            }

            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(properties);
        }

        // Enable debug output to help diagnose issues
        // session.setDebug(true);

        return session;
    }
}
