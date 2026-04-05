package de.mendelson.util.systemevents.notification;

import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.systemevents.SystemEvent;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks the database and switches certificates if there are two available for
 * a partner
 *
 * @author S.Heller
 * @version $Revision: 26 $
 */
public abstract class SystemEventNotificationController {

    /**
     * Wait time, this is how long this thread waits
     */
    private final static long WAIT_TIME_IN_MS = TimeUnit.MINUTES.toMillis(1);
    private final static DateTimeFormatter DAILY_SUBDIR_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("notification-control"));
    /**
     * Logger to log information to
     */
    private final Logger logger;
    private final NotificationCheckThread notificationCheckThread;

    /**
     * Controller that checks notifications and sends them out if required
     *
     */
    protected SystemEventNotificationController(Logger logger) {
        this.logger = logger;
        this.notificationCheckThread = new NotificationCheckThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.notificationCheckThread,
                TimeUnit.SECONDS.toMillis(5), WAIT_TIME_IN_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets all notifications found in a time frame and sends out notifications
     * if required
     */
    private void checkForNotificationToSend() throws Throwable {
        Path storageDir = Paths.get(this.getStorageDir(),
                LocalDateTime.now().format(DAILY_SUBDIR_FORMAT),
                "events");
        DateFormat eventFiledateFormat = new SimpleDateFormat("HH-mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -2 * ((int) WAIT_TIME_IN_MS));
        //there is no event for the current day - the event subdirectory does not exist
        if (!Files.exists(storageDir)) {
            return;
        }
        String startString = eventFiledateFormat.format(calendar.getTime());
        List<SystemEvent> foundSystemEvents = new ArrayList<SystemEvent>();
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) {
                return (entry.getFileName().toString().startsWith(startString));
            }
        };
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(storageDir, filter)) {
            for (Path foundNotificationFile : dirStream) {
                try {
                    SystemEvent event = SystemEvent.parse(foundNotificationFile);
                    foundSystemEvents.add(event);
                } catch (Throwable e) {
                    //ignore - it is no system event that has been found
                    e.printStackTrace();
                }
            }
        }
        if (!foundSystemEvents.isEmpty()) {
            Comparator comparator = new Comparator<SystemEvent>() {
                @Override
                public int compare(SystemEvent evt1, SystemEvent evt2) {
                    if (evt1.getTimestamp() == evt2.getTimestamp()) {
                        return (0);
                    }
                    if (evt1.getTimestamp() > evt2.getTimestamp()) {
                        return (1);
                    } else {
                        return (-1);
                    }

                }
            };
            foundSystemEvents.sort(comparator);
            List<SystemEvent> systemEventsToNotifyUserOf = this.filterEventsForNotification(foundSystemEvents);
            if (!systemEventsToNotifyUserOf.isEmpty()) {
                this.sendNotification(systemEventsToNotifyUserOf);
            }
        }
    }

    /**
     * Check if and a notification should be send - depends on the
     * implementation
     */
    public abstract List<SystemEvent> filterEventsForNotification(List<SystemEvent> foundSystemEvents);

    public abstract void sendNotification(List<SystemEvent> systemEventsToNotifyUserOf) throws Throwable;

    /**
     * Returns the product specific notification dir
     */
    public abstract String getStorageDir();

    public class NotificationCheckThread implements Runnable {

        @Override
        public void run() {
            try {
                checkForNotificationToSend();
            } catch (Throwable e) {
                e.printStackTrace();
                logger.severe("NotificationController: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
            }
        }
    }

}
