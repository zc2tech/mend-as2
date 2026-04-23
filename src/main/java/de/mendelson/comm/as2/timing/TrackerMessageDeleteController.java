package de.mendelson.comm.as2.timing;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.tracker.TrackerMessageAccessDB;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Controls the timed deletion of tracker messages from the database
 *
 * @author Julian Xu
 */
public class TrackerMessageDeleteController {

    /**
     * Logger to log information to
     */
    private final static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final PreferencesAS2 preferences;
    private final TrackerMessageDeleteThread deleteThread;
    private final MecResourceBundle rb;
    private final IDBDriverManager dbDriverManager;
    private final TrackerMessageAccessDB trackerMessageAccess;

    public TrackerMessageDeleteController(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.preferences = new PreferencesAS2(dbDriverManager);
        this.trackerMessageAccess = new TrackerMessageAccessDB(this.dbDriverManager, this.preferences);
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleTrackerMessageDeleteController.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.deleteThread = new TrackerMessageDeleteThread();
    }

    /**
     * Starts the embedded task that guards the tracker messages
     */
    public void startAutoDeleteControl() {
        TimingScheduledThreadPool.scheduleWithFixedDelay(this.deleteThread, 1, 1, TimeUnit.MINUTES);
    }

    public class TrackerMessageDeleteThread implements Runnable {

        public TrackerMessageDeleteThread() {
        }

        @Override
        public void run() {
            try {
                if (preferences.getBoolean(PreferencesAS2.AUTO_TRACKER_DELETE)) {
                    try {
                        long olderThanDays = preferences.getInt(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN);
                        long olderThanMillis = System.currentTimeMillis()
                                - TimeUnit.DAYS.toMillis(olderThanDays);
                        Date cutoffDate = new Date(olderThanMillis);

                        int deletedCount = trackerMessageAccess.deleteTrackerMessagesOlderThan(cutoffDate);

                        if (deletedCount > 0) {
                            logger.fine(rb.getResourceString("autodelete",
                                    new Object[]{
                                        String.valueOf(deletedCount),
                                        String.valueOf(olderThanDays)
                                    }));
                            this.fireSystemEventTrackerMessagesDeleted(cutoffDate, deletedCount);
                        }
                    } catch (Throwable e) {
                        SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                    }
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
            }
        }

        /**
         * Fire a system event that the system maintenance process has deleted
         * tracker messages
         */
        private void fireSystemEventTrackerMessagesDeleted(Date cutoffDate, int deletedCount) {
            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_TRACKER_MESSAGE_DELETE);
            event.setSubject(rb.getResourceString("tracker.deleted.system"));
            StringBuilder builder = new StringBuilder();
            builder.append(rb.getResourceString("tracker.delete.setting.olderthan", dateFormat.format(cutoffDate)));
            builder.append(System.lineSeparator());
            builder.append(rb.getResourceString("tracker.delete.count", String.valueOf(deletedCount)));
            event.setBody(builder.toString());
            SystemEventManagerImplAS2.instance().newEvent(event);
        }

    }
}
