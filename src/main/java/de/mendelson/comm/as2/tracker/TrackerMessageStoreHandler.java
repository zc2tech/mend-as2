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
package de.mendelson.comm.as2.tracker;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles file storage for tracker messages with date-based folder structure
 *
 * @author Julian Xu
 */
public class TrackerMessageStoreHandler {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss-SSS");

    private final PreferencesAS2 preferences;

    public TrackerMessageStoreHandler(PreferencesAS2 preferences) {
        this.preferences = preferences;
    }

    /**
     * Store tracker message to filesystem
     *
     * @param data Message content
     * @param trackerId Unique tracker ID
     * @return Relative path to stored file
     * @throws IOException If storage fails
     */
    public String storeTrackerMessage(byte[] data, String trackerId) throws IOException {
        // Create date-based directory structure
        LocalDateTime now = LocalDateTime.now();
        String dateFolder = now.format(DATE_FORMAT);

        Path storageDir = Paths.get(
                preferences.get(PreferencesAS2.DIR_MSG),
                "tracker",
                dateFolder
        );

        // Create directories if they don't exist
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_FILE_OPERATION_ANY,
                    "Failed to create tracker storage directory",
                    "Directory: " + storageDir.toAbsolutePath() + "\nError: " + e.getMessage()
            );
            throw e;
        }

        // Generate filename: HH-mm-ss-SSS_TRACKER_<uuid>.msg
        String filename = now.format(TIME_FORMAT) + "_TRACKER_" + trackerId + ".msg";
        Path messagePath = storageDir.resolve(filename);

        // Write message to file
        try {
            Files.write(messagePath, data, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_FILE_OPERATION_ANY,
                    "Failed to write tracker message",
                    "File: " + messagePath.toAbsolutePath() + "\nError: " + e.getMessage()
            );
            throw e;
        }

        // Return relative path: tracker/yyyyMMdd/filename
        return Paths.get("tracker", dateFolder, filename).toString();
    }

    /**
     * Read tracker message from filesystem
     *
     * @param relativePath Relative path returned by storeTrackerMessage
     * @return Message content
     * @throws IOException If read fails
     */
    public byte[] readTrackerMessage(String relativePath) throws IOException {
        Path fullPath = Paths.get(preferences.get(PreferencesAS2.DIR_MSG), relativePath);

        if (!Files.exists(fullPath)) {
            throw new IOException("Tracker message file not found: " + relativePath);
        }

        try {
            return Files.readAllBytes(fullPath);
        } catch (IOException e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_FILE_OPERATION_ANY,
                    "Failed to read tracker message",
                    "File: " + fullPath.toAbsolutePath() + "\nError: " + e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Check if tracker message file exists
     *
     * @param relativePath Relative path
     * @return true if file exists
     */
    public boolean trackerMessageExists(String relativePath) {
        Path fullPath = Paths.get(preferences.get(PreferencesAS2.DIR_MSG), relativePath);
        return Files.exists(fullPath);
    }

    /**
     * Get full path for a tracker message
     *
     * @param relativePath Relative path
     * @return Full absolute path
     */
    public Path getFullPath(String relativePath) {
        return Paths.get(preferences.get(PreferencesAS2.DIR_MSG), relativePath);
    }

    /**
     * Delete old tracker message files
     *
     * @param dateFolder Date folder in yyyyMMdd format
     * @return true if folder was deleted
     */
    public boolean deleteTrackerFolder(String dateFolder) {
        Path folderPath = Paths.get(
                preferences.get(PreferencesAS2.DIR_MSG),
                "tracker",
                dateFolder
        );

        if (!Files.exists(folderPath)) {
            return false;
        }

        try {
            // Delete all files in folder
            Files.walk(folderPath)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order for directory deletion
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Log but continue
                        }
                    });
            return true;
        } catch (IOException e) {
            SystemEventManagerImplAS2.instance().newEvent(
                    SystemEvent.SEVERITY_WARNING,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_FILE_OPERATION_ANY,
                    "Failed to delete tracker folder",
                    "Folder: " + folderPath.toAbsolutePath() + "\nError: " + e.getMessage()
            );
            return false;
        }
    }
}
