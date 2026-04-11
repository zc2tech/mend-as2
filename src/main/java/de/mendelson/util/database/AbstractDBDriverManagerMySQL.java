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
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Class needed to access the MySQL/MariaDB database
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public abstract class AbstractDBDriverManagerMySQL implements IDBDriverManager {

    @Override
    public void setTableLockExclusive(Statement statement, String tablenames[]) throws SQLException {
        // MySQL uses LOCK TABLES syntax with WRITE lock for exclusive access
        StringBuilder builder = new StringBuilder("LOCK TABLES ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(tablenames[i]).append(" WRITE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockINSERTAndUPDATE(Statement statement, String[] tablenames) throws SQLException {
        // MySQL table locking is incompatible with transactions
        // LOCK TABLES implicitly commits the current transaction in MySQL
        // For read-only connections, skip locking as MySQL InnoDB uses MVCC for read consistency
        boolean isReadOnly = false;
        try {
            isReadOnly = statement.getConnection().isReadOnly();
        } catch (SQLException e) {
            // Ignore, default to attempting lock
        }

        if (isReadOnly) {
            // Skip locking for read-only connections - MySQL InnoDB MVCC handles read consistency
            return;
        }

        // For write operations, use LOCK TABLES (this will implicitly commit any active transaction)
        StringBuilder builder = new StringBuilder("LOCK TABLES ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(tablenames[i]).append(" WRITE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockDELETE(Statement statement, String[] tablenames) throws SQLException {
        // MySQL uses WRITE lock for DELETE operations
        StringBuilder builder = new StringBuilder("LOCK TABLES ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(tablenames[i]).append(" WRITE");
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
        // MySQL START TRANSACTION with READ COMMITTED isolation level
        statement.execute("START TRANSACTION");
    }

    @Override
    public void commitTransaction(Statement statement, String transactionName) throws SQLException {
        statement.execute("COMMIT");
        // Unlock tables after commit
        try {
            statement.execute("UNLOCK TABLES");
        } catch (SQLException e) {
            // Ignore if no tables were locked
        }
    }

    @Override
    public void rollbackTransaction(Statement statement) throws SQLException {
        statement.execute("ROLLBACK");
        // Unlock tables after rollback
        try {
            statement.execute("UNLOCK TABLES");
        } catch (SQLException e) {
            // Ignore if no tables were locked
        }
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
        // MySQL/MariaDB stores serialized objects as BLOB
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
            statement.setNull(index, Types.BLOB);
        } else {
            // Serialize the object to bytes for MySQL BLOB column
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
     * Adds a limit clause to the SQL query. MySQL uses LIMIT syntax
     *
     * @param query
     * @param maxRows
     */
    @Override
    public String addLimitToQuery(String query, int maxRows) {
        return (query + " LIMIT " + maxRows);
    }
}
