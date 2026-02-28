//$Header: /as2/de/mendelson/util/clientserver/messages/ClientServerMessage.java 12    24/11/23 11:42 Heller $
package de.mendelson.util.clientserver.messages;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Superclass of all messages for the client server protocol
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ClientServerMessage implements Serializable{
       
    private static final long serialVersionUID = 1L;
    private static final AtomicLong referenceIdCounter = new AtomicLong(0);
    private long referenceId = 0;
    private boolean _syncRequest = false;
    private final String pid;

    public ClientServerMessage(){
        this.referenceId = getNextReferenceId();
        this.pid = ManagementFactory.getRuntimeMXBean().getName();        
    }
    
    /**Returns the next unique reference id, thread safe*/
    private static long getNextReferenceId(){
        return( referenceIdCounter.addAndGet(1L));        
    }

    public Long getReferenceId(){
        return( Long.valueOf(this.referenceId));
    }
   
    /** Internal method, do NOT use it
     * @return the _syncRequest
     */
    public boolean _isSyncRequest() {
        return _syncRequest;
    }

    /** Internal method, do NOT use it
     * @param syncRequest the _syncRequest to set
     */
    public void _setSyncRequest(boolean syncRequest) {
        this._syncRequest = syncRequest;
    }

    /**
     * @param referenceId the referenceId to set
     */
    protected void _setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }
    
    /**
     * @return the pid
     */
    public String getPID() {
        return pid;
    }

}
