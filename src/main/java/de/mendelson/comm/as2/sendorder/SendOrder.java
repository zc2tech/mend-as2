//$Header: /as2/de/mendelson/comm/as2/sendorder/SendOrder.java 8     14/01/25 14:20 Heller $
package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.partner.Partner;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Send order that will be enqueued into the as2 server message queue
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class SendOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int STATE_WAITING = 0;
    public static final int STATE_PROCESSING = 1;
    
    private Partner receiver;
    private AS2Message message;
    private Partner sender;
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private int dbId = -1;
    private String userdefinedId = null;
    private int userId = -1; // WebUI user ID for HTTP auth preference resolution

    public Partner getReceiver() {
        return receiver;
    }

    public SendOrder setReceiver(Partner receiver) {
        this.receiver = receiver;
        return( this );
    }

    public AS2Message getMessage() {
        return message;
    }

    public SendOrder setMessage(AS2Message message) {        
        this.message = message;
        return( this );
    }

    public Partner getSender() {
        return sender;
    }

    public SendOrder setSender(Partner sender) {
        this.sender = sender;
        return( this );
    }

    public int incRetryCount() {
        return( this.retryCount.incrementAndGet());
    }

    /**
     * @return the dbId
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * @param dbId the dbId to set
     */
    public SendOrder setDbId(int dbId) {
        this.dbId = dbId;
        return( this );
    }

    /**
     * @return the userdefinedId
     */
    public String getUserdefinedId() {
        return userdefinedId;
    }

    /**
     * @param userdefinedId the userdefinedId to set
     */
    public SendOrder setUserdefinedId(String userdefinedId) {
        this.userdefinedId = userdefinedId;
        return( this );
    }

    /**
     * @return the userId (WebUI user ID for HTTP auth preference resolution)
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public SendOrder setUserId(int userId) {
        this.userId = userId;
        return( this );
    }

}
