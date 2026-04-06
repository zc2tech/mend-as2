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

package de.mendelson.comm.as2.preferences;

import de.mendelson.util.database.IDBDriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Database access class for inbound authentication credentials.
 * Provides CRUD operations for the inbound_auth_credentials table.
 *
 * @author Julian Xu
 */
public class InboundAuthCredentialAccessDB {

    private IDBDriverManager dbDriverManager;
    private Logger logger;

    public InboundAuthCredentialAccessDB(IDBDriverManager dbDriverManager, Logger logger) {
        this.dbDriverManager = dbDriverManager;
        this.logger = logger;
    }

    /**
     * Get all credentials for a specific auth type
     */
    public List<InboundAuthCredential> getCredentials(int authType) {
        List<InboundAuthCredential> credentials = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            stmt = conn.prepareStatement(
                "SELECT * FROM inbound_auth_credentials WHERE auth_type=? ORDER BY id"
            );
            stmt.setInt(1, authType);
            rs = stmt.executeQuery();

            while (rs.next()) {
                InboundAuthCredential cred = new InboundAuthCredential();
                cred.setId(rs.getInt("id"));
                cred.setAuthType(rs.getInt("auth_type"));
                cred.setUsername(rs.getString("username"));
                cred.setPassword(rs.getString("password"));
                cred.setCertAlias(rs.getString("cert_alias"));
                credentials.add(cred);
            }
        } catch (Exception e) {
            this.logger.severe("Error loading inbound auth credentials: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }

        return credentials;
    }

    /**
     * Get all credentials (both basic and certificate types)
     */
    public List<InboundAuthCredential> getAllCredentials() {
        List<InboundAuthCredential> credentials = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            stmt = conn.prepareStatement(
                "SELECT * FROM inbound_auth_credentials ORDER BY auth_type, id"
            );
            rs = stmt.executeQuery();

            while (rs.next()) {
                InboundAuthCredential cred = new InboundAuthCredential();
                cred.setId(rs.getInt("id"));
                cred.setAuthType(rs.getInt("auth_type"));
                cred.setUsername(rs.getString("username"));
                cred.setPassword(rs.getString("password"));
                cred.setCertAlias(rs.getString("cert_alias"));
                credentials.add(cred);
            }
        } catch (Exception e) {
            this.logger.severe("Error loading all inbound auth credentials: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }

        return credentials;
    }

    /**
     * Add a new credential
     */
    public void addCredential(InboundAuthCredential credential) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            stmt = conn.prepareStatement(
                "INSERT INTO inbound_auth_credentials(auth_type, username, password, cert_alias) VALUES (?,?,?,?)"
            );
            stmt.setInt(1, credential.getAuthType());
            stmt.setString(2, credential.getUsername());
            stmt.setString(3, credential.getPassword());
            stmt.setString(4, credential.getCertAlias());
            stmt.executeUpdate();
        } catch (Exception e) {
            this.logger.severe("Error adding inbound auth credential: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Delete a credential by ID
     */
    public void deleteCredential(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            stmt = conn.prepareStatement("DELETE FROM inbound_auth_credentials WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            this.logger.severe("Error deleting inbound auth credential: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Delete all credentials for a specific auth type
     */
    public void deleteAllCredentials(int authType) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            stmt = conn.prepareStatement("DELETE FROM inbound_auth_credentials WHERE auth_type=?");
            stmt.setInt(1, authType);
            stmt.executeUpdate();
        } catch (Exception e) {
            this.logger.severe("Error deleting all inbound auth credentials: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }
}
