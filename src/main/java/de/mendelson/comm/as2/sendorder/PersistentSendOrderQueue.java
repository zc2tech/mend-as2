/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import de.mendelson.util.database.IDBDriverManager;

import java.util.ArrayList;
import java.util.List;

/**
 * PERSISTENT queue strategy implementation.
 * Wraps existing SendOrderAccessDB to provide SendOrderQueueInterface implementation.
 * Preserves 100% of existing behavior: pre-built messages stored in database.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class PersistentSendOrderQueue implements SendOrderQueueInterface {

    private final SendOrderAccessDB accessDB;

    public PersistentSendOrderQueue(IDBDriverManager dbDriverManager) {
        this.accessDB = new SendOrderAccessDB(dbDriverManager);
    }

    /**
     * Enqueue a send order.
     * Note: For PERSISTENT strategy, the caller (SendOrderSender) is responsible
     * for pre-building the AS2Message before calling this method.
     *
     * @param request the send order request (note: message must be pre-built by caller)
     * @return order database ID
     * @throws QueueFullException never thrown for PERSISTENT strategy (unlimited queue)
     */
    @Override
    public int enqueue(SendOrderRequest request) throws QueueFullException {
        // For PERSISTENT strategy, this method is called with a pre-built SendOrder
        // that already contains the AS2Message. This is handled differently - see usage
        // in SendOrderSender where it creates SendOrder with message first.
        throw new UnsupportedOperationException(
            "PERSISTENT strategy requires calling enqueueWithMessage() instead");
    }

    /**
     * Enqueue a send order with pre-built message (PERSISTENT strategy specific).
     * This is the actual method used by SendOrderSender for PERSISTENT strategy.
     *
     * @param order the complete send order with pre-built AS2Message
     * @return order database ID
     */
    public int enqueueWithMessage(SendOrder order) {
        this.accessDB.add(order);
        return order.getDbId();
    }

    @Override
    public List<SendOrderItem> dequeueAvailable(int maxCount) {
        List<SendOrder> orders = accessDB.getNext(maxCount);
        List<SendOrderItem> items = new ArrayList<>();

        for (SendOrder order : orders) {
            // Convert SendOrder to SendOrderItem with pre-built message
            // Note: sender/receiver in SendOrder are deprecated transient fields (null after deserialization)
            // SendOrderReceiver will reload partners from senderDBId/receiverDBId
            SendOrderItem item = new SendOrderItem(
                order.getDbId(),  // Use DB ID as order ID
                order.getDbId(),  // DB ID for PERSISTENT strategy
                order.getMessage(),
                null,  // Sender will be reloaded in SendOrderReceiver
                null   // Receiver will be reloaded in SendOrderReceiver
            );
            // Store senderDBId and receiverDBId so receiver can reload partners
            item.setSenderDBId(order.getSenderDBId());
            item.setReceiverDBId(order.getReceiverDBId());
            item.setUserId(order.getUserId());

            items.add(item);
        }

        return items;
    }

    @Override
    public void markCompleted(int orderId) {
        accessDB.delete(orderId);
    }

    @Override
    public void requeueForRetry(int orderId, long delayMs) {
        // For PERSISTENT strategy, we need to retrieve the order first
        // Since SendOrderAccessDB doesn't have a get() method, we'll need to
        // handle this differently. The order is already in PROCESSING state.
        // We'll create a simplified version that updates just the fields we need.

        // This is a limitation - we can't easily retrieve the order to reschedule it.
        // For now, mark as completed (delete) and let the caller handle retry differently.
        // This will be addressed when we refactor SendOrderReceiver to pass the order object.
        throw new UnsupportedOperationException(
            "requeueForRetry for PERSISTENT strategy requires passing full SendOrder object");
    }

    /**
     * Reschedule with full SendOrder object (PERSISTENT strategy specific).
     */
    public void rescheduleOrder(SendOrder order, long nextExecutionTime) {
        accessDB.rescheduleOrder(order, nextExecutionTime);
    }

    @Override
    public void restore() {
        // Reset all PROCESSING orders to WAITING (crash recovery)
        accessDB.resetAllToWaiting();
    }

    @Override
    public void shutdown() {
        // No-op: database persists automatically
    }

    @Override
    public void updateCachedMessage(int orderId, de.mendelson.comm.as2.message.AS2Message message) {
        // No-op: PERSISTENT strategy pre-builds messages, no caching needed
    }
}
