//$Header: /as2/de/mendelson/comm/as2/log/LogAccessDB.java 55    12/03/25 17:28 Heller $
package de.mendelson.comm.as2.log;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access to the AS2 log that stores log messages for every transaction
 *
 * @author S.Heller
 * @version $Revision: 55 $
 */
public class LogAccessDB {

    private final int LEVEL_FINE = 3;
    private final int LEVEL_SEVERE = 2;
    private final int LEVEL_WARNING = 1;
    private final int LEVEL_INFO = 0;
    private final IDBDriverManager dbDriverManager;

    /**
     * Store the timestamps in the database in UTC to make the database portable
     * and to prevent daylight saving problems
     */
    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    /**
     */
    public LogAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    private int convertLevel(Level level) {
        if (level.equals(Level.WARNING)) {
            return (this.LEVEL_WARNING);
        }
        if (level.equals(Level.SEVERE)) {
            return (this.LEVEL_SEVERE);
        }
        if (level.equals(Level.FINE)) {
            return (this.LEVEL_FINE);
        }
        return (this.LEVEL_INFO);
    }

    private Level convertLevel(int level) {
        if (level == this.LEVEL_WARNING) {
            return (Level.WARNING);
        }
        if (level == this.LEVEL_SEVERE) {
            return (Level.SEVERE);
        }
        if (level == this.LEVEL_FINE) {
            return (Level.FINE);
        }
        return (Level.INFO);
    }

    /**
     * Inserts a new transmission log into the database. As this locks the
     * transaction table and is performed all the time this is threaded. It must
     * not block the processing in a critical way, its just the logging..
     */
    public Runnable generateThreadToInsert(Level level, long millis, String message, String messageId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                insertInBackground(level, millis, message, messageId);
            }
        };
        return (runnable);
    }

    /**
     * Adds a log line to the db - opens a new database connection first
     */
    private void insertInBackground(Level level, long millis, String logMessage, String messageId) {
        if (logMessage == null) {
            return;
        }
        try (Connection runtimeConnectionNoAutoCommit
                = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            this.logAsTransaction(runtimeConnectionNoAutoCommit, level, millis, logMessage, messageId);
        } catch (SQLException e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e);
        }
    }

    /**
     * Adds a single log line to the db
     */
    private void logAsTransaction(Connection runtimeConnectionNoAutoCommit,
            Level level, long millis, String logMessage, String messageId) {
        if (logMessage == null) {
            return;
        }
        String transactionName = "LogAccessDB_logAsTransaction";
        try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            //a lock might not be necessary here because this is a single statement query only but MySQL seems to have
            //problems with the locks
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{"messagelog"});
            try (PreparedStatement insertStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO messagelog(timestamputc,messageid,loglevel,details)VALUES(?,?,?,?)")) {
                insertStatement.setTimestamp(1, new Timestamp(millis), this.calendarUTC);
                insertStatement.setString(2, messageId);
                insertStatement.setInt(3, this.convertLevel(level));
                this.dbDriverManager.setTextParameterAsJavaObject(insertStatement, 4, logMessage);
                insertStatement.executeUpdate();
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (SQLIntegrityConstraintViolationException e) {
                String errorMessage = "LogAccessDB.log "
                        + "(" + e.getClass().getSimpleName() + "): "
                        + " The system tries to store a log entry for the message id \"" + messageId
                        + "\", but this message seems not to exist in the system.\n"
                        + "The reason might be an unreferenced MDN or a bad inbound AS2 message structure.";
                SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                        SystemEvent.ORIGIN_TRANSACTION, SystemEvent.TYPE_TRANSACTION_ANY);
                event.setBody(errorMessage + "\n\nLog message: \"" + logMessage + "\"");
                event.setSubject("Unreferenced MDN or bad message structure");
                SystemEventManagerImplAS2.instance().newEvent(event);
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * Returns the whole log of a single instance
     */
    public List<LogEntry> getLog(String messageId) {
        List<LogEntry> list = new ArrayList<LogEntry>();
        try (Connection runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try (PreparedStatement statement = runtimeConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM messagelog WHERE messageid=? ORDER BY timestamputc")) {
                statement.setString(1, messageId);
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    LogEntry entry = new LogEntry();
                    entry.setLevel(this.convertLevel(result.getInt("loglevel")));
                    String detailsStr = this.dbDriverManager.readTextStoredAsJavaObject(result, "details");
                    if (detailsStr != null) {
                        entry.setMessage(detailsStr);
                    }
                    entry.setMessageId(messageId);
                    entry.setMillis(result.getTimestamp("timestamputc", this.calendarUTC).getTime());
                    list.add(entry);
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (list);
    }

    /**
     * Deletes all information from the table messagelog regarding the passed
     * message instance. Needs a lock on the table messagelog
     */
    public void deleteMessageLog(List<String> messageIds, Connection runtimeConnectionNoAutoCommit) throws Exception {
        if (messageIds != null && !messageIds.isEmpty()) {
            StringBuilder deleteQuery = new StringBuilder(
                    "DELETE FROM messagelog WHERE messageid IN (");
            for (int i = 0; i < messageIds.size(); i++) {
                if (i > 0) {
                    deleteQuery.append(",");
                }
                deleteQuery.append("?");
            }
            deleteQuery.append(")");
            try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(deleteQuery.toString())) {
                for (int i = 0; i < messageIds.size(); i++) {
                    statement.setString(i + 1, messageIds.get(i));
                }
                statement.executeUpdate();
            }
        } else {
            try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "DELETE FROM messagelog WHERE messageid IS NULL")) {
                statement.executeUpdate();
            }
        }
    }

    /**
     * Deletes all information from the table messagelog regarding the passed
     * message instance - opens a new db connection - transactional
     */
    public void deleteMessageLog(List<String> messageIds) {
        String transactionname = "Message_deleteLog";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                //start transaction
                this.dbDriverManager.startTransaction(transactionStatement, transactionname);
                this.dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "messagelog"});
                try {
                    this.deleteMessageLog(messageIds, runtimeConnectionNoAutoCommit);
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionname);
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
