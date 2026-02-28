//$Header: /as2/de/mendelson/util/log/DailySubdirFileLoggingHandler.java 22    20/02/25 13:42 Heller $
package de.mendelson.util.log;

import de.mendelson.util.systemevents.SystemEventManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handler to log logger data to a file in a daily subdirectory. A log dir and a
 * log file name could be passed to this class, the log will be written to
 * logDir/yyMMdd/logfilename Sample: logger.addHandler( new
 * DailySubdirFileLoggingHandler(Paths.get("mylogdir"), "mylogfile.log") );
 *
 * @author S.Heller
 * @version $Revision: 22 $
 */
public class DailySubdirFileLoggingHandler extends Handler {

    private boolean doneHeader;    
    private final Path logDir;
    private final String logfileName;
    private long maxLogFileSize = -1;
    private final SystemEventManager eventManager;

    public DailySubdirFileLoggingHandler(Path logDir, String logfileName, LogFormatter logFormatter,
            SystemEventManager eventManager) {
        this.logDir = logDir;
        this.logfileName = logfileName;
        this.eventManager = eventManager;
        this.setFormatter(logFormatter);
    }

    /**
     * Sets the size of a single log file in bytes
     */
    public void setMaxLogFileSize(long maxLogFileSize) {
        this.maxLogFileSize = maxLogFileSize;
    }

    /**
     * Set (or change) the character encoding used by this <tt>Handler</tt>.
     * <p>
     * The encoding should be set before any <tt>LogRecords</tt> are written to
     * the <tt>Handler</tt>.
     *
     * @param encoding The name of a supported character encoding. May be null,
     * to indicate the default platform encoding.
     * @exception SecurityException if a security manager exists and if the
     * caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception UnsupportedEncodingException if the named encoding is not
     * supported.
     */
    @Override
    public void setEncoding(String encoding)
            throws SecurityException, java.io.UnsupportedEncodingException {
        super.setEncoding(encoding);
    }

    /**
     * Format and publish a LogRecord.
     *
     * @param logRecord description of the log event
     */
    @Override
    public synchronized void publish(LogRecord logRecord) {
        if (!isLoggable(logRecord)) {
            return;
        }
        String msg;
        int rawMessageLength = 0;
        try {
            msg = this.getFormatter().format(logRecord);
            String rawMessage = this.getFormatter().formatMessage(logRecord);
            if (rawMessage != null) {
                rawMessageLength = rawMessage.length();
            }
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        try {
            if (!doneHeader) {
                this.logMessage(logRecord.getLevel(), this.getFormatter().getHead(this), rawMessageLength);
                doneHeader = true;
            }
            this.logMessage(logRecord.getLevel(), msg, rawMessageLength);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    /**
     * Check if this Handler would actually log a given LogRecord, depending of
     * the log level
     *
     * @param logRecord a LogRecord
     * @return true if the LogRecord would be logged.
     *
     */
    @Override
    public boolean isLoggable(LogRecord logRecord) {
        return super.isLoggable(logRecord);
    }

    /**
     * Flush any buffered messages.
     */
    @Override
    public synchronized void flush() {
    }

    /**
     * Just flushes the current message
     */
    @Override
    public synchronized void close() throws SecurityException {
        this.flush();
    }

    private Path generateFullLogDir() {
        //dont use DateTimeFormatter here - this class does not seem to like any Calendar references
        final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
        Path newLogDir = Path.of(this.logDir.toAbsolutePath().toString(),
                LOG_DATE_FORMAT.format(new Date()));
        try {
            if (!Files.exists(newLogDir)) {
                Files.createDirectories(newLogDir);
            }
        } catch (Throwable e) {
            this.eventManager.newEventExceptionInDirectoryCreation(
                    e, newLogDir.toAbsolutePath().toString());
        }
        return( newLogDir );
    }

    private Path generateNewLogFile(Path fullLogDir) {
        Path newLogFile = Paths.get(
                fullLogDir.toAbsolutePath().toString(),
                this.logfileName);
        if (this.maxLogFileSize == -1) {
            return (newLogFile);
        }
        int counter = 1;
        try {
            while (Files.exists(newLogFile)
                    && Files.size(newLogFile) > this.maxLogFileSize) {
                newLogFile = Paths.get(fullLogDir.toAbsolutePath().toString(),
                        this.logfileName + "." + counter);
                counter++;
            }
        } catch (IOException e) {
            //nop
        }
        return (newLogFile);
    }

    /**
     * Finally logs the passed message to the text component and sets the canvas
     * pos
     */
    private synchronized void logMessage(Level level, String message, int rawMessageLength) {
        Path fullLogDir = this.generateFullLogDir();
        Path newLogFile = this.generateNewLogFile(fullLogDir);
        try (BufferedWriter writer = Files.newBufferedWriter(newLogFile,
                StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            writer.write(message);
        } catch (Exception e) {
            System.out.println("DailySubdirFileLogging: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

}
