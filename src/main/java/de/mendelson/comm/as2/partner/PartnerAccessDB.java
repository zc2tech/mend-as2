//$Header: /as2/de/mendelson/comm/as2/partner/PartnerAccessDB.java 109   12/03/25 16:07 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.cert.CertificateAccessDB;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2AccessDB;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.Types;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Implementation of a server log for the mendelson as2 server database
 *
 * @author S.Heller
 * @version $Revision: 109 $
 */
public class PartnerAccessDB {

    /**
     * Access the certificates
     */
    private final CertificateAccessDB certificateAccess;
    private final PartnerEventAccessDB eventAccess;
    private final OAuth2AccessDB oAuth2Access;

    /**
     * Returns the full partner data
     */
    public static final int DATA_COMPLETENESS_FULL = 100;
    /**
     * Return incomplete partner requests - for faster UI client-server requests
     */
    public static final int DATA_COMPLETENESS_NAMES_AS2ID_TYPE = 101;
    private final IDBDriverManager dbDriverManager;

    /**
     *
     */
    public PartnerAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.certificateAccess = new CertificateAccessDB();
        this.eventAccess = new PartnerEventAccessDB();
        this.oAuth2Access = new OAuth2AccessDB(dbDriverManager, SystemEventManagerImplAS2.instance());
    }

    /**
     * Requires a query to select partners from the DB. Works in a transaction
     * context on the passed database connection. Requires a LOCK on the
     * following tables: partner, certificates, httpheader, partnerevent, oauth2
     *
     * @param dataCompleteness Allows to get partner object with lesser
     * information
     */
    private List<Partner> getPartnerByQuery(String query, String parameter, int dataCompleteness,
            Connection configConnectionNoAutoCommit) throws Exception {
        List<Partner> partnerList = new ArrayList<Partner>();
        try (PreparedStatement preparedStatement = configConnectionNoAutoCommit.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setString(1, parameter);
            }
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Partner partner = new Partner();
                    partner.setAS2Identification(result.getString("as2ident"));
                    partner.setName(result.getString("partnername"));
                    partner.setDBId(result.getInt("id"));
                    partner.setLocalStation(result.getInt("islocal") == 1);
                    //All partner data is requested - deliver it
                    if (dataCompleteness == DATA_COMPLETENESS_FULL) {
                        partner.setSignType(result.getInt("sign"));
                        partner.setEncryptionType(result.getInt("encrypt"));
                        partner.setEmail(result.getString("email"));
                        partner.setURL(result.getString("url"));
                        partner.setMdnURL(result.getString("mdnurl"));
                        partner.setSubject(result.getString("msgsubject"));
                        partner.setContentType(result.getString("contenttype"));
                        partner.setSyncMDN(result.getInt("syncmdn") == 1);
                        partner.setPollIgnoreListString(result.getString("pollignorelist"));
                        partner.setPollInterval(result.getInt("pollinterval"));
                        partner.setCompressionType(result.getInt("msgcompression"));
                        partner.setSignedMDN(result.getInt("signedmdn") == 1);
                        partner.setKeepOriginalFilenameOnReceipt(result.getInt("keeporiginalfilenameonreceipt") == 1);
                        HTTPAuthentication authentication = partner.getAuthenticationCredentialsMessage();
                        authentication.setUser(result.getString("httpauthuser"));
                        authentication.setPassword(result.getString("httpauthpass"));
                        authentication.setAuthMode(result.getInt("authmodehttp"));
                        HTTPAuthentication asyncAuthentication = partner.getAuthenticationCredentialsAsyncMDN();
                        asyncAuthentication.setUser(result.getString("httpauthuserasnymdn"));
                        asyncAuthentication.setPassword(result.getString("httpauthpassasnymdn"));
                        asyncAuthentication.setAuthMode(result.getInt("authmodehttpasynmdn"));
                        partner.setComment(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercomment"));
                        partner.setContactAS2(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercontact"));
                        partner.setContactCompany(this.dbDriverManager.readTextStoredAsJavaObject(result, "partneraddress"));
                        partner.setNotifyReceive(result.getInt("notifyreceive"));
                        partner.setNotifySend(result.getInt("notifysend"));
                        partner.setNotifySendReceive(result.getInt("notifysendreceive"));
                        partner.setNotifyReceiveEnabled(result.getInt("notifyreceiveenabled") == 1);
                        partner.setNotifySendEnabled(result.getInt("notifysendenabled") == 1);
                        partner.setNotifySendReceiveEnabled(result.getInt("notifysendreceiveenabled") == 1);
                        partner.setContentTransferEncoding(result.getInt("contenttransferencoding"));
                        partner.setHttpProtocolVersion(result.getString("httpversion"));
                        partner.setMaxPollFiles(result.getInt("maxpollfiles"));
                        partner.setUseAlgorithmIdentifierProtectionAttribute(result.getInt("algidentprotatt") == 1);
                        partner.setEnableDirPoll(result.getInt("enabledirpoll") == 1);
                        partner.setOverwriteLocalStationSecurity(result.getInt("overwritelocalsecurity") == 1);
                        partner.setCreatedByUserId(result.getInt("created_by_user_id"));
                        //ensure to have a valid partner DB id before loading the releated data
                        this.certificateAccess.loadPartnerCertificateInformation(partner, configConnectionNoAutoCommit);
                        this.loadHTTPHeaderIntoPartner(partner, configConnectionNoAutoCommit);
                        this.eventAccess.loadPartnerEvents(partner, configConnectionNoAutoCommit);
                        partner.setUseOAuth2Message(result.getInt("useoauth2message") == 1);
                        int oAuth2ReferenceMessage = result.getInt("oauth2idmessage");
                        if (!result.wasNull()) {
                            OAuth2Config oAuth2ConfigMessage = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMessage, configConnectionNoAutoCommit);
                            partner.setOAuth2Message(oAuth2ConfigMessage);
                        }
                        partner.setUseOAuth2MDN(result.getInt("useoauth2mdn") == 1);
                        int oAuth2ReferenceMDN = result.getInt("oauth2idmdn");
                        if (!result.wasNull()) {
                            OAuth2Config oAuth2ConfigMDN = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMDN, configConnectionNoAutoCommit);
                            partner.setOAuth2MDN(oAuth2ConfigMDN);
                        }
                        //load visibility settings (only for remote partners)
                        if (!partner.isLocalStation()) {
                            List<Integer> visibleUsers = this.loadPartnerVisibility(partner.getDBId(), configConnectionNoAutoCommit);
                            partner.setVisibleToUserIds(visibleUsers);
                        }
                    }
                    partnerList.add(partner);
                }
                Collections.sort(partnerList);
                return (partnerList);
            }
        }
    }

    /**
     * Returns the number of partner in the system
     */
    public int getPartnerCount() {
        int counter = 0;
        try (Connection configConnectionAutoCommit
                = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            try (PreparedStatement statement = configConnectionAutoCommit.prepareStatement(
                    "SELECT COUNT(1) AS partnercount FROM partner")) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        counter = result.getInt("partnercount");
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (counter);
    }

    /**
     * Requires a query to select partners from the DB. Establishes a new
     * connection to the database and gets the data transactional
     *
     * @param dataCompleteness Allows to get partner object with lesser
     * information
     */
    private List<Partner> getPartnerByQuery(String query, String parameter, int dataCompleteness) {
        List<Partner> partnerList = new ArrayList<Partner>();
        String transactionName = "Partner_read";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            configConnectionNoAutoCommit.setReadOnly(true);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "httpheader",
                            "partnerevent",
                            "oauth2",
                            "partner_user_visibility"
                        });
                try {
                    partnerList.addAll(this.getPartnerByQuery(query, parameter, dataCompleteness, configConnectionNoAutoCommit));
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (partnerList);
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getAllPartner(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner", null, dataCompleteness));
    }

    /**
     * Returns all partner stored in the DB with all information, even the local
     * station
     */
    public List<Partner> getAllPartner() {
        return (this.getAllPartner(DATA_COMPLETENESS_FULL));
    }

    /**
     * Returns all partner stored in the DB with all information, even the local
     * station. The transactional context of the passed connection has to be
     * handled outside
     */
    public List<Partner> getAllPartner(int dataCompleteness, Connection configConnectionNoAutoCommit) throws Exception {
        return (this.getPartnerByQuery("SELECT * FROM partner", null, dataCompleteness, configConnectionNoAutoCommit));
    }

    /**
     * Returns all local stations stored in the DB
     */
    public List<Partner> getLocalStations(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner WHERE islocal=1", null, dataCompleteness));
    }

    /**
     * Returns all local stations stored in the DB
     */
    public List<Partner> getLocalStations() {
        return (this.getLocalStations(DATA_COMPLETENESS_FULL));
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getNonLocalStations(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner WHERE islocal<>1", null, dataCompleteness));
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getNonLocalStations() {
        return (this.getNonLocalStations(DATA_COMPLETENESS_FULL));
    }

    /**
     * Updates a single partner to the database by creating a new DB connection
     */
    public void updatePartner(Partner partner) {
        String transactionName = "PartnerAccessDB_updatePartner";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockDELETE(
                        transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "partnerevent",
                            "httpheader",
                            "partnersystem",
                            "oauth2"
                        });
                try {
                    this.updatePartner(partner, configConnectionNoAutoCommit);
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

    /**
     * Updates a single partner in the db
     */
    /**
     * Inserts a new partner into the database DELETE lock on certificates
     * UPDATE lock on partner DELETE lock on partnerevent
     *
     */
    public void updatePartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                "UPDATE partner SET "
                + "as2ident=?,partnername=?,islocal=?,sign=?,encrypt=?,email=?,url=?,"
                + "mdnurl=?,msgsubject=?,contenttype=?,syncmdn=?,pollignorelist=?,"
                + "pollinterval=?,msgcompression=?,signedmdn=?,"
                + "authmodehttp=?,httpauthuser=?,httpauthpass=?,"
                + "authmodehttpasynmdn=?,httpauthuserasnymdn=?,httpauthpassasnymdn=?,"
                + "keeporiginalfilenameonreceipt=?,partnercomment=?,notifysend=?,"
                + "notifyreceive=?,notifysendreceive=?,notifysendenabled=?,"
                + "notifyreceiveenabled=?,notifysendreceiveenabled=?,"
                + "contenttransferencoding=?,httpversion=?,"
                + "maxpollfiles=?,partnercontact=?,partneraddress=?,algidentprotatt=?,"
                + "enabledirpoll=?,useoauth2message=?,useoauth2mdn=?,"
                + "oauth2idmessage=?,oauth2idmdn=?,overwritelocalsecurity=? "
                + "WHERE id=?")) {
            preparedStatement.setString(1, partner.getAS2Identification());
            preparedStatement.setString(2, partner.getName());
            preparedStatement.setInt(3, partner.isLocalStation() ? 1 : 0);
            preparedStatement.setInt(4, partner.getSignType());
            preparedStatement.setInt(5, partner.getEncryptionType());
            preparedStatement.setString(6, partner.getEmail());
            preparedStatement.setString(7, partner.getURL());
            preparedStatement.setString(8, partner.getMdnURL());
            preparedStatement.setString(9, partner.getSubject());
            preparedStatement.setString(10, partner.getContentType());
            preparedStatement.setInt(11, partner.isSyncMDN() ? 1 : 0);
            preparedStatement.setString(12, partner.getPollIgnoreListAsString());
            preparedStatement.setInt(13, partner.getPollInterval());
            preparedStatement.setInt(14, partner.getCompressionType());
            preparedStatement.setInt(15, partner.isSignedMDN() ? 1 : 0);
            preparedStatement.setInt(16, partner.getAuthenticationCredentialsMessage().getAuthMode());
            preparedStatement.setString(17, partner.getAuthenticationCredentialsMessage().getUser());
            preparedStatement.setString(18, partner.getAuthenticationCredentialsMessage().getPassword());
            preparedStatement.setInt(19, partner.getAuthenticationCredentialsAsyncMDN().getAuthMode());
            preparedStatement.setString(20, partner.getAuthenticationCredentialsAsyncMDN().getUser());
            preparedStatement.setString(21, partner.getAuthenticationCredentialsAsyncMDN().getPassword());
            preparedStatement.setInt(22, partner.getKeepOriginalFilenameOnReceipt() ? 1 : 0);
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 23, partner.getComment());
            preparedStatement.setInt(24, partner.getNotifySend());
            preparedStatement.setInt(25, partner.getNotifyReceive());
            preparedStatement.setInt(26, partner.getNotifySendReceive());
            preparedStatement.setInt(27, partner.isNotifySendEnabled() ? 1 : 0);
            preparedStatement.setInt(28, partner.isNotifyReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(29, partner.isNotifySendReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(30, partner.getContentTransferEncoding());
            preparedStatement.setString(31, partner.getHttpProtocolVersion());
            preparedStatement.setInt(32, partner.getMaxPollFiles());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 33, partner.getContactAS2());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 34, partner.getContactCompany());
            preparedStatement.setInt(35, partner.getUseAlgorithmIdentifierProtectionAttribute() ? 1 : 0);
            preparedStatement.setInt(36, partner.isEnableDirPoll() ? 1 : 0);
            preparedStatement.setInt(37, partner.usesOAuth2Message() ? 1 : 0);
            preparedStatement.setInt(38, partner.usesOAuth2MDN() ? 1 : 0);
            if (partner.getOAuth2Message() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2Message(), configConnectionNoAutoCommit);
                preparedStatement.setInt(39, partner.getOAuth2Message().getDBId());
            } else {
                preparedStatement.setNull(39, Types.INTEGER);
            }
            if (partner.getOAuth2MDN() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2MDN(), configConnectionNoAutoCommit);
                preparedStatement.setInt(40, partner.getOAuth2MDN().getDBId());
            } else {
                preparedStatement.setNull(40, Types.INTEGER);
            }
            preparedStatement.setInt(41, partner.isOverwriteLocalStationSecurity() ? 1 : 0);
            //where statement
            preparedStatement.setInt(42, partner.getDBId());
            preparedStatement.executeUpdate();
            this.storeHTTPHeader(partner, configConnectionNoAutoCommit);
            this.certificateAccess.storePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
            this.eventAccess.storePartnerEvents(partner, configConnectionNoAutoCommit);
            //update visibility settings (only for remote partners)
            if (!partner.isLocalStation()) {
                // Ensure creator is always in visibility list if visibility is restricted
                partner.ensureCreatorInVisibilityList();
                this.updatePartnerVisibility(partner.getDBId(), partner.getVisibleToUserIds(), configConnectionNoAutoCommit);
            }
        }
    }

    /**
     * Deletes a single partner from the database by creating a new connection
     */
    public void deletePartner(Partner partner) {
        String transactionName = "PartnerAccess_delete";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockDELETE(
                        transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "partnerevent",
                            "httpheader",
                            "partnersystem"
                        });
                try {
                    this.deletePartner(partner, configConnectionNoAutoCommit);
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Exception e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Deletes a single partner from the database
     */
    public void deletePartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PartnerSystemAccessDB partnerSystemAccess = new PartnerSystemAccessDB(this.dbDriverManager);
        this.deleteHTTPHeader(partner, configConnectionNoAutoCommit);
        this.certificateAccess.deletePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
        this.eventAccess.deletePartnerEvents(partner, configConnectionNoAutoCommit);
        partnerSystemAccess.deletePartnerSystem(partner, configConnectionNoAutoCommit);
        try (PreparedStatement preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                "DELETE FROM partner WHERE id=?")) {
            preparedStatement.setInt(1, partner.getDBId());
            preparedStatement.executeUpdate();
            //this worked fine - try to delete oauth2 references which might fail because they might be used somewhere else
            OAuth2Config oauth2Message = partner.getOAuth2Message();
            if (oauth2Message != null) {
                this.oAuth2Access.deleteOAuth2(oauth2Message.getDBId());
            }
            OAuth2Config oauth2MDN = partner.getOAuth2MDN();
            if (oauth2MDN != null) {
                this.oAuth2Access.deleteOAuth2(oauth2MDN.getDBId());
            }
        }
    }

    /**
     * Inserts a single partner to the database by creating a new DB connection
     */
    public void insertPartner(Partner partner) {
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            String transactionName = "PartnerAccessDB_insertPartner";
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first. Delete is required because
                //the HTTP header is deleted
                this.dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "partnerevent",
                            "httpheader",
                            "oauth2"
                        });
                try {
                    this.insertPartner(partner, configConnectionNoAutoCommit);
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

    /**
     * Inserts a new partner into the database. This has to happen transactional
     * as data is stored in multiple tables and other processes may read
     * incomplete data else
     */
    public void insertPartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                "INSERT INTO partner("
                + "as2ident,partnername,islocal,sign,encrypt,email,url,mdnurl,"
                + "msgsubject,contenttype,syncmdn,pollignorelist,pollinterval,"
                + "msgcompression,signedmdn,"
                + "authmodehttp,httpauthuser,httpauthpass,authmodehttpasynmdn,"
                + "httpauthuserasnymdn,httpauthpassasnymdn,keeporiginalfilenameonreceipt,"
                + "partnercomment,notifysend,notifyreceive,notifysendreceive,"
                + "notifysendenabled,notifyreceiveenabled,notifysendreceiveenabled,"
                + "contenttransferencoding,httpversion,"
                + "maxpollfiles,partnercontact,partneraddress,algidentprotatt,enabledirpoll,"
                + "useoauth2message,useoauth2mdn,oauth2idmessage,oauth2idmdn,overwritelocalsecurity,created_by_user_id"
                + ")VALUES("
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            preparedStatement.setString(1, partner.getAS2Identification());
            preparedStatement.setString(2, partner.getName());
            preparedStatement.setInt(3, partner.isLocalStation() ? 1 : 0);
            preparedStatement.setInt(4, partner.getSignType());
            preparedStatement.setInt(5, partner.getEncryptionType());
            preparedStatement.setString(6, partner.getEmail());
            preparedStatement.setString(7, partner.getURL());
            preparedStatement.setString(8, partner.getMdnURL());
            preparedStatement.setString(9, partner.getSubject());
            preparedStatement.setString(10, partner.getContentType());
            preparedStatement.setInt(11, partner.isSyncMDN() ? 1 : 0);
            preparedStatement.setString(12, partner.getPollIgnoreListAsString());
            preparedStatement.setInt(13, partner.getPollInterval());
            preparedStatement.setInt(14, partner.getCompressionType());
            preparedStatement.setInt(15, partner.isSignedMDN() ? 1 : 0);
            preparedStatement.setInt(16, partner.getAuthenticationCredentialsMessage().getAuthMode());
            preparedStatement.setString(17, partner.getAuthenticationCredentialsMessage().getUser());
            preparedStatement.setString(18, partner.getAuthenticationCredentialsMessage().getPassword());
            preparedStatement.setInt(19, partner.getAuthenticationCredentialsAsyncMDN().getAuthMode());
            preparedStatement.setString(20, partner.getAuthenticationCredentialsAsyncMDN().getUser());
            preparedStatement.setString(21, partner.getAuthenticationCredentialsAsyncMDN().getPassword());
            preparedStatement.setInt(22, partner.getKeepOriginalFilenameOnReceipt() ? 1 : 0);
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 23, partner.getComment());
            preparedStatement.setInt(24, partner.getNotifySend());
            preparedStatement.setInt(25, partner.getNotifyReceive());
            preparedStatement.setInt(26, partner.getNotifySendReceive());
            preparedStatement.setInt(27, partner.isNotifySendEnabled() ? 1 : 0);
            preparedStatement.setInt(28, partner.isNotifyReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(29, partner.isNotifySendReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(30, partner.getContentTransferEncoding());
            preparedStatement.setString(31, partner.getHttpProtocolVersion());
            preparedStatement.setInt(32, partner.getMaxPollFiles());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 33, partner.getContactAS2());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 34, partner.getContactCompany());
            preparedStatement.setInt(35, partner.getUseAlgorithmIdentifierProtectionAttribute() ? 1 : 0);
            preparedStatement.setInt(36, partner.isEnableDirPoll() ? 1 : 0);
            preparedStatement.setInt(37, partner.usesOAuth2Message() ? 1 : 0);
            preparedStatement.setInt(38, partner.usesOAuth2MDN() ? 1 : 0);
            if (partner.getOAuth2Message() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2Message(), configConnectionNoAutoCommit);
                preparedStatement.setInt(39, partner.getOAuth2Message().getDBId());
            } else {
                preparedStatement.setNull(39, Types.INTEGER);
            }
            if (partner.getOAuth2MDN() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2MDN(), configConnectionNoAutoCommit);
                preparedStatement.setInt(40, partner.getOAuth2MDN().getDBId());
            } else {
                preparedStatement.setNull(40, Types.INTEGER);
            }
            preparedStatement.setInt(41, partner.isOverwriteLocalStationSecurity() ? 1 : 0);
            preparedStatement.setInt(42, partner.getCreatedByUserId());
            preparedStatement.executeUpdate();
        }
        partner.setDBId(this.getDBIdForPartner(partner.getAS2Identification(), configConnectionNoAutoCommit));
        this.storeHTTPHeader(partner, configConnectionNoAutoCommit);
        this.certificateAccess.storePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
        this.eventAccess.storePartnerEvents(partner, configConnectionNoAutoCommit);

        // Ensure creator is always in visibility list if visibility is restricted
        partner.ensureCreatorInVisibilityList();
        this.updatePartnerVisibility(partner.getDBId(), partner.getVisibleToUserIds(), configConnectionNoAutoCommit);
    }

    /**
     * returns the internal database id for the passed partner as2
     * identification
     *
     * @param as2ident
     * @param configConnection
     * @return
     */
    private int getDBIdForPartner(String as2ident, Connection configConnection) throws Exception {
        try (PreparedStatement statement = configConnection.prepareStatement("SELECT id FROM partner WHERE as2ident=?")) {
            statement.setString(1, as2ident);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return (result.getInt("id"));
                } else {
                    return (-1);
                }
            }
        }
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartner(String as2ident) {
        if (as2ident == null) {
            return (null);
        }
        return (this.getPartnerByAS2Id(as2ident, DATA_COMPLETENESS_FULL));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartnerByAS2Id(String as2ident, int dataCompleteness) {
        if (as2ident == null) {
            return (null);
        }
        String query = "SELECT * FROM partner WHERE as2ident=?";
        List<Partner> partner = this.getPartnerByQuery(query, as2ident, dataCompleteness);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartnerByName(String partnerName, int dataCompleteness) {
        String query = "SELECT * FROM partner WHERE upper(partnername)=?";
        List<Partner> partner = this.getPartnerByQuery(query, partnerName.toUpperCase(), dataCompleteness);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartner(int dbId) {
        String query = "SELECT * FROM partner WHERE id=" + dbId;
        List<Partner> partner = this.getPartnerByQuery(query, null, DATA_COMPLETENESS_FULL);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /*
     * loads the partner specific http headers from the db and assigns it to the
     * passed partner
     */
    private void loadHTTPHeaderIntoPartner(Partner partner, Connection configConnection) throws Exception {
        int partnerId = partner.getDBId();
        try (PreparedStatement statement
                = configConnection.prepareStatement("SELECT * FROM httpheader WHERE partnerid=?")) {
            statement.setInt(1, partnerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    PartnerHttpHeader header = new PartnerHttpHeader();
                    header.setKey(result.getString("headerkey"));
                    header.setValue(result.getString("headervalue"));
                    partner.addHttpHeader(header);
                }
            }
        }
    }

    /**
     * Deletes a single partners http header from the database. Requires DELETE
     * lock on httpheader
     *
     */
    private void deleteHTTPHeader(Partner partner, Connection configConnection) throws Exception {
        try (PreparedStatement statement
                = configConnection.prepareStatement(
                        "DELETE FROM httpheader WHERE partnerid=?")) {
            statement.setInt(1, partner.getDBId());
            statement.executeUpdate();
        }
    }

    /**
     * Updates a single partners http header in the db. Requires DELETE lock on
     * httpheader
     */
    private void storeHTTPHeader(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        this.deleteHTTPHeader(partner, configConnectionNoAutoCommit);
        //clear unused headers in the partner object
        partner.deleteEmptyHttpHeader();
        List<PartnerHttpHeader> headerList = partner.getHttpHeader();
        if (!headerList.isEmpty()) {
            try (PreparedStatement statement = configConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO httpheader(partnerid,headerkey,headervalue)VALUES(?,?,?)")) {
                for (PartnerHttpHeader header : headerList) {
                    statement.setInt(1, partner.getDBId());
                    statement.setString(2, header.getKey());
                    statement.setString(3, header.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }

    /**
     * Load the list of user IDs who can see this partner when sending messages.
     * Empty list means visible to all users.
     */
    private List<Integer> loadPartnerVisibility(int partnerId, Connection configConnection) throws Exception {
        List<Integer> userIds = new ArrayList<>();
        try (PreparedStatement statement = configConnection.prepareStatement(
                "SELECT user_id FROM partner_user_visibility WHERE partner_id=? ORDER BY user_id")) {
            statement.setInt(1, partnerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    userIds.add(result.getInt("user_id"));
                }
            }
        }
        return userIds;
    }

    /**
     * Update partner visibility settings.
     * Replaces all existing visibility records for this partner.
     */
    public void updatePartnerVisibility(int partnerId, List<Integer> userIds, Connection configConnectionNoAutoCommit) throws Exception {
        // Delete existing visibility records
        try (PreparedStatement statementDelete = configConnectionNoAutoCommit.prepareStatement(
                "DELETE FROM partner_user_visibility WHERE partner_id=?")) {
            statementDelete.setInt(1, partnerId);
            statementDelete.executeUpdate();
        }

        // Insert new records if specific users selected
        if (userIds != null && !userIds.isEmpty()) {
            try (PreparedStatement statementInsert = configConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO partner_user_visibility(partner_id, user_id) VALUES (?,?)")) {
                for (Integer userId : userIds) {
                    statementInsert.setInt(1, partnerId);
                    statementInsert.setInt(2, userId);
                    statementInsert.addBatch();
                }
                statementInsert.executeBatch();
            }
        }
    }

    /**
     * Get all partners visible to a specific WebUI user.
     * Returns all local stations + remote partners that are either:
     * - Visible to all (no visibility records), OR
     * - Specifically assigned to this user
     */
    public List<Partner> getPartnersVisibleToUser(int userId, int dataCompleteness) {
        String sql
                = "SELECT DISTINCT p.* FROM partner p "
                + "LEFT JOIN partner_user_visibility pv ON p.id = pv.partner_id AND pv.user_id = ? "
                + "WHERE p.islocal = 1 " // All local stations
                + "OR NOT EXISTS (SELECT 1 FROM partner_user_visibility WHERE partner_id = p.id) " // Remote partners visible to all
                + "OR pv.user_id = ? " // Remote partners specifically assigned to this user
                + "ORDER BY p.partnername";

        List<Partner> partnerList = new ArrayList<Partner>();
        String transactionName = "Partner_visibility_read";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            configConnectionNoAutoCommit.setReadOnly(true);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "httpheader",
                            "partnerevent",
                            "oauth2",
                            "partner_user_visibility"
                        });
                try {
                    try (PreparedStatement preparedStatement = configConnectionNoAutoCommit.prepareStatement(sql)) {
                        preparedStatement.setInt(1, userId);
                        preparedStatement.setInt(2, userId);
                        try (ResultSet result = preparedStatement.executeQuery()) {
                            while (result.next()) {
                                Partner partner = new Partner();
                                partner.setAS2Identification(result.getString("as2ident"));
                                partner.setName(result.getString("partnername"));
                                partner.setDBId(result.getInt("id"));
                                partner.setLocalStation(result.getInt("islocal") == 1);
                                //All partner data is requested - deliver it
                                if (dataCompleteness == DATA_COMPLETENESS_FULL) {
                                    partner.setSignType(result.getInt("sign"));
                                    partner.setEncryptionType(result.getInt("encrypt"));
                                    partner.setEmail(result.getString("email"));
                                    partner.setURL(result.getString("url"));
                                    partner.setMdnURL(result.getString("mdnurl"));
                                    partner.setSubject(result.getString("msgsubject"));
                                    partner.setContentType(result.getString("contenttype"));
                                    partner.setSyncMDN(result.getInt("syncmdn") == 1);
                                    partner.setPollIgnoreListString(result.getString("pollignorelist"));
                                    partner.setPollInterval(result.getInt("pollinterval"));
                                    partner.setCompressionType(result.getInt("msgcompression"));
                                    partner.setSignedMDN(result.getInt("signedmdn") == 1);
                                    partner.setKeepOriginalFilenameOnReceipt(result.getInt("keeporiginalfilenameonreceipt") == 1);
                                    HTTPAuthentication authentication = partner.getAuthenticationCredentialsMessage();
                                    authentication.setUser(result.getString("httpauthuser"));
                                    authentication.setPassword(result.getString("httpauthpass"));
                                    authentication.setAuthMode(result.getInt("authmodehttp"));
                                    HTTPAuthentication asyncAuthentication = partner.getAuthenticationCredentialsAsyncMDN();
                                    asyncAuthentication.setUser(result.getString("httpauthuserasnymdn"));
                                    asyncAuthentication.setPassword(result.getString("httpauthpassasnymdn"));
                                    asyncAuthentication.setAuthMode(result.getInt("authmodehttpasynmdn"));
                                    partner.setComment(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercomment"));
                                    partner.setContactAS2(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercontact"));
                                    partner.setContactCompany(this.dbDriverManager.readTextStoredAsJavaObject(result, "partneraddress"));
                                    partner.setNotifyReceive(result.getInt("notifyreceive"));
                                    partner.setNotifySend(result.getInt("notifysend"));
                                    partner.setNotifySendReceive(result.getInt("notifysendreceive"));
                                    partner.setNotifyReceiveEnabled(result.getInt("notifyreceiveenabled") == 1);
                                    partner.setNotifySendEnabled(result.getInt("notifysendenabled") == 1);
                                    partner.setNotifySendReceiveEnabled(result.getInt("notifysendreceiveenabled") == 1);
                                    partner.setContentTransferEncoding(result.getInt("contenttransferencoding"));
                                    partner.setHttpProtocolVersion(result.getString("httpversion"));
                                    partner.setMaxPollFiles(result.getInt("maxpollfiles"));
                                    partner.setUseAlgorithmIdentifierProtectionAttribute(result.getInt("algidentprotatt") == 1);
                                    partner.setEnableDirPoll(result.getInt("enabledirpoll") == 1);
                                    partner.setOverwriteLocalStationSecurity(result.getInt("overwritelocalsecurity") == 1);
                                    partner.setCreatedByUserId(result.getInt("created_by_user_id"));
                                    //ensure to have a valid partner DB id before loading the releated data
                                    this.certificateAccess.loadPartnerCertificateInformation(partner, configConnectionNoAutoCommit);
                                    this.loadHTTPHeaderIntoPartner(partner, configConnectionNoAutoCommit);
                                    this.eventAccess.loadPartnerEvents(partner, configConnectionNoAutoCommit);
                                    partner.setUseOAuth2Message(result.getInt("useoauth2message") == 1);
                                    int oAuth2ReferenceMessage = result.getInt("oauth2idmessage");
                                    if (!result.wasNull()) {
                                        OAuth2Config oAuth2ConfigMessage = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMessage, configConnectionNoAutoCommit);
                                        partner.setOAuth2Message(oAuth2ConfigMessage);
                                    }
                                    partner.setUseOAuth2MDN(result.getInt("useoauth2mdn") == 1);
                                    int oAuth2ReferenceMDN = result.getInt("oauth2idmdn");
                                    if (!result.wasNull()) {
                                        OAuth2Config oAuth2ConfigMDN = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMDN, configConnectionNoAutoCommit);
                                        partner.setOAuth2MDN(oAuth2ConfigMDN);
                                    }
                                    //load visibility settings (only for remote partners)
                                    if (!partner.isLocalStation()) {
                                        List<Integer> visibleUsers = this.loadPartnerVisibility(partner.getDBId(), configConnectionNoAutoCommit);
                                        partner.setVisibleToUserIds(visibleUsers);
                                    }
                                }
                                partnerList.add(partner);
                            }
                        }
                    }
                    Collections.sort(partnerList);
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return partnerList;
    }

}
