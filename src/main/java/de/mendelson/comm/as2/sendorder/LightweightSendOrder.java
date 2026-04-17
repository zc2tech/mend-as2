/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight send order for IN_MEMORY strategy.
 * Stores only metadata (~1KB), not pre-built AS2Message (~100KB).
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class LightweightSendOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum OrderState {
        WAITING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    private int orderId;
    private int senderDBId;
    private int receiverDBId;
    private Path[] files;
    private String[] originalFilenames;
    private String userdefinedId;
    private String subject;
    private String[] payloadContentTypes;
    private int userId = -1;

    private long scheduledTime;
    private long nextExecutionTime;
    private transient AtomicInteger retryCount;
    private volatile OrderState state;

    public LightweightSendOrder() {
        this.retryCount = new AtomicInteger(0);
        this.state = OrderState.WAITING;
        this.scheduledTime = System.currentTimeMillis();
        this.nextExecutionTime = System.currentTimeMillis();
    }

    public LightweightSendOrder(int orderId, SendOrderRequest request) {
        this();
        this.orderId = orderId;
        this.senderDBId = request.getSenderDBId();
        this.receiverDBId = request.getReceiverDBId();
        this.files = request.getFiles();
        this.originalFilenames = request.getOriginalFilenames();
        this.userdefinedId = request.getUserdefinedId();
        this.subject = request.getSubject();
        this.payloadContentTypes = request.getPayloadContentTypes();
        this.userId = request.getUserId();
    }

    /**
     * Custom serialization to handle transient AtomicInteger
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Save the retry count value
        out.writeInt(retryCount != null ? retryCount.get() : 0);
    }

    /**
     * Custom deserialization to restore transient AtomicInteger
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Restore the retry count value
        int retryCountValue = in.readInt();
        this.retryCount = new AtomicInteger(retryCountValue);
    }

    /**
     * Increment retry count and return new value
     */
    public int incRetryCount() {
        return this.retryCount.incrementAndGet();
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSenderDBId() {
        return senderDBId;
    }

    public void setSenderDBId(int senderDBId) {
        this.senderDBId = senderDBId;
    }

    public int getReceiverDBId() {
        return receiverDBId;
    }

    public void setReceiverDBId(int receiverDBId) {
        this.receiverDBId = receiverDBId;
    }

    public Path[] getFiles() {
        return files;
    }

    public void setFiles(Path[] files) {
        this.files = files;
    }

    public String[] getOriginalFilenames() {
        return originalFilenames;
    }

    public void setOriginalFilenames(String[] originalFilenames) {
        this.originalFilenames = originalFilenames;
    }

    public String getUserdefinedId() {
        return userdefinedId;
    }

    public void setUserdefinedId(String userdefinedId) {
        this.userdefinedId = userdefinedId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getPayloadContentTypes() {
        return payloadContentTypes;
    }

    public void setPayloadContentTypes(String[] payloadContentTypes) {
        this.payloadContentTypes = payloadContentTypes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public AtomicInteger getRetryCount() {
        return retryCount;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
