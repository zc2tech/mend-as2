//$Header: /as2/de/mendelson/util/clientserver/console/LoggingHandlerPrintStream.java 4     20/02/25 13:41 Heller $
package de.mendelson.util.clientserver.console;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Formatter;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handler to log output to a PrintStream
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class LoggingHandlerPrintStream extends Handler {

    private final PrintStream out;
    private Formatter logFormatter = null;

    public LoggingHandlerPrintStream(PrintStream out) {
        this.out = out;
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
        try {
            if (this.logFormatter == null) {
                this.logMessageUnformatted(logRecord.getMessage());
            } else {
                this.logMessageFormatted(logRecord);
            }
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
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

    /**
     * Finally logs the passed message to the output stream
     */
    private synchronized void logMessageUnformatted(String message) {
        this.out.printf(message + "\n");
    }

    /**
     * Finally logs the passed message using the formatter
     */
    private synchronized void logMessageFormatted(LogRecord logRecord) {
        String message;
        try {
            message = this.logFormatter.format(logRecord);
        } catch (Throwable ex) {
            ex.printStackTrace();
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            if (ex instanceof Exception) {
                reportError(null, (Exception) ex, ErrorManager.FORMAT_FAILURE);
            }
            return;
        }
        try {
            this.out.printf(message);
        } catch (Throwable ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            if (ex instanceof Exception) {
                reportError(null, (Exception) ex, ErrorManager.WRITE_FAILURE);
            }
        }
    }
    
    
    @Override
    public void setFormatter(Formatter formatter) throws SecurityException {
            this.logFormatter = formatter;
    }

}
