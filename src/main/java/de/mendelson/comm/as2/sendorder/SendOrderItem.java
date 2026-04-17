/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.partner.Partner;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Union type returned by dequeue operation.
 * Contains EITHER:
 * - Pre-built message (PERSISTENT strategy): AS2Message + Partner objects
 * - Lightweight metadata (IN_MEMORY strategy): file paths + partner DB IDs
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class SendOrderItem {

    private final int orderId;
    private final int dbId; // Database ID for PERSISTENT strategy (-1 for IN_MEMORY)

    // PERSISTENT strategy fields (pre-built message)
    private AS2Message message;
    private Partner sender;
    private Partner receiver;

    // IN_MEMORY strategy fields (lightweight metadata)
    private int senderDBId = -1;
    private int receiverDBId = -1;
    private Path[] files;
    private String[] originalFilenames;
    private String userdefinedId;
    private String subject;
    private String[] payloadContentTypes;
    private int userId = -1;

    // Common fields
    private AtomicInteger retryCount = new AtomicInteger(0);

    // Cached message for IN_MEMORY strategy retries
    // After first send attempt, the built message is cached here to ensure
    // retries use the same message ID and encrypted bytes (AS2 protocol requirement)
    private AS2Message cachedMessage;

    /**
     * Constructor for PERSISTENT strategy (pre-built message)
     */
    public SendOrderItem(int orderId, int dbId, AS2Message message, Partner sender, Partner receiver) {
        this.orderId = orderId;
        this.dbId = dbId;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Constructor for IN_MEMORY strategy (lightweight metadata)
     */
    public SendOrderItem(int orderId, int senderDBId, int receiverDBId, Path[] files,
                         String[] originalFilenames, String userdefinedId, String subject,
                         String[] payloadContentTypes, int userId) {
        this.orderId = orderId;
        this.dbId = -1; // No database ID for in-memory orders
        this.senderDBId = senderDBId;
        this.receiverDBId = receiverDBId;
        this.files = files;
        this.originalFilenames = originalFilenames;
        this.userdefinedId = userdefinedId;
        this.subject = subject;
        this.payloadContentTypes = payloadContentTypes;
        this.userId = userId;
    }

    /**
     * Check if this item contains a pre-built message (PERSISTENT strategy)
     * or lightweight metadata (IN_MEMORY strategy).
     *
     * @return true if message is pre-built, false if needs on-demand assembly
     */
    public boolean hasPrebuiltMessage() {
        return message != null;
    }

    /**
     * Increment retry count and return new value
     */
    public int incRetryCount() {
        return retryCount.incrementAndGet();
    }

    public int getRetryCount() {
        return retryCount.get();
    }

    public void setRetryCount(int count) {
        this.retryCount.set(count);
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public int getDbId() {
        return dbId;
    }

    public AS2Message getMessage() {
        return message;
    }

    public Partner getSender() {
        return sender;
    }

    public Partner getReceiver() {
        return receiver;
    }

    public int getSenderDBId() {
        return senderDBId;
    }

    public int getReceiverDBId() {
        return receiverDBId;
    }

    public Path[] getFiles() {
        return files;
    }

    public String[] getOriginalFilenames() {
        return originalFilenames;
    }

    public String getUserdefinedId() {
        return userdefinedId;
    }

    public String getSubject() {
        return subject;
    }

    public String[] getPayloadContentTypes() {
        return payloadContentTypes;
    }

    public int getUserId() {
        return userId;
    }

    // Setters for PERSISTENT strategy to populate partner DBIds after deserialization
    public void setSenderDBId(int senderDBId) {
        this.senderDBId = senderDBId;
    }

    public void setReceiverDBId(int receiverDBId) {
        this.receiverDBId = receiverDBId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public AS2Message getCachedMessage() {
        return cachedMessage;
    }

    public void setCachedMessage(AS2Message cachedMessage) {
        this.cachedMessage = cachedMessage;
    }
}
