package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class IncomingMessageRequest extends ClientServerMessage {

    private static final long serialVersionUID = 1L;
    private String contentType = null;
    private String remoteHost = null;
    private String remoteAddress = null;
    private Properties header = new Properties();
    private String messageDataFilename = null;
    private boolean usesTLS = false;
    private int localPort = -1;
    private String tlsProtocol = null;
    private String cipherSuite = null;
    /**
     * Indicates if this is a sync MDN. In this case there is no additional
     * connection information as this came in on the back channel
     * of the outbound connection
     */
    private boolean isSyncMDN = false;
    /**
     * Target user ID for user-specific message processing (0 = admin/system, >0 = specific user)
     */
    private int targetUserId = 0;

    public IncomingMessageRequest() {
    }

    @Override
    public String toString() {
        return ("Incoming message request");
    }

    public void addHeader(String key, String value) {
        this.header.setProperty(key.toLowerCase(), value);
    }

    /**
     * Deletes the existing request header and sets new
     */
    public void setHeader(Properties header) {
        this.header = header;
    }

    public Properties getHeader() {
        return (this.header);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * @return the messageDataFilename
     */
    public String getMessageDataFilename() {
        return messageDataFilename;
    }

    /**
     * @param messageDataFilename the messageDataFilename to set
     */
    public void setMessageDataFilename(String messageDataFilename) {
        this.messageDataFilename = messageDataFilename;
    }

    /**
     * @return the usesTLS
     */
    public boolean usesTLS() {
        return usesTLS;
    }

    /**
     * @param usesTLS the usesTLS to set
     */
    public void setUsesTLS(boolean usesTLS) {
        this.usesTLS = usesTLS;
    }

    /** Prevent an overwrite of the readObject method for de-serialization */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }

    /**
     * @return the localPort
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * @param localPort the localPort to set
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * @return the tlsProtocol
     */
    public String getTLSProtocol() {
        return tlsProtocol;
    }

    /**
     * @param tlsProtocol the tlsProtocol to set
     */
    public void setTLSProtocol(String tlsProtocol) {
        this.tlsProtocol = tlsProtocol;
    }

    /**
     * @return the cipherSuite
     */
    public String getCipherSuite() {
        return cipherSuite;
    }

    /**
     * @param cipherSuite the cipherSuite to set
     */
    public void setCipherSuite(String cipherSuite) {
        this.cipherSuite = cipherSuite;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * Indicates if this is a sync MDN. In this case there is no additional
     * connection information as this came in on the back channel
     * of the outbound connection
     * 
     * @return the isSyncMDN
     */
    public boolean isSyncMDN() {
        return isSyncMDN;
    }

    /**
     * Indicates if this is a sync MDN. In this case there is no additional
     * connection information as this came in on the back channel
     * of the outbound connection
     * 
     * @param isSyncMDN the isSyncMDN to set
     */
    public void setSyncMDN(boolean isSyncMDN) {
        this.isSyncMDN = isSyncMDN;
    }

    /**
     * @return the targetUserId
     */
    public int getTargetUserId() {
        return targetUserId;
    }

    /**
     * @param targetUserId the targetUserId to set (0 = admin/system, >0 = specific user)
     */
    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

}
