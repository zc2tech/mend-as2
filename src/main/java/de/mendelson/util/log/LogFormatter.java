//$Header: /as2/de/mendelson/util/log/LogFormatter.java 28    20/02/25 13:42 Heller $
package de.mendelson.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Level;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Formatter to format the log of mq messages
 *
 * @author S.Heller
 */
public class LogFormatter extends Formatter {

    /**
     * Human readable timestamp - mainly not used. This field is just available
     * to allow humans to open and read the server logs in an editor
     */
    public static final String KEY_DATETIME = "dt";
    /**
     * Timestamp of the logline, this is not unique over all log entries as
     * everything could happen in one ms....
     */
    public static final String KEY_MILLISECS = "ms";
    /**
     * Sequence number to use for result sorting if more than one log line
     * happened in one ms
     */
    public static final String KEY_SEQUENCE = "seq";
    /**
     * The log message text itself..
     */
    public static final String KEY_LOGMESSAGE = "msg";

    /**
     * Generate a header for each line that contains the time only
     */
    public static final int FORMAT_CONSOLE = 1;
    public static final int FORMAT_LOGFILE = 2;
    public static final int FORMAT_CONSOLE_COLORED = 3;

    private final int formatType;

    private final static DateTimeFormatter DATE_TIME_FORMAT 
            = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
    private final static DateTimeFormatter TIME_FORMAT 
            = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
    
    private final Map<Level, String> COLOR_MAP = new ConcurrentHashMap<Level, String>();

    public LogFormatter(final int FORMAT_TYPE) {
        super();
        this.formatType = FORMAT_TYPE;
        if (FORMAT_TYPE == FORMAT_CONSOLE_COLORED) {
            this.setupDefaultColors();
        }
    }

    private void setupDefaultColors() {
        this.setColor(Level.SEVERE, ANSI.COLOR_SYSTEM_RED_BRIGHT);
        this.setColor(Level.WARNING, ANSI.COLOR_SYSTEM_BLUE_BRIGHT);
        this.setColor(Level.INFO, ANSI.COLOR_SYSTEM_GREY_BRIGHT);
        this.setColor(Level.CONFIG, ANSI.COLOR_SYSTEM_GREEN);
        this.setColor(Level.FINE, ANSI.COLOR_SYSTEM_GREEN);
        this.setColor(Level.FINER, ANSI.COLOR_SYSTEM_GREY);
        this.setColor(Level.FINEST, ANSI.COLOR_SYSTEM_GREY_BRIGHT);
    }

    /**
     * Overwrite this method to add product specific parameter to the log line
     */
    protected void addOutputToLog(int formatType, StringBuilder builder, Object[] recordParameter) {
    }

    /**
     * Sets a level color, this makes only sense if the type is
     * FORMAT_CONSOLE_COLORED
     *
     * @param ansiColor One of the colors defined in the class ANSI, e.g.
     * ANSI.COLOR_SYSTEM_GREEN
     */
    public void setColor(Level level, String ansiColor) {
        this.COLOR_MAP.put(level, ansiColor);
    }

    /**
     * Very fast approach and a little bit tricky: It takes advantage of the
     * fact that any number can be represented by the addition of powers of 2.
     * For example, 15 can be represented as 8+4+2+1, which all are powers of 2.
     */
    private int getDigitsInNumber(int number) {
        int length = 1;
        if (number >= 100000000) {
            length += 8;
            number /= 100000000;
        }
        if (number >= 10000) {
            length += 4;
            number /= 10000;
        }
        if (number >= 100) {
            length += 2;
            number /= 100;
        }
        if (number >= 10) {
            length += 1;
        }
        return length;
    }

    /**
     * Format the given LogRecord.
     *
     * @param logRecord the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord logRecord) {
        LocalDateTime dateTime = Instant.ofEpochMilli(logRecord.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (this.formatType == FORMAT_CONSOLE) {
            StringBuilder header = new StringBuilder();
            header.append("[")                    
                    .append(dateTime.format(TIME_FORMAT))
                    .append("] ");
            Object[] recordParameter = logRecord.getParameters();
            if (recordParameter != null) {
                this.addOutputToLog(this.formatType, header, recordParameter);
            }
            StringBuilder linesBuilder = new StringBuilder();
            linesBuilder.append(super.formatMessage(logRecord));
            if (logRecord.getThrown() != null) {
                try {
                    try (StringWriter stringWriter = new StringWriter()) {
                        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                            logRecord.getThrown().printStackTrace(printWriter);
                        }
                        linesBuilder.append(stringWriter.toString());
                    }
                } catch (Exception ex) {
                }
            }
            StringBuilder fullLine = new StringBuilder();
            String[] lines = linesBuilder.toString().split("\\n");
            if (lines != null) {
                for (String line : lines) {
                    fullLine.append(header)
                            .append(line)
                            .append(System.lineSeparator());
                }
            }
            return (fullLine.toString());
        }
        if (this.formatType == FORMAT_CONSOLE_COLORED) {
            StringBuilder header = new StringBuilder();
            header.append(ANSI.RESET)
                    .append(ANSI.COLOR_GREY62)
                    .append("[")
                    .append(dateTime.format(TIME_FORMAT))
                    .append("] ")
                    .append(this.COLOR_MAP.getOrDefault(logRecord.getLevel(), ""));
            Object[] recordParameter = logRecord.getParameters();
            if (recordParameter != null) {
                this.addOutputToLog(this.formatType, header, recordParameter);
            }
            StringBuilder linesBuilder = new StringBuilder();
            linesBuilder.append(super.formatMessage(logRecord));
            if (logRecord.getThrown() != null) {
                try {
                    try (StringWriter stringWriter = new StringWriter()) {
                        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                            logRecord.getThrown().printStackTrace(printWriter);
                        }
                        linesBuilder.append(stringWriter.toString());
                    }
                } catch (Exception ex) {
                }
            }
            StringBuilder fullLine = new StringBuilder();
            String[] lines = linesBuilder.toString().split("\\n");
            if (lines != null) {
                for (String line : lines) {
                    fullLine.append(header)
                            .append(line)
                            .append(System.lineSeparator());
                }
            }
            return (fullLine.toString());
        } else if (this.formatType == FORMAT_LOGFILE) {
            StringBuilder preHeader = new StringBuilder();
            preHeader.append("[")
                    .append(KEY_DATETIME).append("=")
                    .append(dateTime.format( DATE_TIME_FORMAT))
                    .append(",")
                    .append(KEY_MILLISECS).append("=")
                    .append(String.valueOf(logRecord.getMillis()))
                    .append(",")
                    .append(KEY_SEQUENCE).append("=")
                    .append(String.valueOf(logRecord.getSequenceNumber()));
            StringBuilder postHeader = new StringBuilder();
            Object[] recordParameter = logRecord.getParameters();
            if (recordParameter != null) {
                this.addOutputToLog(this.formatType, postHeader, recordParameter);
            }
            postHeader.append("]");
            StringBuilder fullLine = new StringBuilder();
            StringBuilder linesBuilder = new StringBuilder();
            linesBuilder.append(super.formatMessage(logRecord));
            if (logRecord.getThrown() != null) {
                linesBuilder.append("\n");
                try {
                    try (StringWriter stringWriter = new StringWriter()) {
                        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                            logRecord.getThrown().printStackTrace(printWriter);
                        }
                        linesBuilder.append(stringWriter.toString());
                    }
                } catch (Exception ex) {
                }
            }
            String[] lines = linesBuilder.toString().split("\\n");
            if (lines != null) {
                int subSequenceLength = this.getDigitsInNumber(lines.length);
                boolean multipleLines = lines.length > 1;
                for (int i = 0; i < lines.length; i++) {
                    fullLine.append(preHeader);
                    if (multipleLines) {
                        if (i % 10 == 0) {
                            subSequenceLength--;
                        }
                        fullLine.append("_");
                        for (int ii = 0; ii < subSequenceLength; ii++) {
                            fullLine.append("0");
                        }
                        fullLine.append(String.valueOf(i));
                    }
                    fullLine.append(postHeader)
                            .append(lines[i])
                            .append(System.lineSeparator());
                }
            }
            return (fullLine.toString());
        }
        return ("");
    }
}
