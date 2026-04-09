package de.mendelson.comm.as2.partner;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
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
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Database access wrapper for partner system information. This is the
 * information that is collected if the AS2 system connects to an other AS2
 * system, it will be displayed in the partner panel
 *
 * @author S.Heller
 * @version $Revision: 35 $
 */
public class PartnerSystemAccessDB {

    private final PartnerAccessDB partnerAccess;
    private final IDBDriverManager dbDriverManager;

    /**
     *
     */
    public PartnerSystemAccessDB(IDBDriverManager dbDriverManager) {
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns a list of all available partner system information
     *
     * @return
     */
    public List<PartnerSystem> getAllPartnerSystems() {
        List<PartnerSystem> list = new ArrayList<PartnerSystem>();
        List<Partner> allPartnerList = this.partnerAccess.getAllPartner(PartnerAccessDB.DATA_COMPLETENESS_FULL);
        try(Connection configConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)){
            try (PreparedStatement statement = configConnectionAutoCommit.prepareStatement("SELECT * FROM partnersystem")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int partnerId = result.getInt("partnerid");
                        Partner relatedPartner = null;
                        //this is really slow...
                        for (Partner partner : allPartnerList) {
                            if (partner.getDBId() == partnerId) {
                                relatedPartner = partner;
                                break;
                            }
                        }
                        if (relatedPartner != null) {
                            PartnerSystem system = new PartnerSystem();
                            system.setPartner(relatedPartner);
                            system.setAS2Version(result.getString("as2version"));
                            system.setProductName(result.getString("productname"));
                            system.setCEM(result.getInt("cem") == 1);
                            system.setCompression(result.getInt("msgcompression") == 1);
                            system.setMa(result.getInt("ma") == 1);
                            list.add(system);
                        }
                    }
                }
            }
            return (list);
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (null);
    }

    /**
     * Returns information about the system of a single partner
     */
    public PartnerSystem getPartnerSystem(Partner partner) {
        try(Connection configConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)){
            try (PreparedStatement statement = configConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM partnersystem WHERE partnerid=?")) {
                statement.setInt(1, partner.getDBId());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        PartnerSystem system = new PartnerSystem();
                        system.setPartner(partner);
                        system.setAS2Version(result.getString("as2version"));
                        system.setProductName(result.getString("productname"));
                        system.setCEM(result.getInt("cem") == 1);
                        system.setCompression(result.getInt("msgcompression") == 1);
                        system.setMa(result.getInt("ma") == 1);
                        return (system);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (null);
    }

    /**
     * Updates a single partnersystem in the db and returns the number of
     * updated rows. If the number of updates rows is 0 there should follow an
     * insert
     */
    private int updatePartnerSystem(PartnerSystem system, Connection configConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = configConnectionNoAutoCommit.prepareStatement(
                "UPDATE partnersystem SET as2version=?,productname=?,msgcompression=?,ma=?,cem=? WHERE partnerid=?")) {
            statement.setString(1, system.getAS2Version());
            statement.setString(2, system.getProductName());
            statement.setInt(3, system.supportsCompression() ? 1 : 0);
            statement.setInt(4, system.supportsMA() ? 1 : 0);
            statement.setInt(5, system.supportsCEM() ? 1 : 0);
            statement.setInt(6, system.getPartner().getDBId());
            return (statement.executeUpdate());
        }
    }

    /**
     * Deletes a single partner system from the database
     */
    protected void deletePartnerSystem(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = configConnectionNoAutoCommit.prepareStatement(
                "DELETE FROM partnersystem WHERE partnerid=?")) {
            statement.setInt(1, partner.getDBId());
            statement.executeUpdate();
        }
    }

    /**
     * Inserts a new entry into the database or updates an existing one. This
     * has to happen in a transaction as there are two statements to check if an
     * update was successful - if not an insert will happen
     */
    public void insertOrUpdatePartnerSystem(PartnerSystem partnerSystem) {
        String transactionName = "PartnerSystem_insert_update";
        try (Connection configConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
            configConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = configConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                        new String[]{
                            "partnersystem",});
                try {
                    int updatedRows = this.updatePartnerSystem(partnerSystem, configConnectionNoAutoCommit);
                    if (updatedRows == 0) {
                        this.insertPartnerSystem(partnerSystem, configConnectionNoAutoCommit);
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

    /**
     * Inserts a new partner system into the database
     */
    private void insertPartnerSystem(PartnerSystem partnerSystem, Connection configConnectionNoAutoCommit) throws Exception {
        try (PreparedStatement statement = configConnectionNoAutoCommit.prepareStatement(
                "INSERT INTO partnersystem(partnerid,as2version,productname,msgcompression,ma,cem)VALUES(?,?,?,?,?,?)")) {
            statement.setInt(1, partnerSystem.getPartner().getDBId());
            statement.setString(2, partnerSystem.getAS2Version());
            statement.setString(3, partnerSystem.getProductName());
            statement.setInt(4, partnerSystem.supportsCompression() ? 1 : 0);
            statement.setInt(5, partnerSystem.supportsMA() ? 1 : 0);
            statement.setInt(6, partnerSystem.supportsCEM() ? 1 : 0);
            statement.executeUpdate();
        }
    }
}
