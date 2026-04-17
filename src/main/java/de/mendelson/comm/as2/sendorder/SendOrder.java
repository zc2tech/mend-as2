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

    // Store only partner DB IDs instead of full Partner objects for efficiency
    // Partners will be reloaded from database when processing the order
    private int receiverDBId = -1;
    private int senderDBId = -1;

    // Legacy fields - kept for backward compatibility with existing serialized SendOrders
    // These will be null for newly created SendOrders
    @Deprecated
    private transient Partner receiver;
    @Deprecated
    private transient Partner sender;

    private AS2Message message;
    private transient AtomicInteger retryCount;
    private int dbId = -1;
    private String userdefinedId = null;
    private int userId = -1; // WebUI user ID for HTTP auth preference resolution

    public SendOrder() {
        this.retryCount = new AtomicInteger(0);
    }

    /**
     * @deprecated Use getReceiverDBId() and reload from database instead
     */
    @Deprecated
    public Partner getReceiver() {
        return receiver;
    }

    /**
     * @deprecated Use setReceiverDBId() instead
     */
    @Deprecated
    public SendOrder setReceiver(Partner receiver) {
        this.receiver = receiver;
        if (receiver != null) {
            this.receiverDBId = receiver.getDBId();
        }
        return( this );
    }

    public int getReceiverDBId() {
        return receiverDBId;
    }

    public SendOrder setReceiverDBId(int receiverDBId) {
        this.receiverDBId = receiverDBId;
        return this;
    }

    public AS2Message getMessage() {
        return message;
    }

    public SendOrder setMessage(AS2Message message) {
        this.message = message;
        return( this );
    }

    /**
     * @deprecated Use getSenderDBId() and reload from database instead
     */
    @Deprecated
    public Partner getSender() {
        return sender;
    }

    /**
     * @deprecated Use setSenderDBId() instead
     */
    @Deprecated
    public SendOrder setSender(Partner sender) {
        this.sender = sender;
        if (sender != null) {
            this.senderDBId = sender.getDBId();
        }
        return( this );
    }

    public int getSenderDBId() {
        return senderDBId;
    }

    public SendOrder setSenderDBId(int senderDBId) {
        this.senderDBId = senderDBId;
        return this;
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

    /**
     * Custom serialization to handle transient AtomicInteger
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        // Save the retry count value
        out.writeInt(retryCount != null ? retryCount.get() : 0);
    }

    /**
     * Custom deserialization to restore transient AtomicInteger
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Restore the retry count value
        int retryCountValue = in.readInt();
        this.retryCount = new AtomicInteger(retryCountValue);
    }

}
