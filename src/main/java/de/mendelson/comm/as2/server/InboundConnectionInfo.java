//$Header: /mec_as2/de/mendelson/comm/as2/server/InboundConnectionInfo.java 2     21/03/25 9:12 Heller $
package de.mendelson.comm.as2.server;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Container that stores information about the inbound connection the data is received from
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class InboundConnectionInfo {

    private String remoteAddress = null;
    private boolean usesTLS = false;
    private int localPort = -1;
    private String tlsProtocol = null;
    private String cipherSuite = null;
    /**Indicates if this is a sync MDN. In this case there is no additional connection information as this came in on the back channel
     * of the outbound connection. Means in case of sync MDN this is just an empty object without any additional information
     */
    private boolean isSyncMDN = false;
    
    
    public InboundConnectionInfo() {

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
     * @return the usesTLS
     */
    public boolean getUsesTLS() {
        return usesTLS;
    }

    /**
     * @param usesTLS the usesTLS to set
     */
    public void setUsesTLS(boolean usesTLS) {
        this.usesTLS = usesTLS;
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
     * Indicates if this is a sync MDN. In this case there is no additional connection information as this came in on the back channel
     * of the outbound connection
     * @return the isSyncMDN
     */
    public boolean isSyncMDN() {
        return isSyncMDN;
    }

    /**
     * Indicates if this is a sync MDN. In this case there is no additional connection information as this came in on the back channel
     * of the outbound connection
     * @param isSyncMDN the isSyncMDN to set
     */
    public void setSyncMDN(boolean isSyncMDN) {
        this.isSyncMDN = isSyncMDN;
    }
}