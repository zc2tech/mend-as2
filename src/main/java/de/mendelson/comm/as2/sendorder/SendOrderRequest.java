/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.sendorder;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Request object for enqueueing a send order.
 * Contains minimal metadata - no pre-built AS2Message.
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class SendOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private int senderDBId = -1;
    private int receiverDBId = -1;
    private Path[] files;
    private String[] originalFilenames;
    private String userdefinedId;
    private String subject;
    private String[] payloadContentTypes;
    private int userId = -1; // WebUI user ID for HTTP auth preference resolution

    public SendOrderRequest() {
    }

    public int getSenderDBId() {
        return senderDBId;
    }

    public SendOrderRequest setSenderDBId(int senderDBId) {
        this.senderDBId = senderDBId;
        return this;
    }

    public int getReceiverDBId() {
        return receiverDBId;
    }

    public SendOrderRequest setReceiverDBId(int receiverDBId) {
        this.receiverDBId = receiverDBId;
        return this;
    }

    public Path[] getFiles() {
        return files;
    }

    public SendOrderRequest setFiles(Path[] files) {
        this.files = files;
        return this;
    }

    public String[] getOriginalFilenames() {
        return originalFilenames;
    }

    public SendOrderRequest setOriginalFilenames(String[] originalFilenames) {
        this.originalFilenames = originalFilenames;
        return this;
    }

    public String getUserdefinedId() {
        return userdefinedId;
    }

    public SendOrderRequest setUserdefinedId(String userdefinedId) {
        this.userdefinedId = userdefinedId;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SendOrderRequest setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String[] getPayloadContentTypes() {
        return payloadContentTypes;
    }

    public SendOrderRequest setPayloadContentTypes(String[] payloadContentTypes) {
        this.payloadContentTypes = payloadContentTypes;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public SendOrderRequest setUserId(int userId) {
        this.userId = userId;
        return this;
    }
}
