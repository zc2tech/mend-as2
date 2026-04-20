/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.message.AS2Message;

/**
 * Result object for SendOrderSender.send() operations.
 * Replaces the confusing null-means-success pattern with explicit success/failure states.
 *
 * @author Julian Xu
 */
public class SendResult {
    private final boolean success;
    private final AS2Message message;  // null for IN_MEMORY strategy
    private final int orderId;         // for IN_MEMORY strategy
    private final String errorMessage;

    /**
     * Private constructor - use static factory methods instead
     */
    private SendResult(boolean success, AS2Message message, int orderId, String errorMessage) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
        this.errorMessage = errorMessage;
    }

    /**
     * Create success result for PERSISTENT strategy (message pre-built)
     */
    public static SendResult successWithMessage(AS2Message message) {
        return new SendResult(true, message, -1, null);
    }

    /**
     * Create success result for IN_MEMORY strategy (message queued, built later)
     */
    public static SendResult successQueued(int orderId) {
        return new SendResult(true, null, orderId, null);
    }

    /**
     * Create failure result
     */
    public static SendResult failure(String errorMessage) {
        return new SendResult(false, null, -1, errorMessage);
    }

    /**
     * @return true if send order was successfully queued or sent
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return true if this is a failure result
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * @return The AS2Message (PERSISTENT strategy only), or null for IN_MEMORY strategy
     */
    public AS2Message getMessage() {
        return message;
    }

    /**
     * @return true if message is available (PERSISTENT strategy)
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * @return The order ID (IN_MEMORY strategy only), or -1 for PERSISTENT strategy
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @return Error message if failure, null if success
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        if (success) {
            if (message != null) {
                return "SendResult{success, messageId=" + message.getAS2Info().getMessageId() + "}";
            } else {
                return "SendResult{success, orderId=" + orderId + " (queued)}";
            }
        } else {
            return "SendResult{failure, error=" + errorMessage + "}";
        }
    }
}
