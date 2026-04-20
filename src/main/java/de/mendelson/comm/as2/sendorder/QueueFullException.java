/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

/**
 * Exception thrown when queue capacity is exceeded.
 * Only applies to IN_MEMORY strategy which has bounded queue depth.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class QueueFullException extends Exception {

    public QueueFullException(String message) {
        super(message);
    }

    public QueueFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
