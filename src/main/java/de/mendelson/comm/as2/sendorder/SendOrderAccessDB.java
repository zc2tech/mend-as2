//$Header: /as2/de/mendelson/comm/as2/sendorder/SendOrderAccessDB.java 36    12/03/25 16:07 Heller $
package de.mendelson.comm.as2.sendorder;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Accesses the queue for the internal send orders
 *
 * @author S.Heller
 * @version $Revision: 36 $
 */
public class SendOrderAccessDB {

    private final IDBDriverManager dbDriverManager;

    public SendOrderAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Deletes an entry in the database that contains a send order. Opens a new
     * connection to the DB
     *
     */
    public void delete(int dbId) {
        String transactionName = "SendOrder_delete";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "sendorder"
                        });
                try {
                    this.delete(dbId, runtimeConnectionNoAutoCommit);
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
     * Deletes an entry in the database that contains a send order
     *
     * @param dbId
     */
    private void delete(int dbId, Connection runtimeConnectionNoAutoCommit) throws Exception {
        if (dbId == -1) {
            return;
        }
        try (PreparedStatement preparedStatement
                = runtimeConnectionNoAutoCommit.prepareStatement(
                        "DELETE FROM sendorder WHERE id=?")) {
            preparedStatement.setInt(1, dbId);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Reschedules an existing order
     */
    public void rescheduleOrder(SendOrder order, long nextExecutionTime) {
        String transactionName = "SendOrder_reschedule";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "sendorder"
                        });
                try (PreparedStatement statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement(
                        "UPDATE sendorder SET nextexecutiontime=?,sendorder=?,orderstate=? WHERE id=?")) {
                    statementUpdate.setLong(1, nextExecutionTime);
                    this.dbDriverManager.setObjectParameterAsJavaObject(statementUpdate, 2, order);
                    statementUpdate.setInt(3, SendOrder.STATE_WAITING);
                    //condition
                    statementUpdate.setInt(4, order.getDbId());
                    statementUpdate.executeUpdate();
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
     * Adds a new send order to the outbound queue
     */
    public void add(SendOrder order) {
        String transactionName = "SendOrder_add";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{
                        "sendorder"
                    });
            try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO sendorder(scheduletime,nextexecutiontime,sendorder,orderstate)"
                    + "VALUES(?,?,?,?)")) {
                statement.setLong(1, System.currentTimeMillis());
                //execute as soon as possible
                statement.setLong(2, System.currentTimeMillis());
                this.dbDriverManager.setObjectParameterAsJavaObject(statement, 3, order);
                statement.setInt(4, SendOrder.STATE_WAITING);
                statement.executeUpdate();
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
    }

    /**
     * On a server start all the available transaction should be reset to the
     * wait state
     */
    public void resetAllToWaiting() {
        String transactionName = "SendOrder_resetAllToWaiting";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "sendorder"
                        });
                try (PreparedStatement statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement(
                        "UPDATE sendorder SET orderstate=?")) {
                    statementUpdate.setInt(1, SendOrder.STATE_WAITING);
                    statementUpdate.executeUpdate();
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
     * Sets a new state to a send order
     */
    private void setState(int id, int orderState, Connection runtimeConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statementUpdate
                = runtimeConnectionNoAutoCommit.prepareStatement(
                        "UPDATE sendorder SET orderstate=? WHERE id=?")) {
            statementUpdate.setInt(1, orderState);
            statementUpdate.setLong(2, id);
            statementUpdate.executeUpdate();
        }
    }

    /**
     * Returns the next n scheduled orders or an empty list if none exists. This
     * reads orders from the database queue and deletes them once picked up.
     * This is a transactional operation, the database table is locked
     */
    public List<SendOrder> getNext(int maxCount) {
        final List<SendOrder> sendOrderList = new ArrayList<SendOrder>();
        final String transactionName = "SendOrder_next";
        try (Connection runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(
                IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            int count = 0;
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                dbDriverManager.startTransaction(transactionStatement, transactionName);
                dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "sendorder"
                        });
                try (PreparedStatement preparedStatementSelect = runtimeConnectionNoAutoCommit.prepareStatement(
                        "SELECT * FROM sendorder WHERE orderstate=? "
                        + "AND nextexecutiontime <=? ORDER BY nextexecutiontime")) {
                    preparedStatementSelect.setInt(1, SendOrder.STATE_WAITING);
                    preparedStatementSelect.setLong(2, System.currentTimeMillis());
                    try (ResultSet result = preparedStatementSelect.executeQuery()) {
                        while (result.next() && count < maxCount) {
                            Object orderObject = null;
                            try {
                                orderObject = this.dbDriverManager.readObjectStoredAsJavaObject(result, "sendorder");
                            } catch (Throwable invalidClassExeption) {
                                //nop
                            }
                            SendOrder order = null;
                            if (orderObject != null) {
                                if (orderObject instanceof SendOrder) {
                                    //this happens if you read the serialized object from HSQLDB
                                    order = (SendOrder) orderObject;
                                    int id = result.getInt("id");
                                    order.setDbId(id);
                                    //do not let it pick up by any other node/process now: Set the state to processing
                                    this.setState(id, SendOrder.STATE_PROCESSING, runtimeConnectionNoAutoCommit);
                                    sendOrderList.add(order);
                                    count++;
                                } else if (orderObject instanceof byte[]) {
                                    //this happens if you read the serialized object from mySQL
                                    try (ByteArrayInputStream memIn = new ByteArrayInputStream((byte[]) orderObject)) {
                                        try (ObjectInput in = new ObjectInputStream(memIn)) {
                                            SendOrder sendOrderObj = (SendOrder) in.readObject();
                                            int id = result.getInt("id");
                                            sendOrderObj.setDbId(id);
                                            //do not let it pick up by any other node/process now: Set the state to processing
                                            this.setState(id, SendOrder.STATE_PROCESSING, runtimeConnectionNoAutoCommit);
                                            sendOrderList.add(sendOrderObj);
                                            count++;
                                        }
                                    }
                                }
                            } else {
                                //delete the entry from the database, its from an older version or an invalid entry
                                int id = result.getInt("id");
                                this.delete(id, runtimeConnectionNoAutoCommit);
                                break;
                            }
                        }
                    }
                    //all ok - finish transaction
                    this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ROLLBACK);
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                    //return empty list
                    return (new ArrayList<SendOrder>());
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (sendOrderList);
    }
}
