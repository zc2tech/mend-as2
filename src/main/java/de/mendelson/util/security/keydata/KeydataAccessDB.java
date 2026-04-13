package de.mendelson.util.security.keydata;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.cert.KeystoreStorageImplDB;
import de.mendelson.util.security.cert.ResourceBundleKeystoreStorage;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Database access wrapper for key/certificate information
 *
 * @author S.Heller
 * @version $Revision: 24 $
 */
public class KeydataAccessDB {

    public static String REASON_IMPORT_INITIAL = "INITIAL";
    public static String REASON_IMPORT_COMMAND_LINE_SETTINGS = "COMMAND_LINE_SETTINGS";

    private final IDBDriverManager dbDriverManager;
    private final SystemEventManager systemEventManager;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public static final int KEYSTORE_USAGE_TLS = KeystoreStorageImplDB.KEYSTORE_USAGE_TLS;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplDB.KEYSTORE_USAGE_ENC_SIGN;
    public static String KEYSTORE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static String KEYSTORE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;

    public KeydataAccessDB(IDBDriverManager dbDriverManager, SystemEventManager systemEventManager) {
        this.dbDriverManager = dbDriverManager;
        this.systemEventManager = systemEventManager;
    }

    public static int keystoreTypeStrToInt(String keystoreType) {
        if (keystoreType != null && keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS)) {
            return (1);
        } else if (keystoreType != null && keystoreType.equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
            return (2);
        } else {
            throw new IllegalArgumentException("KeydataAccessDB.keystoreTypeStrToInt: Unsupported keystore type " + keystoreType);
        }
    }

    public static String intToKeystoreTypeStr(int keystoreType) {
        if (keystoreType == 1) {
            return (BCCryptoHelper.KEYSTORE_JKS);
        } else if (keystoreType == 2) {
            return (BCCryptoHelper.KEYSTORE_PKCS12);
        } else {
            throw new IllegalArgumentException("KeydataAccessDB.intToKeystoreTypeStr: Unsupported keystore type " + keystoreType);
        }
    }

    /**
     * Returns the timestamp in ms the keydata entry has been modified last
     */
    public long getLastChanged(int purpose, int userId) {
        try (Connection configConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            try (PreparedStatement statement = configConnectionAutoCommit.prepareStatement(
                    "SELECT lastchanged FROM keydata WHERE purpose=? AND user_id=?")) {
                statement.setInt(1, purpose);
                statement.setInt(2, userId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        long timestamp = result.getLong("lastchanged");
                        return (timestamp);
                    }
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (0);
    }

    /**
     * Returns the key storage of the requested purpose and user
     * @param purpose KEYSTORE_USAGE_TLS or KEYSTORE_USAGE_ENC_SIGN
     * @param userId User ID (0 = admin/system, >0 = specific user)
     */
    public KeystoreData getKeydata(int purpose, int userId) {
        try (Connection configConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            try (PreparedStatement selectStatement = configConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM keydata WHERE purpose=? AND user_id=?")) {
                selectStatement.setInt(1, purpose);
                selectStatement.setInt(2, userId);
                try (ResultSet result = selectStatement.executeQuery()) {
                    if (result.next()) {
                        byte[] keyData = this.dbDriverManager.readBytesStoredAsJavaObject(result, "storagedata");
                        String securityProvider = result.getString("securityprovider");
                        int storageType = result.getInt("storagetype");
                        KeystoreData data = new KeystoreData(securityProvider, storageType, keyData);
                        return (data);
                    }
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (null);
    }

    /**
     * Writes to the log that there has been a keystore import into the system
     *
     * @param importReason One of REASON_IMPORT_INITIAL,
     * REASON_IMPORT_COMMAND_LINE_SETTINGS
     */
    public void logKeystoreImport(Logger logger, Path keystoreFile, int purpose,
            String importReason) {
        String moveTitle = rb.getResourceString("moved.keystore.to.db.title",
                purpose == KeystoreStorageImplDB.KEYSTORE_USAGE_TLS ? "TLS" : "ENC/SIGN");
        String reasonText = "";
        if (importReason.equals(REASON_IMPORT_COMMAND_LINE_SETTINGS)) {
            reasonText = rb.getResourceString("moved.keystore.reason.commandline");
        } else if (importReason.equals(REASON_IMPORT_INITIAL)) {
            reasonText = rb.getResourceString("moved.keystore.reason.initial");
        }
        String moveText = rb.getResourceString("moved.keystore.to.db",
                new Object[]{
                    keystoreFile.toAbsolutePath().toString(),
                    purpose == KeystoreStorageImplDB.KEYSTORE_USAGE_TLS ? "TLS" : "ENC/SIGN"
                });
        logger.info(moveText);
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO, SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_CERTIFICATE_IMPORT_KEYSTORE);
        event.setBody(moveText + "\n" + reasonText);
        event.setSubject(moveTitle);
        systemEventManager.newEvent(event);
    }

    /**
     * Inserts a new key store into the database if none exists so far
     */
    public void insertKeydataFromFileIfItDoesNotExistInDB(Logger logger, Path keystoreFile,
            String storageType, int purpose, String securityProvider, int userId) {
        String transactionName = "Keydata_insert";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first to forbit any write operation
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "keydata"});
                boolean entryExists = true;
                try (PreparedStatement checkStatement = configConnectionNoAutoCommit.prepareStatement(
                        "SELECT COUNT(1) AS counter FROM keydata WHERE purpose=? AND user_id=?")) {
                    checkStatement.setInt(1, purpose);
                    checkStatement.setInt(2, userId);
                    try (ResultSet result = checkStatement.executeQuery()) {
                        if (result.next()) {
                            entryExists = result.getInt("counter") > 0;
                        }
                    }
                    if (!entryExists) {
                        byte[] keystoreData = Files.readAllBytes(keystoreFile);
                        try (PreparedStatement insertStatement = configConnectionNoAutoCommit.prepareStatement(
                                "INSERT INTO keydata(user_id,storagedata,storagetype,purpose,lastchanged,securityprovider)"
                                + "VALUES(?,?,?,?,?,?)")) {
                            insertStatement.setInt(1, userId);
                            this.dbDriverManager.setBytesParameterAsJavaObject(insertStatement, 2, keystoreData);
                            insertStatement.setInt(3, keystoreTypeStrToInt(storageType));
                            insertStatement.setInt(4, purpose);
                            insertStatement.setLong(5, System.currentTimeMillis());
                            insertStatement.setString(6, securityProvider);
                            insertStatement.executeUpdate();
                        }
                    }
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                    if (!entryExists) {
                        this.logKeystoreImport(logger, keystoreFile, purpose, REASON_IMPORT_INITIAL);
                    }
                } catch (Exception e) {
                    this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Updates an existing key store in the database. The update will only
     * happen if the keystoreData is different to the keystore data of this
     * purpose that is already in the system. Else the system data will left
     * untouched.
     */
    public void updateKeydata(byte[] keystoreData, String keystoreType, int purpose, String securityProvider, int userId) {
        String transactionName = "update_Keydata";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first to forbit any write operation
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "keydata"});
                try (PreparedStatement updateStatement = configConnectionNoAutoCommit.prepareStatement(
                        "UPDATE keydata SET "
                        + "storagedata=?,storagetype=?,lastchanged=?,securityprovider=? "
                        + "WHERE purpose=? AND user_id=?")) {
                    this.dbDriverManager.setBytesParameterAsJavaObject(updateStatement, 1, keystoreData);
                    updateStatement.setInt(2, keystoreTypeStrToInt(keystoreType));
                    updateStatement.setLong(3, System.currentTimeMillis());
                    updateStatement.setString(4, securityProvider);
                    //condition
                    updateStatement.setInt(5, purpose);
                    updateStatement.setInt(6, userId);
                    updateStatement.executeUpdate();
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Insert empty keystore data for a new user
     */
    public void insertEmptyKeydata(byte[] keystoreData, String keystoreType, int purpose, String securityProvider, int userId) {
        String transactionName = "insert_Keydata";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first to forbit any write operation
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{"keydata"});
                try (PreparedStatement insertStatement = configConnectionNoAutoCommit.prepareStatement(
                        "INSERT INTO keydata(user_id,storagedata,storagetype,purpose,lastchanged,securityprovider) "
                        + "VALUES(?,?,?,?,?,?)")) {
                    insertStatement.setInt(1, userId);
                    this.dbDriverManager.setBytesParameterAsJavaObject(insertStatement, 2, keystoreData);
                    insertStatement.setInt(3, keystoreTypeStrToInt(keystoreType));
                    insertStatement.setInt(4, purpose);
                    insertStatement.setLong(5, System.currentTimeMillis());
                    insertStatement.setString(6, securityProvider);
                    insertStatement.executeUpdate();
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Deletes the keydata entry of a special purpose
     */
    public void deleteKeydata(int purpose, int userId) {
        String transactionName = "delete_Keydata";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first to forbit any write operation
                this.dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "keydata"});
                try (PreparedStatement deleteStatement = configConnectionNoAutoCommit.prepareStatement(
                        "DELETE FROM keydata WHERE purpose=? AND user_id=?")) {
                    deleteStatement.setInt(1, purpose);
                    deleteStatement.setInt(2, userId);
                    deleteStatement.executeUpdate();
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            this.systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }
}
