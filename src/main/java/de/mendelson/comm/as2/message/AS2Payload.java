package de.mendelson.comm.as2.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores all information about an as2 payload. Since AS2 1.2 it is allowed to
 * have multiple attachments in as2 transmission
 *
 * @author S.Heller
 * @version $Revision: 24 $
 */
public class AS2Payload implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Original filename of the sender, mustn't be provided
     */
    private String originalFilename = null;
    private final ByteStorage byteStorage = new ByteStorage();
    /**
     * Filename of the payload in the as2 system
     */
    private String payloadFilename = null;
    /**
     * Content id of this payload. May be null but is important for CEM because
     * the different certificates are referenced by their content id header
     */
    private String contentId = null;
    /**
     * Content description of this payload. Optional field for describing the payload content
     */
    private String contentDescription = null;
    /**
     * content type of this payload. Is not important any may be null for normal
     * AS2 messages but is important for CEM because the description xml is
     * identified by its content type
     */
    private String contentType = null;
    /**
     * Format of the payload (cXML, X12, EDIFACT, Unknown)
     */
    private String payloadFormat = null;
    /**
     * Document type of the payload (Purchase Order, Invoice, 810, DESADV, etc.)
     */
    private String payloadDocType = null;

    public AS2Payload() {
    }

    /**
     * Returns the content of this object for debug purpose
     */
    public String getDebugDisplay() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("originalFilename=\t\t").append(this.originalFilename);
        buffer.append("\n");
        buffer.append("data size=\t\t").append(this.byteStorage != null ? String.valueOf(this.byteStorage.getSize()) : "0");
        buffer.append("\n");
        buffer.append("payloadFilename=\t\t").append(this.payloadFilename);
        buffer.append("\n");
        buffer.append("\n");
        return (buffer.toString());
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public byte[] getData() throws Exception {
        return this.byteStorage.get();
    }

    public void setData(byte[] data) throws Exception {
        this.byteStorage.put(data);
    }

    public String getPayloadFilename() {
        return payloadFilename;
    }

    public void setPayloadFilename(String payloadFilename) {
        this.payloadFilename = payloadFilename;
    }

    /**
     * Writes the payload to the message to the passed file
     */
    public void writeTo(Path file) throws Exception {        
        try (InputStream inStream = this.byteStorage.getInputStream()) {
            try (OutputStream outStream = Files.newOutputStream(file,
                    StandardOpenOption.SYNC,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                inStream.transferTo(outStream);
            }
        }
    }

    /**
     * The standard instance of this payload does not contain the data but just
     * a reference to its filename. Calling this method will load the data into
     * the object if possible.
     */
    public void loadDataFromPayloadFile() throws Exception {
        this.byteStorage.put(Files.readAllBytes(Paths.get(this.payloadFilename)));
    }

    /**
     * @return the contentId
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * @param contentId the contentId to set
     */
    public void setContentId(String contentId) {
        if (contentId != null && contentId.startsWith("<") && contentId.endsWith(">")) {
            contentId = contentId.substring(1, contentId.length() - 1);
        }
        this.contentId = contentId;
    }

    /**
     * @return the contentDescription
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * @param contentDescription the contentDescription to set
     */
    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the payloadFormat
     */
    public String getPayloadFormat() {
        return payloadFormat;
    }

    /**
     * @param payloadFormat the payloadFormat to set
     */
    public void setPayloadFormat(String payloadFormat) {
        this.payloadFormat = payloadFormat;
    }

    /**
     * @return the payloadDocType
     */
    public String getPayloadDocType() {
        return payloadDocType;
    }

    /**
     * @param payloadDocType the payloadDocType to set
     */
    public void setPayloadDocType(String payloadDocType) {
        this.payloadDocType = payloadDocType;
    }

    /**
     * Releases all resources that have been allocated, e.g. temp files
     */
    public void releaseResources() {
        if (this.byteStorage != null) {
            this.byteStorage.release();
        }
    }
}
