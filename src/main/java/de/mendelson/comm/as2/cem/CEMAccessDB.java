package de.mendelson.comm.as2.cem;

import de.mendelson.comm.as2.cem.messages.EDIINTCertificateExchangeRequest;
import de.mendelson.comm.as2.cem.messages.EDIINTCertificateExchangeResponse;
import de.mendelson.comm.as2.cem.messages.TrustRequest;
import de.mendelson.comm.as2.cem.messages.TrustResponse;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access the certificate lists in the database
 *
 * @author S.Heller
 * @version $Revision: 35 $
 */
public class CEMAccessDB {

    private final IDBDriverManager dbDriverManager;

    /**
     * Creates new message I/O log and connects to localhost
     *
     */
    public CEMAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * For debug purpose
     */
    public static String convertCategory(int category) {
        if (category == CEMEntry.CATEGORY_CRYPT) {
            return ("encryption");
        } else if (category == CEMEntry.CATEGORY_TLS) {
            return ("SSL");
        } else if (category == CEMEntry.CATEGORY_SIGN) {
            return ("signature");
        } else {
            return ("unknown");
        }
    }

    /**
     * Returns if a request with the passed request id exists in the system
     */
    public boolean requestExists(String requestId) {
        try (Connection runtimeConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            try {
                //get SSL and sign certificates
                String query = "SELECT COUNT(1) AS counter FROM cem WHERE requestid=?";
                try (PreparedStatement statement = runtimeConnectionAutoCommit.prepareStatement(query)) {
                    statement.setString(1, requestId);
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            return (result.getInt("counter") > 0);
                        }
                    }
                }
            } catch (Exception e) {
                SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (false);
    }

    /**
     * Marks a request as processed, it will no longer be processed by the
     * CertificateCEMController
     */
    public void markAsProcessed(String requestId, int category) {
        String transactionName = "CEM_markAsProcessed";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //get SSL and sign certificates
                String query = "UPDATE cem SET processed=?,processdate=? WHERE requestid=? AND category=?";
                try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(query)) {
                    statement.setInt(1, 1);
                    statement.setLong(2, System.currentTimeMillis());
                    statement.setString(3, requestId);
                    statement.setInt(4, category);
                    statement.executeUpdate();
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
     * Returns all available CEM entries that have the state pending
     */
    public List<CEMEntry> getCEMEntriesPending() {
        List<CEMEntry> entryList = new ArrayList<CEMEntry>();
        try (Connection runtimeConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            //get SSL and sign certificates
            String query = "SELECT * FROM cem WHERE cemstate=? ORDER BY id";
            try (PreparedStatement statement = runtimeConnectionAutoCommit.prepareStatement(query)) {
                statement.setInt(1, CEMEntry.STATUS_PENDING_INT);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        CEMEntry cemEntry = new CEMEntry();
                        cemEntry.setCategory(result.getInt("category"));
                        cemEntry.setInitiatorAS2Id(result.getString("initiatoras2id"));
                        cemEntry.setReceiverAS2Id(result.getString("receiveras2id"));
                        long respondByDateValue = result.getLong("respondbydate");
                        if (!result.wasNull()) {
                            cemEntry.setRespondByDate(respondByDateValue);
                        }
                        cemEntry.setSerialId(result.getString("serialid"));
                        cemEntry.setRequestId(result.getString("requestid"));
                        cemEntry.setCemState(result.getInt("cemstate"));
                        cemEntry.setIssuername(result.getString("issuername"));
                        cemEntry.setProcessed(result.getInt("processed") != 0);
                        cemEntry.setRequestMessageid(result.getString("requestmessageid"));
                        cemEntry.setResponseMessageid(result.getString("responsemessageid"));
                        cemEntry.setRequestMessageOriginated(result.getLong("requestmessageoriginated"));
                        cemEntry.setResponseMessageOriginated(result.getLong("responsemessageoriginated"));
                        cemEntry.setProcessDate(result.getLong("processdate"));
                        String reasonForRejectionStr = this.dbDriverManager.readTextStoredAsJavaObject(result, "reasonforrejection");
                        if (reasonForRejectionStr != null) {
                            cemEntry.setReasonForRejection(reasonForRejectionStr);
                        }
                        entryList.add(cemEntry);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (entryList);
    }

    /**
     * Returns all available CEM entries
     */
    public List<CEMEntry> getCEMEntries() {
        List<CEMEntry> entryList = new ArrayList<CEMEntry>();
        try (Connection runtimeConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            //get SSL and sign certificates
            String query = "SELECT * FROM cem ORDER BY id";
            try (PreparedStatement statement = runtimeConnectionAutoCommit.prepareStatement(query)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        CEMEntry cemEntry = new CEMEntry();
                        cemEntry.setCategory(result.getInt("category"));
                        cemEntry.setInitiatorAS2Id(result.getString("initiatoras2id"));
                        cemEntry.setReceiverAS2Id(result.getString("receiveras2id"));
                        long respondByDateValue = result.getLong("respondbydate");
                        if (!result.wasNull()) {
                            cemEntry.setRespondByDate(respondByDateValue);
                        }
                        cemEntry.setSerialId(result.getString("serialid"));
                        cemEntry.setRequestId(result.getString("requestid"));
                        cemEntry.setCemState(result.getInt("cemstate"));
                        cemEntry.setIssuername(result.getString("issuername"));
                        cemEntry.setProcessed(result.getInt("processed") != 0);
                        cemEntry.setRequestMessageid(result.getString("requestmessageid"));
                        cemEntry.setResponseMessageid(result.getString("responsemessageid"));
                        cemEntry.setRequestMessageOriginated(result.getLong("requestmessageoriginated"));
                        cemEntry.setResponseMessageOriginated(result.getLong("responsemessageoriginated"));
                        cemEntry.setProcessDate(result.getLong("processdate"));
                        Object reasonForRejectionObj = result.getObject("reasonforrejection");
                        if (!result.wasNull() && reasonForRejectionObj instanceof String) {
                            cemEntry.setReasonForRejection((String) reasonForRejectionObj);
                        }
                        entryList.add(cemEntry);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (entryList);
    }

    /**
     * If a CEM is a SSL or signature request it contains a respondByDate entry.
     * This is the date where this entry should be set as ssl or signature cert
     *
     * @return
     */
    public List<CEMEntry> getCertificatesToChange() {
        List<CEMEntry> responseList = new ArrayList<CEMEntry>();
        try (Connection runtimeConnectionAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            //get SSL and sign certificates where the date is set
            try (PreparedStatement statementSelect1 = runtimeConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM cem WHERE (category=? OR category=?) "
                    + "AND (cemstate=? OR cemstate=?) "
                    + "AND respondbydate IS NOT NULL "
                    + "AND respondbydate < ? AND processed=?")) {
                statementSelect1.setInt(1, CEMEntry.CATEGORY_SIGN);
                statementSelect1.setInt(2, CEMEntry.CATEGORY_TLS);
                statementSelect1.setInt(3, CEMEntry.STATUS_ACCEPTED_INT);
                statementSelect1.setInt(4, CEMEntry.STATUS_PENDING_INT);
                statementSelect1.setLong(5, System.currentTimeMillis());
                statementSelect1.setInt(6, 0);
                try (ResultSet resultSign = statementSelect1.executeQuery()) {
                    while (resultSign.next()) {
                        CEMEntry cemEntry = new CEMEntry();
                        cemEntry.setCategory(resultSign.getInt("category"));
                        cemEntry.setInitiatorAS2Id(resultSign.getString("initiatoras2id"));
                        cemEntry.setReceiverAS2Id(resultSign.getString("receiveras2id"));
                        long respondByDateValue = resultSign.getLong("respondbydate");
                        if (!resultSign.wasNull()) {
                            cemEntry.setRespondByDate(respondByDateValue);
                        }
                        cemEntry.setSerialId(resultSign.getString("serialid"));
                        cemEntry.setRequestId(resultSign.getString("requestid"));
                        cemEntry.setIssuername(resultSign.getString("issuername"));
                        responseList.add(cemEntry);
                    }
                }
            }
            //get crypt certificates and sign/ssl certificates where respondbydate is not set and the partner has answered
            try (PreparedStatement statementSelect2 = runtimeConnectionAutoCommit.prepareStatement(
                    "SELECT * FROM cem WHERE (category=? OR ((category=? OR category=?) "
                    + "AND respondbydate IS NULL)) "
                    + "AND cemstate=? AND processed=?")) {
                statementSelect2.setInt(1, CEMEntry.CATEGORY_CRYPT);
                statementSelect2.setInt(2, CEMEntry.CATEGORY_SIGN);
                statementSelect2.setInt(3, CEMEntry.CATEGORY_TLS);
                statementSelect2.setInt(4, CEMEntry.STATUS_ACCEPTED_INT);
                statementSelect2.setInt(5, 0);
                try (ResultSet resultCrypt = statementSelect2.executeQuery()) {
                    while (resultCrypt.next()) {
                        CEMEntry cemEntry = new CEMEntry();
                        cemEntry.setCategory(resultCrypt.getInt("category"));
                        cemEntry.setInitiatorAS2Id(resultCrypt.getString("initiatoras2id"));
                        cemEntry.setReceiverAS2Id(resultCrypt.getString("receiveras2id"));
                        long respondByDateValue = resultCrypt.getLong("respondbydate");
                        if (!resultCrypt.wasNull()) {
                            cemEntry.setRespondByDate(respondByDateValue);
                        }
                        cemEntry.setSerialId(resultCrypt.getString("serialid"));
                        cemEntry.setRequestId(resultCrypt.getString("requestid"));
                        cemEntry.setIssuername(resultCrypt.getString("issuername"));
                        responseList.add(cemEntry);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        }
        return (responseList);
    }

    /**
     * Inserts a new CEM response into the database
     */
    public void insertResponse(AS2MessageInfo info, Partner initiator, Partner receiver, EDIINTCertificateExchangeResponse response) {
        List<TrustResponse> responseList = response.getTrustResponseList();
        for (TrustResponse trustResponse : responseList) {
            this.insertResponse(info, initiator, receiver, response, trustResponse);
        }
    }

    /**
     * Inserts a new CEM response into the database
     */
    public void insertResponse(AS2MessageInfo info, Partner initiator, Partner receiver,
            EDIINTCertificateExchangeResponse response,
            TrustResponse trustResponse) {
        String transactionName = "CEM_insertResponse";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                String query = "UPDATE cem SET cemstate=?, responsemessageid=?,responsemessageoriginated=?,reasonforrejection=? WHERE "
                        + "requestid=? AND initiatoras2id=? AND receiveras2id=? AND serialid=?";
                try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(query)) {
                    statement.setInt(1, trustResponse.getState());
                    statement.setString(2, info.getMessageId());
                    statement.setLong(3, response.getTradingPartnerInfo().getMessageOriginated().getTime());
                    this.dbDriverManager.setTextParameterAsJavaObject(statement, 4, trustResponse.getReasonForRejection());
                    statement.setString(5, response.getRequestId());
                    statement.setString(6, initiator.getAS2Identification());
                    statement.setString(7, receiver.getAS2Identification());
                    statement.setString(8, trustResponse.getCertificateReference().getSerialNumber());
                    statement.executeUpdate();
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
     * Inserts a new request into the cem database
     */
    public void insertRequest(AS2MessageInfo info, Partner initiator, Partner receiver, EDIINTCertificateExchangeRequest request) {
        List<TrustRequest> trustRequestList = request.getTrustRequestList();
        for (TrustRequest trustRequest : trustRequestList) {
            if (trustRequest.isCertUsageEncryption()) {
                this.insertTrustRequest(info, initiator, receiver, request, trustRequest, CEMEntry.CATEGORY_CRYPT);
            }
            if (trustRequest.isCertUsageSSL()) {
                this.insertTrustRequest(info, initiator, receiver, request, trustRequest, CEMEntry.CATEGORY_TLS);
            }
            if (trustRequest.isCertUsageSignature()) {
                this.insertTrustRequest(info, initiator, receiver, request, trustRequest, CEMEntry.CATEGORY_SIGN);
            }
        }
    }

    /**
     * Inserts a new request into the cem database
     */
    private void insertTrustRequest(AS2MessageInfo info, Partner initiator, Partner receiver, EDIINTCertificateExchangeRequest request,
            TrustRequest trustRequest, int category) {
        String transactionName = "CEM_insertTrustRequest";
        //cancel old entries with the same parameter
        this.setAllPendingRequestsToState(initiator.getAS2Identification(), receiver.getAS2Identification(), category, CEMEntry.STATUS_CANCELED_INT);
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                String query = "INSERT INTO cem(initiatoras2id,receiveras2id,requestid,requestmessageid,respondbydate,requestmessageoriginated,category,cemstate,serialid,issuername)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(query)) {
                    statement.setString(1, initiator.getAS2Identification());
                    statement.setString(2, receiver.getAS2Identification());
                    statement.setString(3, request.getRequestId());
                    statement.setString(4, info.getMessageId());
                    if (trustRequest.getRespondByDate() == null) {
                        statement.setNull(5, Types.BIGINT);
                    } else {
                        statement.setLong(5, trustRequest.getRespondByDate().getTime());
                    }
                    statement.setLong(6, request.getTradingPartnerInfo().getMessageOriginated().getTime());
                    statement.setInt(7, category);
                    //enter all requests as pending first. They are accepted by the response or the respondbydate
                    statement.setInt(8, CEMEntry.STATUS_PENDING_INT);
                    statement.setString(9, trustRequest.getEndEntity().getSerialNumber());
                    statement.setString(10, trustRequest.getEndEntity().getIssuerName());
                    statement.executeUpdate();
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
     * Updates a cem entry to a new state if its pending
     */
    public void setAllPendingRequestsToState(String initiatorAS2Id, String receiverAS2Id, int category, int newState) {
        this.setPendingRequestsToState(initiatorAS2Id, receiverAS2Id, category, null, newState);
    }

    /**
     * A new request for a chance came in. Update existing ones for this
     * relationship and category to canceled if they exist
     */
    public void setPendingRequestsToState(String initiatorAS2Id, String receiverAS2Id, int category, String requestId, int newState) {
        String transactionName = "CEM_setPendingRequestsToState";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                String query = "UPDATE cem SET cemstate=? WHERE initiatoras2id=? AND receiveras2id=? AND category=? AND (cemstate=? OR cemstate=?) AND processed=?";
                if (requestId != null) {
                    query += " AND requestId=?";
                }
                try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(query)) {
                    statement.setInt(1, newState);
                    statement.setString(2, initiatorAS2Id);
                    statement.setString(3, receiverAS2Id);
                    statement.setInt(4, category);
                    statement.setInt(5, CEMEntry.STATUS_PENDING_INT);
                    statement.setInt(6, CEMEntry.STATUS_ACCEPTED_INT);
                    statement.setInt(7, 0);
                    if (requestId != null) {
                        statement.setString(8, requestId);
                    }
                    statement.executeUpdate();
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
     * Remove an entry from the cem table
     */
    public void removeEntry(String initiatorAS2Id, String receiverAS2Id, int category, String requestId) {
        String transactionName = "CEM_removeEntry";
        try (Connection runtimeConnectionNoAutoCommit = this.dbDriverManager
                .getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME)) {
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            try (Statement transactionStatement = runtimeConnectionNoAutoCommit.createStatement()) {
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                String query = "DELETE FROM cem WHERE initiatoras2id=? AND receiveras2id=? AND category=? AND requestId=?";
                try (PreparedStatement statement = runtimeConnectionNoAutoCommit.prepareStatement(query)) {
                    statement.setString(1, initiatorAS2Id);
                    statement.setString(2, receiverAS2Id);
                    statement.setInt(3, category);
                    statement.setString(4, requestId);
                    statement.executeUpdate();
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

}
