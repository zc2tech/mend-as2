package de.mendelson.util;

import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Some programming tools for mendelson business integration
 *
 * @author S.Heller
 * @version $Revision: 20 $
 */
public class AS2Tools {

    private AS2Tools(){        
    }
    
    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    public static String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

    /**
     * Folds a string using the passed delimiter where the max line length is
     * the passed lineLenght
     *
     * @param source Source string to use
     * @param delimiter Delimiter to add at the folding point
     * @param lineLength Max line length of the result
     */
    public static final String fold(String source, String delimiter, int lineLength) {
        if (source == null) {
            return ("null");
        }
        StringBuilder result = new StringBuilder();
        int linePos = 0;
        for (int i = 0; i < source.length(); i++) {
            char singleChar = source.charAt(i);
            if (singleChar == ' ' && linePos >= lineLength) {
                result.append(delimiter);
                linePos = 0;
            } else {
                result.append(singleChar);
                linePos++;
            }
        }
        return (result.toString());
    }

    /**
     * Returns the daily temp directory as absolute path. If the directory does
     * not exist it is created
     *
     * @return
     */
    public static String getDailyTempDir() throws IOException {
        //date format is not thread safe!
        DateFormat dateFormatTempName = new SimpleDateFormat("yyyyMMdd");
        Path tempDateDir = Paths.get("temp", dateFormatTempName.format(new Date()));
        if (!Files.exists(tempDateDir)) {
            try {
                Files.createDirectories(tempDateDir);
            } catch (IOException e) {
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        tempDateDir.toAbsolutePath().toString());
                throw e;
            }
        }
        return (tempDateDir.toAbsolutePath().toString());
    }

    /**
     * Creates a temp file in a data stamped folder below the directory temp
     */
    public static synchronized Path createTempFile(String prefix, String suffix) throws IOException {
        String tempDateDirStr = getDailyTempDir();
        //create a unique file in the temp subdirectory
        Path tempFile = Files.createTempFile(Paths.get(tempDateDirStr), prefix, suffix);
        return (tempFile);
    }

    /**
     * Displays the passed data size in a proper format
     */
    public static String getDataSizeDisplay(long size) {
        StringBuilder builder = new StringBuilder();
        try (Formatter formatter = new Formatter(builder)) {
            if (size > 1.048E6) {
                formatter.format(Locale.getDefault(), "%.2f", Float.valueOf((float) size / (float) 1.048E6));
                builder.append(" ").append("MB");
                return (builder.toString());
            } else if (size > 1024L) {
                formatter.format(Locale.getDefault(), "%.2f", Float.valueOf((float) size / 1024f));
                builder.append(" ").append("KB");
                return (builder.toString());
            }
        }
        return (String.valueOf(size) + " Byte");
    }

    /**
     * Displays a duration
     */
    public static String getTimeDisplay(long duration) {
        NumberFormat formatter = new DecimalFormat("0.00");
        if (duration < 1000) {
            return (duration + "ms");
        }
        float timeInSecs = (float) ((float) duration / 1000f);
        return (formatter.format(timeInSecs) + "s");
    }

    /**
     * Converts a suggested filename to a valid filename. This may be necessary
     * if as2 ids contain chars that are not allowed in the current file system
     * This method will also replace the "." by "_"
     */
    public static String convertToValidFilename(String filename) {
        //replace everything that may be a problem, e.g. pathes etc
        String invalidChars = "\\/:*?\"<>|";
        for (int i = 0; i < invalidChars.length(); i++) {
            filename = filename.replace(invalidChars.charAt(i), '_');
        }
        //replace some additional chars
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, length = filename.length(); i < length; i++) {
            char c = filename.charAt(i);
            int type = Character.getType(c);
            if (c == '@'
                    || c == '-'
                    || type == Character.DECIMAL_DIGIT_NUMBER
                    || type == Character.LETTER_NUMBER
                    || type == Character.LOWERCASE_LETTER
                    || type == Character.OTHER_LETTER
                    || type == Character.OTHER_NUMBER
                    || type == Character.TITLECASE_LETTER
                    || type == Character.UPPERCASE_LETTER) {
                buffer.append(c);
            } else {
                buffer.append('_');
            }
        }
        return (buffer.toString());
    }

    /**
     * Converts a suggested filename to a valid filename. Prevents any path
     * characters and special characters and replaces them by "_". This method
     * will also replace multiple ".." character by a single one and will delete
     * any "." at the end of the filename because some Windows System could not
     * deal with this
     */
    public static String convertToValidFilenameAllowSinglePoint(String filename) {
        while (filename.contains("..")) {
            filename = filename.replace("..", ".");
        }
        if (filename.endsWith(".")) {
            if (filename.length() == 1) {
                filename = "_";
            } else {
                filename = filename.substring(0, filename.length() - 1);
            }
        }
        //replace everything that may be a problem, e.g. pathes etc
        String invalidChars = "\\/:*?\"<>|";
        for (int i = 0; i < invalidChars.length(); i++) {
            filename = filename.replace(invalidChars.charAt(i), '_');
        }
        //replace some additional chars
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, length = filename.length(); i < length; i++) {
            char c = filename.charAt(i);
            int type = Character.getType(c);
            if (c == '@'
                    || c == '.'
                    || c == '-'
                    || type == Character.DECIMAL_DIGIT_NUMBER
                    || type == Character.LETTER_NUMBER
                    || type == Character.LOWERCASE_LETTER
                    || type == Character.OTHER_LETTER
                    || type == Character.OTHER_NUMBER
                    || type == Character.TITLECASE_LETTER
                    || type == Character.UPPERCASE_LETTER) {
                buffer.append(c);
            } else {
                buffer.append('_');
            }
        }
        return (buffer.toString());
    }

}
