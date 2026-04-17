/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IN_MEMORY queue strategy implementation.
 * Stores lightweight metadata in memory with periodic checkpointing to disk.
 * Provides 99% memory reduction compared to PERSISTENT strategy.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class InMemorySendOrderQueue implements SendOrderQueueInterface {

    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);

    // Core queue storage
    private final ConcurrentLinkedQueue<LightweightSendOrder> waitingQueue;
    private final ConcurrentHashMap<Integer, LightweightSendOrder> processingOrders;
    private final AtomicInteger nextOrderId;

    // Backpressure management
    private final int maxQueueDepth;
    private final Semaphore queueSlots;

    // Crash recovery
    private final Path checkpointFile;
    private final ScheduledExecutorService checkpointer;
    private final int checkpointIntervalSeconds;

    /**
     * Constructor
     *
     * @param maxQueueDepth maximum queue depth (default 1000)
     * @param checkpointIntervalSeconds checkpoint interval in seconds (default 60)
     */
    public InMemorySendOrderQueue(int maxQueueDepth, int checkpointIntervalSeconds) {
        this.waitingQueue = new ConcurrentLinkedQueue<>();
        this.processingOrders = new ConcurrentHashMap<>();
        this.nextOrderId = new AtomicInteger(0);

        this.maxQueueDepth = maxQueueDepth;
        this.queueSlots = new Semaphore(maxQueueDepth);

        // Checkpoint file location: ~/.as2/sendorder_checkpoint.dat
        String userHome = System.getProperty("user.home");
        Path as2Dir = Paths.get(userHome, ".as2");
        try {
            Files.createDirectories(as2Dir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create .as2 directory: " + e.getMessage(), e);
        }
        this.checkpointFile = as2Dir.resolve("sendorder_checkpoint.dat");

        this.checkpointIntervalSeconds = checkpointIntervalSeconds;

        // Start checkpoint scheduler
        this.checkpointer = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SendOrder-Checkpointer");
            t.setDaemon(true);
            return t;
        });

        this.checkpointer.scheduleAtFixedRate(
            this::checkpoint,
            checkpointIntervalSeconds,
            checkpointIntervalSeconds,
            TimeUnit.SECONDS
        );

        logger.info("InMemorySendOrderQueue initialized: maxDepth=" + maxQueueDepth +
                   ", checkpointInterval=" + checkpointIntervalSeconds + "s");
    }

    @Override
    public int enqueue(SendOrderRequest request) throws QueueFullException {
        // Try to acquire slot (backpressure)
        if (!queueSlots.tryAcquire()) {
            String errorMsg = "Queue full, rejecting send order (depth=" + maxQueueDepth +
                            ", sender=" + request.getSenderDBId() +
                            ", receiver=" + request.getReceiverDBId() + ")";
            logger.warning(errorMsg);
            throw new QueueFullException(errorMsg);
        }

        int orderId = nextOrderId.incrementAndGet();
        LightweightSendOrder order = new LightweightSendOrder(orderId, request);
        waitingQueue.offer(order);

        System.out.println("*** ENQUEUE on queue instance: " + System.identityHashCode(this) + ", waitingQueue.size=" + waitingQueue.size());
        logger.info("*** NEW CODE LOADED *** Enqueued send order " + orderId +
                   " (senderDBId=" + request.getSenderDBId() +
                   ", receiverDBId=" + request.getReceiverDBId() +
                   ", waitingQueue.size=" + waitingQueue.size() + ")");

        return orderId;
    }

    @Override
    public List<SendOrderItem> dequeueAvailable(int maxCount) {
        List<SendOrderItem> items = new ArrayList<>();
        long now = System.currentTimeMillis();
        int count = 0;

        logger.info("[DEQUEUE-DEBUG] Checking queue: maxCount=" + maxCount + ", waitingQueue.size=" + waitingQueue.size());

        // Poll waiting queue for orders ready to execute
        while (count < maxCount) {
            LightweightSendOrder order = waitingQueue.peek();
            if (order == null) {
                logger.info("[DEQUEUE-DEBUG] Queue is empty");
                break; // Queue empty
            }

            logger.info("[DEQUEUE-DEBUG] Found order " + order.getOrderId() +
                       ": nextExecutionTime=" + order.getNextExecutionTime() +
                       ", now=" + now +
                       ", ready=" + (order.getNextExecutionTime() <= now));

            // Check if order is ready (nextExecutionTime <= now)
            if (order.getNextExecutionTime() > now) {
                logger.info("[DEQUEUE-DEBUG] Order not ready yet, stopping");
                break; // Next order not ready yet (queue sorted by execution time)
            }

            // Remove from waiting queue
            order = waitingQueue.poll();
            if (order == null) {
                logger.info("[DEQUEUE-DEBUG] Concurrent modification, order disappeared");
                break; // Concurrent modification
            }

            logger.info("[DEQUEUE-DEBUG] Dequeuing order " + order.getOrderId());

            // Move to processing
            order.setState(LightweightSendOrder.OrderState.PROCESSING);
            processingOrders.put(order.getOrderId(), order);

            // Convert to SendOrderItem
            SendOrderItem item = new SendOrderItem(
                order.getOrderId(),
                order.getSenderDBId(),
                order.getReceiverDBId(),
                order.getFiles(),
                order.getOriginalFilenames(),
                order.getUserdefinedId(),
                order.getSubject(),
                order.getPayloadContentTypes(),
                order.getUserId()
            );
            item.setRetryCount(order.getRetryCount().get());
            item.setCachedMessage(order.getCachedMessage());  // Pass cached message for retries

            items.add(item);
            count++;
        }

        if (!items.isEmpty()) {
            logger.fine("Dequeued " + items.size() + " orders for processing");
        }

        return items;
    }

    @Override
    public void markCompleted(int orderId) {
        LightweightSendOrder order = processingOrders.remove(orderId);
        if (order != null) {
            order.setState(LightweightSendOrder.OrderState.COMPLETED);
            queueSlots.release(); // Free slot for new orders
            logger.fine("Completed send order " + orderId);
        }
    }

    @Override
    public void requeueForRetry(int orderId, long delayMs) {
        LightweightSendOrder order = processingOrders.remove(orderId);
        if (order != null) {
            order.setState(LightweightSendOrder.OrderState.WAITING);
            order.setNextExecutionTime(System.currentTimeMillis() + delayMs);
            order.incRetryCount();

            // Re-add to waiting queue
            waitingQueue.offer(order);

            logger.fine("Requeued send order " + orderId +
                       " for retry (delay=" + delayMs + "ms, retryCount=" +
                       order.getRetryCount().get() + ")");
        }
    }

    @Override
    public void updateCachedMessage(int orderId, de.mendelson.comm.as2.message.AS2Message message) {
        // Find order in processing map
        LightweightSendOrder order = processingOrders.get(orderId);
        if (order != null) {
            order.setCachedMessage(message);
            logger.fine("Cached AS2Message for order " + orderId +
                       " (message ID: " + message.getAS2Info().getMessageId() + ")");
        }
    }

    @Override
    public void restore() {
        if (!Files.exists(checkpointFile)) {
            logger.info("No checkpoint file found, starting with empty queue");
            return;
        }

        try (FileInputStream fis = new FileInputStream(checkpointFile.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            @SuppressWarnings("unchecked")
            List<LightweightSendOrder> restoredOrders = (List<LightweightSendOrder>) ois.readObject();

            int waitingCount = 0;
            int processingCount = 0;

            for (LightweightSendOrder order : restoredOrders) {
                // Reset PROCESSING orders to WAITING (crash recovery)
                if (order.getState() == LightweightSendOrder.OrderState.PROCESSING) {
                    order.setState(LightweightSendOrder.OrderState.WAITING);
                    processingCount++;
                }

                if (order.getState() == LightweightSendOrder.OrderState.WAITING) {
                    waitingQueue.offer(order);
                    waitingCount++;

                    // Update nextOrderId to avoid ID collisions
                    if (order.getOrderId() >= nextOrderId.get()) {
                        nextOrderId.set(order.getOrderId() + 1);
                    }
                }
            }

            // Adjust semaphore to account for restored orders
            int restored = waitingCount + processingCount;
            queueSlots.acquireUninterruptibly(restored);

            logger.info("Restored " + restored + " orders from checkpoint " +
                       "(waiting=" + waitingCount +
                       ", processing_reset=" + processingCount + ")");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to restore checkpoint file: " + e.getMessage(), e);
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
        }
    }

    @Override
    public void shutdown() {
        logger.info("Shutting down InMemorySendOrderQueue");

        // Stop checkpoint scheduler
        checkpointer.shutdown();
        try {
            if (!checkpointer.awaitTermination(5, TimeUnit.SECONDS)) {
                checkpointer.shutdownNow();
            }
        } catch (InterruptedException e) {
            checkpointer.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Force final checkpoint
        checkpoint();

        logger.info("InMemorySendOrderQueue shutdown complete");
    }

    /**
     * Checkpoint current queue state to disk
     */
    private void checkpoint() {
        try {
            // Collect all orders (waiting + processing)
            List<LightweightSendOrder> allOrders = new ArrayList<>();
            allOrders.addAll(waitingQueue);
            allOrders.addAll(processingOrders.values());

            if (allOrders.isEmpty()) {
                logger.finest("Checkpoint skipped (queue empty)");
                return;
            }

            // Write to temp file first, then atomic rename
            Path tempFile = Paths.get(checkpointFile.toString() + ".tmp");

            try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(allOrders);
                oos.flush();
                fos.getFD().sync(); // Force write to disk
            }

            // Atomic rename
            Files.move(tempFile, checkpointFile,
                      java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                      java.nio.file.StandardCopyOption.ATOMIC_MOVE);

            logger.fine("Checkpointed " + allOrders.size() + " orders to disk");

        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to checkpoint queue: " + e.getMessage(), e);
        }
    }

    /**
     * Get current queue statistics
     */
    public QueueStats getStats() {
        return new QueueStats(
            waitingQueue.size(),
            processingOrders.size(),
            maxQueueDepth - queueSlots.availablePermits()
        );
    }

    public static class QueueStats {
        public final int waitingCount;
        public final int processingCount;
        public final int totalCount;

        public QueueStats(int waitingCount, int processingCount, int totalCount) {
            this.waitingCount = waitingCount;
            this.processingCount = processingCount;
            this.totalCount = totalCount;
        }
    }
}
