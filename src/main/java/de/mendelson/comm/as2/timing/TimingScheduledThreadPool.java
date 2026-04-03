//$Header: /as2/de/mendelson/comm/as2/timing/TimingScheduledThreadPool.java 4     11/02/25 13:39 Heller $
package de.mendelson.comm.as2.timing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
 * Thread pool for all timing schedules that are not time critical, e.g. check for
 * certificate expire, delete old log files, etc
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class TimingScheduledThreadPool {

    // Java 17 compatible thread pool (virtual threads require Java 21+)
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(2);
    // Thread.ofVirtual().name("serverside-not-timecritical-", 0).factory()

    private TimingScheduledThreadPool(){
    }
    
    public static void scheduleWithFixedDelay(Runnable task, int startDelay, int executionDelay, TimeUnit timeunit){
        SCHEDULED_EXECUTOR.scheduleWithFixedDelay(task, startDelay, executionDelay, timeunit);
    }
    
}
