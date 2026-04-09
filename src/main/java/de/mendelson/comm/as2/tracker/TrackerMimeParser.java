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
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Parser for MIME messages to extract and save payloads
 *
 * @author Julian Xu
 */
public class TrackerMimeParser {

    private static final Logger LOGGER = Logger.getLogger("de.mendelson.as2.server");
    private final PreferencesAS2 preferences;
    private PayloadAnalyzer.PayloadAnalysis firstPayloadAnalysis;

    public TrackerMimeParser(PreferencesAS2 preferences) {
        this.preferences = preferences;
    }

    /**
     * Parse MIME message and extract payloads
     *
     * @param data Raw message data
     * @param contentType Content-Type header value
     * @param trackerId Tracker ID for file naming
     * @param rawFilename Raw message filename for subdirectory
     * @return Number of payloads extracted
     */
    public int parseAndExtractPayloads(byte[] data, String contentType, String trackerId, String rawFilename) {
        firstPayloadAnalysis = null; // Reset

        // Extract directory from rawFilename (e.g., "tracker/20260409" from "tracker/20260409/file.msg")
        Path rawPath = Paths.get(rawFilename);
        String dateFolder = rawPath.getParent().getFileName().toString();

        // Create payload subdirectory
        Path payloadDir = Paths.get(
                preferences.get(PreferencesAS2.DIR_MSG),
                "tracker",
                dateFolder,
                "payloads_" + trackerId
        );

        try {
            Files.createDirectories(payloadDir);
        } catch (IOException e) {
            LOGGER.warning("Failed to create payload directory for tracker " + trackerId + ": " + e.getMessage());
            return 0;
        }

        // Check if it's a MIME multipart message
        if (contentType != null && contentType.toLowerCase().contains("multipart")) {
            return extractMultipartPayloads(data, contentType, trackerId, payloadDir);
        } else {
            // Non-MIME message: treat entire content as single payload
            return extractSinglePayload(data, contentType, trackerId, payloadDir);
        }
    }

    /**
     * Get the analysis result for the first payload
     */
    public PayloadAnalyzer.PayloadAnalysis getFirstPayloadAnalysis() {
        return firstPayloadAnalysis;
    }

    /**
     * Extract payloads from MIME multipart message
     */
    private int extractMultipartPayloads(byte[] data, String contentType, String trackerId, Path payloadDir) {
        try {
            // Create a complete MIME message by prepending headers
            // The stored data is just the body, we need to add MIME headers
            StringBuilder mimeHeaders = new StringBuilder();
            mimeHeaders.append("Content-Type: ").append(contentType).append("\r\n");
            mimeHeaders.append("\r\n");

            // Combine headers and body
            byte[] headerBytes = mimeHeaders.toString().getBytes("UTF-8");
            byte[] fullMessage = new byte[headerBytes.length + data.length];
            System.arraycopy(headerBytes, 0, fullMessage, 0, headerBytes.length);
            System.arraycopy(data, 0, fullMessage, headerBytes.length, data.length);

            // Create MimeMessage from complete message
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props);

            MimeMessage mimeMessage = new MimeMessage(session, new ByteArrayInputStream(fullMessage));

            Object content = mimeMessage.getContent();

            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                int payloadCount = multipart.getCount();

                // Extract each part
                for (int i = 0; i < payloadCount; i++) {
                    try {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        byte[] payloadData = extractPayload(bodyPart, payloadDir, i + 1);

                        // Analyze first payload
                        if (i == 0 && payloadData != null) {
                            firstPayloadAnalysis = PayloadAnalyzer.analyze(payloadData);
                        }
                    } catch (Exception e) {
                        LOGGER.warning("Failed to extract payload " + (i + 1) + " for tracker " + trackerId + ": " + e.getMessage());
                    }
                }

                LOGGER.info("Extracted " + payloadCount + " MIME payloads for tracker " + trackerId);
                return payloadCount;
            }

        } catch (Exception e) {
            LOGGER.warning("Failed to parse MIME message for tracker " + trackerId + ": " + e.getMessage());
        }

        return 0;
    }

    /**
     * Extract single payload from non-MIME message
     */
    private int extractSinglePayload(byte[] data, String contentType, String trackerId, Path payloadDir) {
        try {
            // Analyze the payload before saving
            firstPayloadAnalysis = PayloadAnalyzer.analyze(data);

            // Generate filename based on content type
            String extension = getExtensionFromContentType(contentType);
            String filename = "payload_1" + extension;

            Path outputPath = payloadDir.resolve(filename);

            // Write entire content as single payload
            Files.write(outputPath, data, StandardOpenOption.CREATE_NEW);

            LOGGER.info("Extracted 1 non-MIME payload for tracker " + trackerId);
            return 1;

        } catch (Exception e) {
            LOGGER.warning("Failed to extract non-MIME payload for tracker " + trackerId + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Extract a single payload to file
     */
    private byte[] extractPayload(BodyPart bodyPart, Path payloadDir, int partNumber) throws MessagingException, IOException {
        // Get filename from Content-Disposition or generate one
        String filename = bodyPart.getFileName();
        if (filename == null || filename.trim().isEmpty()) {
            // Get content type and create extension
            String contentType = bodyPart.getContentType();
            String extension = getExtensionFromContentType(contentType);
            filename = "payload_" + partNumber + extension;
        }

        // Sanitize filename
        filename = sanitizeFilename(filename);

        Path outputPath = payloadDir.resolve(filename);

        // Read payload content
        InputStream inputStream = bodyPart.getInputStream();
        byte[] payloadData = inputStream.readAllBytes();

        // Write to file
        Files.write(outputPath, payloadData, StandardOpenOption.CREATE_NEW);

        LOGGER.fine("Saved payload: " + outputPath.toAbsolutePath());

        return payloadData;
    }

    /**
     * Get file extension from content type
     */
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".bin";
        }

        contentType = contentType.toLowerCase();

        // Common MIME types
        if (contentType.contains("text/plain")) return ".txt";
        if (contentType.contains("text/html")) return ".html";
        if (contentType.contains("text/xml") || contentType.contains("application/xml")) return ".xml";
        if (contentType.contains("application/json")) return ".json";
        if (contentType.contains("application/pdf")) return ".pdf";
        if (contentType.contains("application/zip")) return ".zip";
        if (contentType.contains("image/jpeg")) return ".jpg";
        if (contentType.contains("image/png")) return ".png";
        if (contentType.contains("image/gif")) return ".gif";
        if (contentType.contains("application/pkcs7-signature")) return ".p7s";
        if (contentType.contains("application/pkcs7-mime")) return ".p7m";

        return ".bin";
    }

    /**
     * Sanitize filename to prevent directory traversal and invalid characters
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed";
        }

        // Remove path separators and dangerous characters
        filename = filename.replaceAll("[/\\\\:*?\"<>|]", "_");

        // Remove leading dots
        filename = filename.replaceAll("^\\.+", "");

        // Limit length
        if (filename.length() > 200) {
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex > 0) {
                String ext = filename.substring(dotIndex);
                filename = filename.substring(0, 200 - ext.length()) + ext;
            } else {
                filename = filename.substring(0, 200);
            }
        }

        return filename;
    }
}
