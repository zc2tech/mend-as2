//$Header: /as2/de/mendelson/util/systemevents/notification/NotificationAccessDBImplAS2.java 22    12/03/25 17:28 Heller $
package de.mendelson.util.systemevents.notification;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2AccessDB;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Stores the notification data for the AS2
 *
 * @author S.Heller
 * @version $Revision: 22 $
 */
public class NotificationAccessDBImplAS2 implements NotificationAccessDB {

    private final IDBDriverManager dbDriverManager;

    public NotificationAccessDBImplAS2(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Reads the notification data from the db, there is only one available
     */
    @Override
    public NotificationData getNotificationData() {
        try (Connection configConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            try (PreparedStatement statement = configConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM notification")) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        NotificationDataImplAS2 data = new NotificationDataImplAS2();
                        data.setMailServer(result.getString("mailhost"));
                        data.setMailServerPort(result.getInt("mailhostport"));
                        data.setNotificationMail(result.getString("notificationemailaddress"));
                        data.setNotifyCertExpire(result.getInt("notifycertexpire") == 1 ? true : false);
                        data.setNotifyTransactionError(result.getInt("notifytransactionerror") == 1 ? true : false);
                        data.setNotifyCEM(result.getInt("notifycem") == 1 ? true : false);
                        data.setNotifySystemFailure(result.getInt("notifysystemfailure") == 1 ? true : false);
                        data.setNotifyResendDetected(result.getInt("notifyresend") == 1 ? true : false);
                        data.setReplyTo(result.getString("replyto"));
                        data.setUsesSMTPAuthCredentials(result.getInt("usesmtpauth") == 1 ? true : false);
                        data.setSMTPUser(result.getString("smtpauthuser"));
                        String smtpPass = result.getString("smtpauthpass");
                        if (!result.wasNull()) {
                            data.setSMTPPass(smtpPass.toCharArray());
                        }
                        data.setUsesSMTPAuthOAuth2(result.getInt("usesmtpoauth2") == 1 ? true : false);
                        data.setConnectionSecurity(result.getInt("security"));
                        data.setMaxNotificationsPerMin(result.getInt("maxnotificationspermin"));
                        data.setNotifyConnectionProblem(result.getInt("notifyconnectionproblem") == 1 ? true : false);
                        data.setNotifyPostprocessingProblem(result.getInt("notifypostprocessing") == 1 ? true : false);
                        // int oAuth2Id = result.getInt("smtpoauth2id");
                        // if (!result.wasNull()) {
                        //     OAuth2AccessDB oauth2Access = new OAuth2AccessDB(this.dbDriverManager, SystemEventManagerImplAS2.instance());
                        //     OAuth2Config config = oauth2Access.getOAuth2Config(oAuth2Id, configConnectionAutoCommit);
                        //     if (config != null) {
                        //         data.setOAuth2Config(config);
                        //     } else {
                        //         data.setUsesSMTPAuthOAuth2(false);
                        //     }
                        // } else {
                            data.setUsesSMTPAuthOAuth2(false);
                        // }
                        data.setNotifyClientServerProblem(result.getInt("notifyclientserver") == 1 ? true : false);
                        return (data);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (null);
    }

    /**
     * Inserts a new message entry into the database
     */
    @Override
    public void updateNotification(NotificationData notificationData) {
        NotificationDataImplAS2 data = (NotificationDataImplAS2) notificationData;
        String transactionName = "Notification_updateNotification";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "oauth2", "notification"});
                try {
                    if (data.getOAuth2Config() != null) {
                        OAuth2AccessDB oauth2Access = new OAuth2AccessDB(this.dbDriverManager, SystemEventManagerImplAS2.instance());
                        oauth2Access.insertOrUpdateOAuth2(data.getOAuth2Config(), configConnectionNoAutoCommit);
                    }
                    try (PreparedStatement statement = configConnectionNoAutoCommit.prepareStatement(
                            "UPDATE notification SET mailhost=?,mailhostport=?,notificationemailaddress=?,"
                            + "notifycertexpire=?,notifytransactionerror=?,notifycem=?,notifysystemfailure=?,replyto=?,usesmtpauth=?,"
                            + "smtpauthuser=?,smtpauthpass=?,notifyresend=?,security=?,maxnotificationspermin=?,notifyconnectionproblem=?,"
                            + "notifypostprocessing=?,usesmtpoauth2=?,smtpoauth2id=?,notifyclientserver=?")) {
                        statement.setString(1, data.getMailServer());
                        statement.setInt(2, data.getMailServerPort());
                        statement.setString(3, data.getNotificationMail());
                        statement.setInt(4, data.notifyCertExpire() ? 1 : 0);
                        statement.setInt(5, data.notifyTransactionError() ? 1 : 0);
                        statement.setInt(6, data.notifyCEM() ? 1 : 0);
                        statement.setInt(7, data.notifySystemFailure() ? 1 : 0);
                        statement.setString(8, data.getReplyTo());
                        statement.setInt(9, data.usesSMTPAuthCredentials() ? 1 : 0);
                        if (data.getSMTPUser() != null) {
                            statement.setString(10, data.getSMTPUser());
                        } else {
                            statement.setNull(10, Types.VARCHAR);
                        }
                        if (data.getSMTPPass() != null) {
                            statement.setString(11, String.valueOf(data.getSMTPPass()));
                        } else {
                            statement.setNull(11, Types.VARCHAR);
                        }
                        statement.setInt(12, data.notifyResendDetected() ? 1 : 0);
                        statement.setInt(13, data.getConnectionSecurity());
                        statement.setInt(14, data.getMaxNotificationsPerMin());
                        statement.setInt(15, data.notifyConnectionProblem() ? 1 : 0);
                        statement.setInt(16, data.notifyPostprocessingProblem() ? 1 : 0);
                        statement.setInt(17, data.usesSMTPAuthOAuth2() ? 1 : 0);
                        if (data.getOAuth2Config() != null) {
                            statement.setInt(18, data.getOAuth2Config().getDBId());
                        } else {
                            statement.setNull(18, Types.INTEGER);
                        }
                        statement.setInt(19, data.notifyClientServerProblem() ? 1 : 0);
                        statement.executeUpdate();
                    }
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

}
