/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import java.util.List;

/**
 * Interface for SendOrder queue implementations.
 * Provides abstraction for different queue strategies (PERSISTENT vs IN_MEMORY).
 *
 * Thread Safety: All implementations must be thread-safe for concurrent access
 * from multiple threads.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public interface SendOrderQueueInterface {

    /**
     * Add a new send order to the queue.
     *
     * @param request the send order request containing all necessary information
     * @return order ID for tracking
     * @throws QueueFullException if queue has reached capacity (IN_MEMORY strategy only)
     */
    int enqueue(SendOrderRequest request) throws QueueFullException;

    /**
     * Retrieve next available orders for processing.
     * Orders are transitioned to PROCESSING state and will not be returned
     * by subsequent calls until completed or requeued.
     *
     * @param maxCount maximum number of orders to retrieve
     * @return list of orders ready to process (may be empty)
     */
    List<SendOrderItem> dequeueAvailable(int maxCount);

    /**
     * Mark order as successfully sent and remove from queue.
     *
     * @param orderId the order ID returned by enqueue
     */
    void markCompleted(int orderId);

    /**
     * Requeue order for retry after failure.
     * Order is transitioned back to WAITING state with delayed execution time.
     *
     * @param orderId the order ID returned by enqueue
     * @param delayMs milliseconds to wait before retry
     */
    void requeueForRetry(int orderId, long delayMs);

    /**
     * Update cached AS2Message for an order (IN_MEMORY strategy only).
     * After first send attempt, the built message is cached to ensure
     * retries use the same message ID and encrypted bytes.
     *
     * @param orderId the order ID
     * @param message the AS2Message to cache
     */
    void updateCachedMessage(int orderId, de.mendelson.comm.as2.message.AS2Message message);

    /**
     * Called on server startup for crash recovery.
     * Implementations should:
     * - Restore queue state from persistent storage
     * - Reset PROCESSING orders to WAITING state
     */
    void restore();

    /**
     * Called on server shutdown for graceful cleanup.
     * Implementations should:
     * - Persist current queue state
     * - Release resources (threads, file handles, etc.)
     */
    void shutdown();
}
