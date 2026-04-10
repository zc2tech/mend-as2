package org.apache.mina.core.session;

/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Minimal stub for IoSession compatibility.
 * Mina networking removed.
 *
 * @author Julian Xu
 * @version 1.0
 */
public interface IoSession {
    void write(Object message);
    Object getAttribute(String key);
    void setAttribute(String key, Object value);
    Object getRemoteAddress();
    Object getLocalAddress();
    void closeNow();
    void closeOnFlush();
    long getId();
}
