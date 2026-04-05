package de.mendelson.util.ha;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores information about this any server instance found in the HA
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ServerInstanceHA implements Serializable {

    private static final long serialVersionUID = 1L;

    public ServerInstanceHA() {
    }

    /**
     * @return the uniqueDd
     */
    public String getUniqueId() {
        return "";
    }

    /**
     * @param uniqueDd the uniqueDd to set
     */
    public void setUniqueId(String a) {        
    }

    /**
     * @return the ip
     */
    public String getLocalIP() {
        return "";
    }

    /**
     * @param ip the ip to set
     */
    public void setLocalIP(String a) {        
    }

    /**
     * @return the host
     */
    public String getHost() {
        return "";
    }

    /**
     * @param host the host to set
     */
    public void setHost(String a) {        
    }

    /**
     * @return the numberOfClients
     */
    public int getNumberOfClients() {
        return 0;
    }

    /**
     * @param numberOfClients the numberOfClients to set
     */
    public void setNumberOfClients(int a) {        
    }

    /**
     * @return the os
     */
    public String getOS() {
        return "";
    }

    /**
     * @param os the os to set
     */
    public void setOS(String a) {       
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return "";
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String a) {        
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return 0;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long a) {        
    }

    /**
     * @return the lastSeenTime
     */
    public long getLastSeenTime() {
        return 0;
    }

    /**
     * @param lastSeenTime the lastSeenTime to set
     */
    public void setLastSeenTime(long a) {        
    }

    /**
     * @return the publicIP
     */
    public String getPublicIP() {
        return "";
    }

    /**
     * @param publicIP the publicIP to set
     */
    public void setPublicIP(String a) {        
    }

    /**
     * @return the cloudInstanceId
     */
    public String getCloudInstanceId() {
        return "";
    }

    /**
     * @param cloudInstanceId the cloudInstanceId to set
     */
    public void setCloudInstanceId(String a) {        
    }

}
