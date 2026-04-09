package de.mendelson.comm.as2.message.store;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores messages in specified directories
 *
 * @author S.Heller
 * @version $Revision: 102 $
 */
public class MessageStoreHandler {

    /**
     * products preferences
     */
    private final PreferencesAS2 preferences;
    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * localize the output
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageStoreHandler.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private final String CRLF = new String(new byte[]{0x0d, 0x0a});
    private final IDBDriverManager dbDriverManager;
    //DateTimeFormatter is thread safe
    private final static DateTimeFormatter DATE_FORMAT_RAW_INCOMING_FILE = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    //DateTimeFormatter is thread safe
    private final static DateTimeFormatter DATE_FORMAT_ERROR_MESSAGE = DateTimeFormatter.ofPattern("yyyyMMdd");
    //DateTimeFormatter is thread safe
    private final static DateTimeFormatter DATE_FORMAT_SENT_MESSAGE = DateTimeFormatter.ofPattern("yyyyMMdd");

    public MessageStoreHandler(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.preferences = new PreferencesAS2(dbDriverManager);
    }

    /**
     * Stores incoming data for the server without analyzing it, raw Returns the
     * raw filename and the header filename
     */
    public String[] storeRawIncomingData(byte[] data, Properties header, String remoteHost) throws IOException {
        String[] filenames = new String[2];
        Path inRawDir = Paths.get(Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString(),
                "_rawincoming");
        //ensure the directory exists
        if (!Files.exists(inRawDir)) {
            try {
                Files.createDirectories(inRawDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        inRawDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        inRawDir.toAbsolutePath().toString());
            }
        }
        StringBuilder rawFilename = new StringBuilder();
        rawFilename.append(LocalDateTime.now().format(
                DATE_FORMAT_RAW_INCOMING_FILE)).append("_");
        if (remoteHost != null) {
            rawFilename.append(remoteHost);
        } else {
            rawFilename.append("unknownhost");
        }
        rawFilename.append("_");
        String validFilename = AS2Tools.convertToValidFilename(rawFilename.toString());
        //create unique filename
        Path rawDataFile = Files.createTempFile(inRawDir, validFilename, ".as2");
        Files.write(rawDataFile, data,
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        //write header
        Path headerFile = Paths.get(rawDataFile.toAbsolutePath().toString() + ".header");
        StringBuilder headerStrBuilder = new StringBuilder();
        Enumeration<?> enumeration = header.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            headerStrBuilder.append(key)
                    .append(" = ")
                    .append(header.getProperty(key))
                    .append(CRLF);
        }
        //ensure the underlaying filesystem provider synchronizes the data with the filesystem
        try (OutputStream outStreamHeader = Files.newOutputStream(headerFile,
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            outStreamHeader.write(headerStrBuilder.toString().getBytes());
        }
        filenames[0] = rawDataFile.toAbsolutePath().toString();
        filenames[1] = headerFile.toAbsolutePath().toString();
        return (filenames);
    }

    /**
     * If a message state is OK the payload has to be moved to the right
     * directory
     *
     * @param messageType could be a normal EDI message or a CEM
     */
    public void movePayloadToInbox(int messageType, String messageId,
            Partner localstation, Partner senderstation) throws Exception {
        StringBuilder inBoxDirPath = new StringBuilder();
        inBoxDirPath.append(localstation.getMessagePath(
                Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString(), 
                FileSystems.getDefault().getSeparator()));
        inBoxDirPath.append(FileSystems.getDefault().getSeparator());
        if (messageType == AS2Message.MESSAGETYPE_AS2) {
            inBoxDirPath.append("inbox");
        } else if (messageType == AS2Message.MESSAGETYPE_CEM) {
            inBoxDirPath.append("certificates");
        }
        if (this.preferences.getBoolean(PreferencesAS2.RECEIPT_PARTNER_SUBDIR)) {
            inBoxDirPath.append(FileSystems.getDefault().getSeparator());
            inBoxDirPath.append(AS2Tools.convertToValidFilename(senderstation.getName()));
        }
        //store incoming message
        Path inboxDir = Paths.get(inBoxDirPath.toString());
        //ensure the directory exists
        if (!Files.exists(inboxDir)) {
            try {
                Files.createDirectories(inboxDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        inboxDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        inboxDir.toAbsolutePath().toString());
            }
        }
        //load message overview from database
        MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
        List<AS2Payload> payloadList = messageAccess.getPayload(messageId);
        AS2MessageInfo messageInfo = messageAccess.getLastMessageEntry(messageId);
        if (payloadList != null) {
            for (int i = 0; i < payloadList.size(); i++) {
                String payloadFilename = payloadList.get(i).getPayloadFilename();
                if (payloadFilename == null) {
                    continue;
                }
                //source where to copy from
                Path inFile = Paths.get(payloadFilename);
                long payloadSize = Files.size(inFile);
                //is it defined to keep the original filename for messages from this sender?
                if (senderstation.getKeepOriginalFilenameOnReceipt()
                        && payloadList.get(i).getOriginalFilename() != null
                        && !payloadList.get(i).getOriginalFilename().isEmpty()) {
                    payloadFilename = payloadList.get(i).getOriginalFilename();
                }
                //is it a CEM? Take the content id as filename and add an extension
                if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM && payloadList.get(i).getContentId() != null) {
                    payloadFilename = payloadFilename + "_" + AS2Tools.convertToValidFilename(payloadList.get(i).getContentId());
                    if (payloadList.get(i).getContentType() != null) {
                        if (payloadList.get(i).getContentType().toLowerCase().contains("ediint-cert-exchange+xml")) {
                            payloadFilename = payloadFilename + ".xml";
                        } else {
                            payloadFilename = payloadFilename + ".p7c";
                        }
                    }
                }
                StringBuilder outFilename = new StringBuilder();
                outFilename.append(inboxDir.toAbsolutePath().toString())
                        .append(FileSystems.getDefault().getSeparator())
                        .append(Paths.get(payloadFilename).getFileName().toString());
                Path outFile = Paths.get(outFilename.toString());
                Files.move(inFile, outFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
                payloadList.get(i).setPayloadFilename(outFilename.toString());
                this.logger.log(Level.FINE, rb.getResourceString("comm.success",
                        new Object[]{
                            String.valueOf(i + 1),
                            outFilename.toString(),
                            String.valueOf(AS2Tools.getDataSizeDisplay(payloadSize))
                        }), messageInfo);
            }
            messageAccess.insertPayloads(messageId, payloadList);
        }
    }

    /**
     * Stores an incoming message payload to the right partners mailbox, the
     * decrypted message to the raw directory The filenames of the files where
     * the data has been stored in is written to the message object
     */
    public void storeParsedIncomingMessage(AS2Message message, Partner localstation) throws Exception {
        //do not store signals payload in pending dir
        if (!message.getAS2Info().isMDN()) {
            //store incoming message
            StringBuilder inBoxDirPath = new StringBuilder();
            inBoxDirPath.append(localstation.getMessagePath(
                    Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString(),
                    FileSystems.getDefault().getSeparator()))
                    .append(FileSystems.getDefault().getSeparator())
                    .append("inbox");
            //store incoming message
            Path inboxDir = Paths.get(inBoxDirPath.toString());
            //ensure the directory exists
            if (!Files.exists(inboxDir)) {
                try {
                    Files.createDirectories(inboxDir);
                } catch (Exception e) {
                    this.logger.warning(rb.getResourceString("dir.createerror",
                            inboxDir.toAbsolutePath().toString()));
                    SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                            inboxDir.toAbsolutePath().toString());
                }
            }
            //store the payload to the pending directory. It resists there as long as no positive MDN comes in
            Path pendingDir = Paths.get(inboxDir.toAbsolutePath().toString(), "pending");
            if (!Files.exists(pendingDir)) {
                try {
                    Files.createDirectories(pendingDir);
                } catch (Exception e) {
                    this.logger.warning(rb.getResourceString("dir.createerror",
                            pendingDir.toAbsolutePath().toString()));
                    SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                            pendingDir.toAbsolutePath().toString());
                }
            }
            for (int i = 0; i < message.getPayloadCount(); i++) {
                AS2Payload payload = message.getPayload(i);
                StringBuilder pendingFilename = new StringBuilder();
                pendingFilename.append(pendingDir.toAbsolutePath());
                pendingFilename.append(FileSystems.getDefault().getSeparator());
                pendingFilename.append(AS2Tools.convertToValidFilename(message.getAS2Info().getMessageId()));
                if (message.getPayloadCount() > 1) {
                    pendingFilename.append("_").append(String.valueOf(i));
                }
                Path pendingFile = Paths.get(pendingFilename.toString());
                payload.writeTo(pendingFile);
                payload.setPayloadFilename(pendingFile.toAbsolutePath().toString());
            }
            MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
            messageAccess.insertPayloads(message.getAS2Info().getMessageId(), message.getPayloads());
            Path decryptedRawFile = Paths.get(message.getAS2Info().getRawFilename() + ".decrypted");
            //ensure the underlaying filesystem provider synchronizes the data with the filesystem
            try (OutputStream outStream = Files.newOutputStream(decryptedRawFile,
                    StandardOpenOption.SYNC,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                try (InputStream inStream = message.getDecryptedRawDataInputStream()) {
                    inStream.transferTo(outStream);
                }
            }
            ((AS2MessageInfo) message.getAS2Info()).setRawFilenameDecrypted(decryptedRawFile.toAbsolutePath().toString());
        }
    }

    /**
     * Stores the message if an error occured during creation or sending the
     * message
     */
    public void storeSentErrorMessage(AS2Message message, Partner localstation, Partner receiver) throws Exception {
        StringBuilder errorDirName = new StringBuilder();
        errorDirName.append(Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString());
        errorDirName.append(FileSystems.getDefault().getSeparator());
        errorDirName.append(AS2Tools.convertToValidFilename(receiver.getName())).append(FileSystems.getDefault().getSeparator()).append("error");
        errorDirName.append(FileSystems.getDefault().getSeparator()).append(AS2Tools.convertToValidFilename(localstation.getName()));
        errorDirName.append(FileSystems.getDefault().getSeparator()).append(
                LocalDateTime.now().format(DATE_FORMAT_ERROR_MESSAGE));
        //store sent message
        Path errorDir = Paths.get(errorDirName.toString());
        //ensure the directory exists
        if (!Files.exists(errorDir)) {
            try {
                Files.createDirectories(errorDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        errorDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        errorDir.toAbsolutePath().toString());
            }
        }
        //write out the payload(s)
        for (int i = 0; i < message.getPayloadCount(); i++) {
            Path payloadFile = Files.createTempFile(errorDir, "AS2Message", ".as2");
            message.getPayload(i).writeTo(payloadFile);
            message.getPayload(i).setPayloadFilename(payloadFile.toAbsolutePath().toString());
            this.logger.log(Level.SEVERE, rb.getResourceString("message.error.stored",
                    new Object[]{
                        payloadFile.toAbsolutePath().toString()
                    }), message.getAS2Info());
        }
        //write raw file to error/raw
        Path errorRawDir = Paths.get(errorDir.toAbsolutePath().toString(), "raw");
        //ensure the directory exists
        if (!Files.exists(errorRawDir)) {
            try {
                Files.createDirectories(errorRawDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        errorRawDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        errorDir.toAbsolutePath().toString());
            }
        }
        Path errorRawFile = Files.createTempFile(errorRawDir, "error", ".raw");
        message.writeRawDecryptedTo(errorRawFile);
        this.logger.log(Level.SEVERE, rb.getResourceString("message.error.raw.stored",
                new Object[]{
                    errorRawFile.toAbsolutePath().toString()
                }), message.getAS2Info());
        MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
        if (!message.getAS2Info().isMDN()) {
            AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
            messageInfo.setRawFilenameDecrypted(errorRawFile.toAbsolutePath().toString());
            //update the filenames in the db            
            messageAccess.updateFilenames(messageInfo);
        }
        messageAccess.insertPayloads(message.getAS2Info().getMessageId(), message.getPayloads());
    }

    /**
     * Stores an outgoing message in a sent directory
     */
    public void storeSentMessage(AS2Message message, Partner localstation, Partner receiver, Properties header) throws Exception {
        String receiverName = "unidentified";
        if (receiver != null) {
            receiverName = AS2Tools.convertToValidFilename(receiver.getName());
        }
        String localStationName = "unknown";
        if (localstation != null) {
            localStationName = AS2Tools.convertToValidFilename(localstation.getName());
        }
        //store sent message
        Path sentDir = Paths.get(
                Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString(),
                receiverName,
                "sent",
                localStationName,
                LocalDateTime.now().format(DATE_FORMAT_SENT_MESSAGE));
        //ensure the directory exists
        if (!Files.exists(sentDir)) {
            try {
                Files.createDirectories(sentDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        sentDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        sentDir.toAbsolutePath().toString());
            }
        }
        AS2Info as2Info = message.getAS2Info();
        String requestType = "";
        if (as2Info.isMDN()) {
            requestType = "_MDN";
        }
        StringBuilder rawFilename = new StringBuilder();
        rawFilename.append(sentDir.toAbsolutePath().toString())
                .append(FileSystems.getDefault().getSeparator())
                .append(AS2Tools.convertToValidFilename(as2Info.getMessageId()))
                .append(requestType)
                .append(".as2");
        Path headerFile = Paths.get(rawFilename.toString() + ".header");
        StringBuilder headerStrBuilder = new StringBuilder();
        Enumeration<?> keyEnumeration = header.keys();
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            headerStrBuilder.append(key)
                    .append(" = ")
                    .append(header.getProperty(key))
                    .append(CRLF);
        }
        //ensure the underlaying filesystem provider synchronizes the data with the filesystem
        try (OutputStream outStream = Files.newOutputStream(headerFile,
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            outStream.write(headerStrBuilder.toString().getBytes());
        }
        as2Info.setHeaderFilename(headerFile.toAbsolutePath().toString());
        Path rawFile = Paths.get(rawFilename.toString());
        try (InputStream rawDataInStream = message.getDecryptedRawDataInputStream()) {
            //ensure the underlaying filesystem provider synchronizes the data with the filesystem
            try (OutputStream outFileStream = Files.newOutputStream(rawFile,
                    StandardOpenOption.SYNC,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                rawDataInStream.transferTo(outFileStream);
            }
        }
        Path rawFileDecrypted = Paths.get(rawFilename.toString() + ".decrypted");
        InputStream contentSourceStream = null;
        try {
            if (as2Info.isMDN()) {
                contentSourceStream = message.getRawDataInputStream();
            } else {
                contentSourceStream = message.getDecryptedRawDataInputStream();
            }
            //ensure the underlaying filesystem provider synchronizes the data with the filesystem
            try (OutputStream outStream = Files.newOutputStream(rawFileDecrypted,
                    StandardOpenOption.SYNC,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                contentSourceStream.transferTo(outStream);
            }
        } finally {
            if (contentSourceStream != null) {
                contentSourceStream.close();
            }
        }
        for (int i = 0; i < message.getPayloadCount(); i++) {
            StringBuilder payloadFilename = new StringBuilder();
            payloadFilename.append(sentDir.toAbsolutePath().toString()).append(FileSystems.getDefault().getSeparator());
            String originalFilename = message.getPayload(i).getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unknown";
            }
            payloadFilename.append(AS2Tools.convertToValidFilename(as2Info.getMessageId()));
            if (message.getPayloadCount() > 1) {
                payloadFilename.append("_");
                payloadFilename.append(String.valueOf(i + 1));
            }
            payloadFilename.append(".payload");
            Path payloadFile = Paths.get(payloadFilename.toString());
            message.getPayload(i).writeTo(payloadFile);
            message.getPayload(i).setPayloadFilename(payloadFile.toAbsolutePath().toString());
        }
        //set all filenames to the message object
        as2Info.setRawFilename(rawFile.toAbsolutePath().toString());
        as2Info.setHeaderFilename(headerFile.toAbsolutePath().toString());
        //update the filenames in the db
        MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
        if (!as2Info.isMDN()) {
            AS2MessageInfo messageInfo = (AS2MessageInfo) as2Info;
            messageInfo.setRawFilenameDecrypted(rawFileDecrypted.toAbsolutePath().toString());
            messageAccess.updateFilenames(messageInfo);
        }
        messageAccess.insertPayloads(message.getAS2Info().getMessageId(), message.getPayloads());
    }

    /**
     * Stores the status information for outbound transactions in a file
     */
    public void writeOutboundStatusFile(AS2MessageInfo messageInfo) throws Exception {
        //ignore the write process if this is not requested in the preferences
        if (!this.preferences.getBoolean(PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE)) {
            return;
        }
        PartnerAccessDB partnerAccessDB = new PartnerAccessDB(this.dbDriverManager);
        Partner sender = partnerAccessDB.getPartner(messageInfo.getSenderId());
        Partner receiver = partnerAccessDB.getPartner(messageInfo.getReceiverId());
        MessageAccessDB access = new MessageAccessDB(this.dbDriverManager);
        List<AS2Payload> payload = access.getPayload(messageInfo.getMessageId());
        //deal with the status directory
        Path statusDir = Paths.get("outboundstatus");
        //ensure the directory exists
        if (!Files.exists(statusDir)) {
            try {
                Files.createDirectories(statusDir);
            } catch (Exception e) {
                this.logger.warning(rb.getResourceString("dir.createerror",
                        statusDir.toAbsolutePath().toString()));
                SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                        statusDir.toAbsolutePath().toString());
            }
        }
        StringBuilder rawFilename = new StringBuilder();
        rawFilename.append(statusDir.toAbsolutePath().toString());
        rawFilename.append(FileSystems.getDefault().getSeparator());
        for (int i = 0; i < payload.size(); i++) {
            rawFilename.append(payload.get(i).getOriginalFilename());
            rawFilename.append("_");
        }
        rawFilename.append(AS2Tools.convertToValidFilename(messageInfo.getMessageId()));
        rawFilename.append(".sent.state");
        Path statusFile = Paths.get(rawFilename.toString());
        //ensure the underlaying filesystem provider synchronizes the data with the filesystem
        try (OutputStream outStream = Files.newOutputStream(statusFile,
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            outStream.write("product=".getBytes());
            outStream.write(AS2ServerVersion.getProductName().getBytes());
            outStream.write(" ".getBytes());
            outStream.write(AS2ServerVersion.getVersion().getBytes());
            outStream.write(" ".getBytes());
            outStream.write(AS2ServerVersion.getBuild().getBytes());
            outStream.write("\n".getBytes());
            for (int i = 0; i < payload.size(); i++) {
                String originalFileKey = "originalfile." + i + "=";
                outStream.write(originalFileKey.getBytes());
                outStream.write(payload.get(i).getOriginalFilename().getBytes());
                outStream.write("\n".getBytes());
            }
            outStream.write("messageid=".getBytes());
            outStream.write(messageInfo.getMessageId().getBytes());
            outStream.write("\n".getBytes());
            outStream.write("sender=".getBytes());
            outStream.write(sender.getName().getBytes());
            outStream.write("\n".getBytes());
            outStream.write("senderAS2Id=".getBytes());
            outStream.write(sender.getAS2Identification().getBytes());
            outStream.write("\n".getBytes());
            outStream.write("receiver=".getBytes());
            outStream.write(receiver.getName().getBytes());
            outStream.write("\n".getBytes());
            outStream.write("receiverAS2Id=".getBytes());
            outStream.write(receiver.getAS2Identification().getBytes());
            outStream.write("\n".getBytes());
            outStream.write("state=".getBytes());
            if (messageInfo.getState() == AS2Message.STATE_FINISHED) {
                outStream.write("OK".getBytes());
            } else {
                outStream.write("ERROR".getBytes());
            }
        }
        this.logger.log(Level.FINE, rb.getResourceString("outboundstatus.written",
                new Object[]{
                    statusFile.toAbsolutePath().toString()
                }), messageInfo);
    }

}
