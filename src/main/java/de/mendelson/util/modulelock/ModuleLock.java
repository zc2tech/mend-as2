//$Header: /oftp2/de/mendelson/util/modulelock/ModuleLock.java 27    12/03/25 13:13 Heller $
package de.mendelson.util.modulelock;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handles the locks of modules. Some modules are opened exclusive by a client -
 * all other clients should have just a read-only view.
 *
 * @author S.Heller
 * @version $Revision: 27 $
 */
public class ModuleLock {

    public static final String MODULE_SSL_KEYSTORE = "TLS keystore";
    public static final String MODULE_ENCSIGN_KEYSTORE = "ENC/SIGN keystore";
    public static final String MODULE_PARTNER = "Partner management";
    public static final String MODULE_SERVER_SETTINGS = "Server settings";

    private static final Logger logger = Logger.getAnonymousLogger();

    private ModuleLock() {
    }

    /**
     * Tries to set the lock for a module and returns the lock keeper. If the
     * lock is set successful the lock keeper is the passed client information.
     * if no lock could be set just null is returned
     */
    public static LockClientInformation setLock(String moduleName, LockClientInformation requestingClient,
            IDBDriverManager dbDriverManager) throws Exception {
        String transactionName = "ModuleLock_set";
        LockClientInformation returnLockKeeper = null;
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                //start transaction
                dbDriverManager.startTransaction(transactionStatement, transactionName);
                dbDriverManager.setTableLockExclusive(transactionStatement,
                        new String[]{"modulelock"});
                try {
                    LockClientInformation currentLockKeeper = getCurrentLockKeeper(moduleName, runtimeConnectionNoAutoCommit);
                    if (currentLockKeeper != null) {
                        if (currentLockKeeper.equals(requestingClient)) {
                            //perform a refresh, the requesting client is the lock keeper
                            _refreshLock(moduleName, requestingClient, runtimeConnectionNoAutoCommit);
                            returnLockKeeper = requestingClient;
                        } else {
                            //another client has the lock: just return its information
                            returnLockKeeper = currentLockKeeper;
                        }
                    } else {
                        //noone has the lock on this module: set it to the requesting client
                        _setLock(moduleName, requestingClient, runtimeConnectionNoAutoCommit);
                        returnLockKeeper = requestingClient;
                    }
                    dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    dbDriverManager.rollbackTransaction(transactionStatement);
                    throw e;
                }
            }
        }
        return (returnLockKeeper);
    }

    /**
     * Tries to refresh the lock for a module. Returns the lockkeeper anyway. If
     * no lock is set this is null
     */
    public static LockClientInformation refreshLock(String moduleName, LockClientInformation requestingClient,
            IDBDriverManager dbDriverManager) {
        String transactionName = "ModuleLock_refresh";
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                dbDriverManager.startTransaction(transactionStatement, transactionName);
                dbDriverManager.setTableLockExclusive(transactionStatement,
                        new String[]{"modulelock"});
                try {
                    LockClientInformation currentLockKeeper = getCurrentLockKeeper(moduleName, runtimeConnectionNoAutoCommit);
                    if (currentLockKeeper != null) {
                        if (currentLockKeeper.equals(requestingClient)) {
                            //perform a refresh, the requesting client is the lock keeper
                            _refreshLock(moduleName, requestingClient, runtimeConnectionNoAutoCommit);
                            //write the lock transactional to the database
                            dbDriverManager.commitTransaction(transactionStatement, transactionName);
                            return (requestingClient);
                        }
                    } else {
                        //its another lockkeeper: just rollback
                        dbDriverManager.rollbackTransaction(transactionStatement);
                    }
                } catch (Throwable e) {
                    dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
        }
        //this is an error: There is no lock on this module but a client tried to refresh it.
        return (null);
    }

    /**
     * Releases all locks, should be executed on server start
     */
    public static synchronized void releaseAllLocks(IDBDriverManager dbDriverManager) {
        String transationName = "ModuleLock_releaseAllLocks";
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                dbDriverManager.startTransaction(transactionStatement, transationName);
                dbDriverManager.setTableLockExclusive(transactionStatement,
                        new String[]{"modulelock"});
                try {
                    _deleteAllLocks(runtimeConnectionNoAutoCommit, 0L);
                    dbDriverManager.commitTransaction(transactionStatement, transationName);
                } catch (Throwable e) {
                    dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
        }
    }

    /**
     * Releases all locks, should be executed on server start
     */
    public static void releaseAllLocksOlderThan(IDBDriverManager dbDriverManager,
            SystemEventManager systemEventManager,
            long ageInms) {
        String transationName = "ModuleLock_releaseOlderThan";
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                dbDriverManager.startTransaction(transactionStatement, transationName);
                dbDriverManager.setTableLockExclusive(transactionStatement,
                        new String[]{"modulelock"});
                try {
                    _deleteAllLocks(runtimeConnectionNoAutoCommit, ageInms);
                    dbDriverManager.commitTransaction(transactionStatement, transationName);
                } catch (Throwable e) {
                    systemEventManager.systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
            systemEventManager.systemFailure(e);
        }
    }

    /**
     * Tries to release the lock for a module
     */
    public static void releaseLock(String moduleName, LockClientInformation clientInformation,
            IDBDriverManager dbDriverManager) {
        String transactionName = "ModuleLock_release";
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                dbDriverManager.startTransaction(transactionStatement, transactionName);
                dbDriverManager.setTableLockExclusive(transactionStatement,
                        new String[]{"modulelock"});
                try {
                    _deleteLock(moduleName, runtimeConnectionNoAutoCommit);
                    dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    dbDriverManager.rollbackTransaction(transactionStatement);
                }
            }
        } catch (Throwable e) {
        }
    }

    private static void _deleteAllLocks(Connection runtimeConnectionNoAutoCommit, long ageInms) throws Exception {
        if (ageInms > 0) {
            try (PreparedStatement preparedStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "DELETE FROM modulelock WHERE refreshlockmillies<?")) {
                long absoluteAge = System.currentTimeMillis() - ageInms;
                preparedStatement.setLong(1, absoluteAge);
                preparedStatement.executeUpdate();
            }
        } else {
            try (PreparedStatement preparedStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "DELETE FROM modulelock")) {
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Deletes a lock, could be run in transactional mode
     */
    private static void _deleteLock(String moduleName, Connection runtimeConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(
                "DELETE FROM modulelock WHERE modulename=?")) {
            statement.setString(1, moduleName);
            statement.executeUpdate();
        } catch (Throwable e) {
            logger.severe("ModuleLock._deleteLock: " + e.getMessage());
            throw e;
        }
    }

    private static void _setLock(String moduleName, LockClientInformation clientInformation,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(
                "INSERT INTO modulelock(modulename,startlockmillis,refreshlockmillies,"
                + "clientip,clientid,username,clientpid)"
                + "VALUES(?,?,?,?,?,?,?)")) {
            statement.setString(1, moduleName);
            long lockTime = System.currentTimeMillis();
            statement.setLong(2, lockTime);
            statement.setLong(3, lockTime);
            statement.setString(4, clientInformation.getClientIP());
            statement.setString(5, clientInformation.getUniqueid());
            statement.setString(6, clientInformation.getUsername());
            statement.setString(7, clientInformation.getPid());
            statement.executeUpdate();
        } catch (Exception e) {
            logger.severe("ModuleLock._setLock: " + e.getMessage());
            throw e;
        }
    }

    private static void _refreshLock(String moduleName, LockClientInformation clientInformation,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(
                "UPDATE modulelock SET refreshlockmillies=? "
                + "WHERE modulename=? AND clientid=?")) {
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, moduleName);
            statement.setString(3, clientInformation.getUniqueid());
            statement.executeUpdate();
        } catch (Throwable e) {
            logger.severe("ModuleLock._refreshLock: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Checks for an existing lock on the requested module and return the lock
     * keeper if there is one of null if there is none This is non transactional
     * - it just reads information
     */
    public static LockClientInformation getCurrentLockKeeper(String moduleName,
            Connection runtimeConnection) throws Exception {
        try (PreparedStatement statement = runtimeConnection.prepareStatement("SELECT * FROM modulelock WHERE modulename=?")) {
            statement.setString(1, moduleName);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String username = result.getString("username");
                    String clientIP = result.getString("clientip");
                    String uniqueId = result.getString("clientid");
                    String pid = result.getString("clientpid");
                    LockClientInformation clientInfo = new LockClientInformation(username, clientIP, uniqueId, pid);
                    return (clientInfo);
                } else {
                    return (null);
                }
            }
        } catch (Throwable e) {
            logger.severe(e.getMessage());
            throw e;
        }
    }

    /**
     * Checks for an existing lock on the requested module and return the lock
     * keeper if there is one of null if there is none This is non transactional
     * - it just reads information
     */
    public static LockClientInformation getCurrentLockKeeper(String moduleName, IDBDriverManager dbDriverManager) throws Exception {
        try(Connection runtimeConnectionAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)){
            return (getCurrentLockKeeper(moduleName, runtimeConnectionAutoCommit));
        } catch (Throwable e) {
            logger.severe("ModuleLock.getCurrentLockKeeper: " + e.getMessage());
            throw e;
        }
    }

    public static void displayDialogModuleLocked(JFrame parent, LockClientInformation lockKeeper, String nonLocalizedModuleName) {
        MecResourceBundle rb;
        //Load default resourcebundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleModuleLock.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        String text = rb.getResourceString("configuration.locked.otherclient",
                new Object[]{
                    rb.getResourceString(nonLocalizedModuleName),
                    lockKeeper.getClientIP(),
                    lockKeeper.getUsername(),
                    lockKeeper.getPid()
                });
        JOptionPane.showMessageDialog(parent,
                text,
                rb.getResourceString("modifications.notallowed.message"),
                JOptionPane.ERROR_MESSAGE);
    }

}
