package de.mendelson.comm.as2.server;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * In-memory event bus for client-server communication without Apache Mina.
 * Replaces Mina TCP socket broadcasts with direct method calls in same JVM.
 *
 * This provides zero-latency push notifications for SwingUI clients while
 * maintaining the existing message-based architecture.
 *
 * Thread-safe: Uses CopyOnWriteArrayList for listener management.
 *
 * @author Julian Xu
 * @version 1.0
 */
public class EventBus {
    private static EventBus instance;
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    /**
     * Listener interface for receiving server events.
     */
    public interface EventListener {
        /**
         * Called when a server event is published.
         *
         * @param message the event message (e.g., RefreshClientMessageOverviewList)
         */
        void onEvent(ClientServerMessage message);
    }

    private EventBus() {
        // Singleton - private constructor
    }

    /**
     * Get the singleton EventBus instance.
     *
     * @return the EventBus instance
     */
    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Subscribe to server events.
     * The listener will receive all published events until unsubscribed.
     *
     * @param listener the event listener to add
     */
    public void subscribe(EventListener listener) {
        listeners.add(listener);
        logger.info("EventBus: Client subscribed, total listeners: " + listeners.size());
    }

    /**
     * Unsubscribe from server events.
     *
     * @param listener the event listener to remove
     */
    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
        logger.info("EventBus: Client unsubscribed, total listeners: " + listeners.size());
    }

    /**
     * Publish an event to all subscribed listeners.
     * This replaces the Mina ClientServer.broadcastToClients() method.
     *
     * Exceptions from listeners are caught and logged to prevent one
     * listener from affecting others.
     *
     * @param message the event message to publish
     */
    public void publish(ClientServerMessage message) {
        logger.fine("EventBus: Publishing " + message.getClass().getSimpleName() +
                    " to " + listeners.size() + " listeners");
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(message);
            } catch (Exception e) {
                logger.warning("EventBus: Listener error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the current number of subscribed listeners.
     * Useful for monitoring and debugging.
     *
     * @return number of active listeners
     */
    public int getListenerCount() {
        return listeners.size();
    }
}
