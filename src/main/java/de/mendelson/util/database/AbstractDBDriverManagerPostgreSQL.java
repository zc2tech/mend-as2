//$Header: /mec_as4/de/mendelson/util/database/AbstractDBDriverManagerPostgreSQL.java 3     6.01.22 9:25 Heller $
package de.mendelson.util.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class needed to access the PostgreSQL database
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public abstract class AbstractDBDriverManagerPostgreSQL implements IDBDriverManager {

    @Override
    public void setTableLockExclusive(Statement statement, String tablenames[]) throws SQLException {
        // PostgreSQL uses LOCK TABLE syntax
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append("; ");
            }
            builder.append("LOCK TABLE ")
                   .append(tablenames[i])
                   .append(" IN EXCLUSIVE MODE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockINSERTAndUPDATE(Statement statement, String[] tablenames) throws SQLException {
        // PostgreSQL uses ROW EXCLUSIVE mode for INSERT/UPDATE operations
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append("; ");
            }
            builder.append("LOCK TABLE ")
                   .append(tablenames[i])
                   .append(" IN ROW EXCLUSIVE MODE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockDELETE(Statement statement, String[] tablenames) throws SQLException {
        // PostgreSQL uses ROW EXCLUSIVE mode for DELETE operations
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append("; ");
            }
            builder.append("LOCK TABLE ")
                   .append(tablenames[i])
                   .append(" IN ROW EXCLUSIVE MODE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void startTransaction(Statement statement, String transactionName) throws SQLException {
        if (statement.getConnection().getAutoCommit()) {
            throw new SQLException("Transaction "
                    + transactionName
                    + " started on database connection that is in auto commit mode");
        }
        // PostgreSQL BEGIN TRANSACTION with READ COMMITTED isolation level (default)
        statement.execute("BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED");
    }

    @Override
    public void commitTransaction(Statement statement, String transactionName) throws SQLException {
        statement.execute("COMMIT");
    }

    @Override
    public void rollbackTransaction(Statement statement) throws SQLException {
        statement.execute("ROLLBACK");
    }

    @Override
    public void setTextParameterAsJavaObject(PreparedStatement statement, int index, String text) throws SQLException {
        if (text == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, text);
        }
    }

    @Override
    public String readTextStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        String text = result.getString(columnName);
        if (!result.wasNull()) {
            return text;
        }
        return (null);
    }

    @Override
    public Object readObjectStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        // PostgreSQL stores serialized objects as bytea
        byte[] bytes = result.getBytes(columnName);
        if (!result.wasNull() && bytes != null) {
            try (java.io.ByteArrayInputStream byteStream = new java.io.ByteArrayInputStream(bytes);
                 java.io.ObjectInputStream objectStream = new java.io.ObjectInputStream(byteStream)) {
                return objectStream.readObject();
            }
        }
        return null;
    }

    @Override
    public void setObjectParameterAsJavaObject(PreparedStatement statement, int index, Object obj) throws Exception {
        if (obj == null) {
            statement.setNull(index, Types.BINARY);
        } else {
            // Serialize the object to bytes for PostgreSQL BYTEA column
            java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
            try (java.io.ObjectOutputStream objectStream = new java.io.ObjectOutputStream(byteStream)) {
                objectStream.writeObject(obj);
                objectStream.flush();
            }
            statement.setBytes(index, byteStream.toByteArray());
        }
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public byte[] readBytesStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        byte[] bytes = result.getBytes(columnName);
        if (!result.wasNull()) {
            return bytes;
        }
        return (null);
    }

    /**
     * Adds a limit clause to the SQL query. PostgreSQL uses LIMIT syntax
     *
     * @param query
     * @param maxRows
     */
    @Override
    public String addLimitToQuery(String query, int maxRows) {
        return (query + " LIMIT " + maxRows);
    }
}
