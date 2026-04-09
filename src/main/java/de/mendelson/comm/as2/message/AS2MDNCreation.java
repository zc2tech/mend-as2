package de.mendelson.comm.as2.message;

import de.mendelson.comm.as2.AS2Exception;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.BCCryptoHelper;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Packs a message with all necessary headers and attachments
 *
 * @author S.Heller
 * @version $Revision: 55 $
 */
public class AS2MDNCreation {

    private Logger logger = null;
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2MessagePacker.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private final CertificateManager certificateManager;

    public AS2MDNCreation(CertificateManager certificateManager) {
        this.certificateManager = certificateManager;
    }

    /**
     * Build the header for the sync response and returns them
     */
    public Properties buildHeaderForSyncMDN(AS2Message message) {
        String ediintFeatures = "multiple-attachments, CEM";
        AS2MDNInfo info = (AS2MDNInfo) message.getAS2Info();
        Properties header = new Properties();
        header.setProperty("server", AS2ServerVersion.getUserAgent());
        header.setProperty("as2-version", "1.2");
        header.setProperty("ediint-features", ediintFeatures);
        header.setProperty("mime-version", "1.0");
        header.setProperty("message-id", "<" + info.getMessageId() + ">");
        //the data header must be always in english locale else there would be special
        //french characters (e.g. 13 déc. 2011 16:28:56 CET) which is not allowed after 
        //RFC 4130           
        DateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz", Locale.US);
        header.setProperty("date", format.format(new Date()));
        header.setProperty("connection", "close");
        if (info.getReceiverId() != null) {
            header.setProperty("as2-to", AS2Message.escapeFromToHeader(info.getReceiverId()));
        }
        if (info.getSenderId() != null) {
            header.setProperty("as2-from", AS2Message.escapeFromToHeader(info.getSenderId()));
        }
        header.setProperty("content-type", message.getContentType());
        header.setProperty("content-length", String.valueOf(message.getRawDataSize()));
        return (header);
    }

    /**
     * Creates an mdn that could be returned to the sender and indicates that
     * everything is ok
     */
    public AS2Message createMDNProcessed(AS2MessageInfo releatedMessageInfo, Partner mdnSender, Partner mdnReceiver) throws Exception {
        return (this.createMDNProcessed(releatedMessageInfo, mdnSender, mdnReceiver,
                MDNText.get(MDNText.RECEIVED, releatedMessageInfo.getMessageType())));
    }

    /**
     * Creates an mdn that could be returned to the sender and indicates that
     * everything is ok
     */
    public AS2Message createMDNProcessed(AS2MessageInfo releatedMessageInfo, Partner mdnSender, Partner mdnReceiver, String detailText) throws Exception {
        AS2Message mdn = this.createMDN(releatedMessageInfo, mdnSender, mdnSender.getAS2Identification(),
                mdnReceiver, mdnReceiver.getAS2Identification(), "processed", detailText);
        mdn.getAS2Info().setState(AS2Message.STATE_FINISHED);
        return (mdn);
    }

    /**
     * Creates an mdn that could be returned to the sender and indicates an
     * error by processing the message
     *
     * @param as2MessageSender might be null
     * @param as2MessageReceiver might be null
     */
    public AS2Message createMDNError(AS2Exception exception, Partner as2MessageSender, String as2MessageSenderId,
            Partner as2MessageReceiver, String as2MessageReceiverId) throws Exception {
        AS2MessageInfo messageInfoOfException = (AS2MessageInfo) exception.getAS2Message().getAS2Info();
        AS2Message mdn = this.createMDN(messageInfoOfException, as2MessageReceiver, as2MessageReceiverId,
                as2MessageSender, as2MessageSenderId, "processed/error: " + exception.getErrorType(),
                MDNText.get(MDNText.ERROR, messageInfoOfException.getMessageType()) + exception.getMessage());
        if (this.logger != null) {
            this.logger.log(Level.SEVERE, rb.getResourceString("mdn.details",
                    new Object[]{
                        exception.getMessage()
                    }), messageInfoOfException);
        }
        mdn.getAS2Info().setState(AS2Message.STATE_STOPPED);
        return (mdn);
    }

    /**
     * Creates a MDN to return. It may be confusing that the sender and the
     * sender id is passed but the sender is null if the partner with the sender
     * id has not been found in the db
     *
     * @param dispositionState State that will be written into the disposition
     * header
     * @param receiver might be null
     * @param sender might be null
     */
    private AS2Message createMDN(AS2MessageInfo relatedMessageInfo, Partner sender,
            String senderAS2Id, Partner receiver, String receiverAS2Id, String dispositionState,
            String additionalText) throws Exception {
        AS2Message message = new AS2Message(new AS2MDNInfo());
        AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
        mdnInfo.setMessageId(UniqueId.createMessageId(senderAS2Id, receiverAS2Id));
        mdnInfo.setDispositionState(dispositionState);
        if (this.logger != null) {
            this.logger.log(Level.FINE, rb.getResourceString("mdn.creation.start",
                    new Object[]{
                        mdnInfo.getMessageId()
                    }),
                    relatedMessageInfo);
        }
        mdnInfo.setSenderId(senderAS2Id);
        mdnInfo.setReceiverId(receiverAS2Id);
        mdnInfo.setRelatedMessageId(relatedMessageInfo.getMessageId());
        try {
            mdnInfo.setSenderHost(InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException e) {
            //nop
        }
        //Other possible value is "base64"
        String contentTransferEncoding = "7bit";        
        MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(this.createMDNNotesBody(additionalText, contentTransferEncoding));
        multiPart.addBodyPart(this.createMDNDispositionBody(relatedMessageInfo, dispositionState, contentTransferEncoding));
        multiPart.setSubType("report; report-type=disposition-notification");
        MimeMessage messagePart = new MimeMessage(Session.getInstance(System.getProperties(), null));
        messagePart.setContent(multiPart, MimeUtility.unfold(multiPart.getContentType()));
        messagePart.saveChanges();
        try (ByteArrayOutputStream memOutUnsigned = new ByteArrayOutputStream()) {
            //normally the content type header is folded (which is correct but some products are not able to parse this properly)
            //Now take the content-type, unfold it and write it
            Enumeration<String> hdrLines = messagePart.getMatchingHeaderLines(new String[]{"Content-Type"});
            while (hdrLines.hasMoreElements()) {
                //requires java mail API >= 1.4
                String nextHeaderLine = MimeUtility.unfold(hdrLines.nextElement());
                memOutUnsigned.write((nextHeaderLine + "\r\n").getBytes("US-ASCII"));
            }
            messagePart.writeTo(memOutUnsigned,
                    new String[]{"Message-ID", "Mime-Version", "Content-Type"});
            message.setDecryptedRawData(memOutUnsigned.toByteArray());
        }
        //check if authentification of sender is ok, then sign if possible
        if (sender != null && receiver != null) {
            MimeMessage signedMessage = this.signMDN(messagePart, sender, receiver, message, relatedMessageInfo);
            message.setContentType(MimeUtility.unfold(signedMessage.getContentType()));
            try (ByteArrayOutputStream memOutSigned = new ByteArrayOutputStream()) {
                signedMessage.writeTo(memOutSigned,
                        new String[]{"Message-ID", "Mime-Version", "Content-Type"});
                message.setRawData(memOutSigned.toByteArray());
            }
        } //there occured an authentification error: the system was unable to authentificate the sender,
        //do not sign MDN
        else {
            try (ByteArrayOutputStream memOut = new ByteArrayOutputStream()) {
                messagePart.writeTo(memOut,
                        new String[]{"Message-ID", "Mime-Version", "Content-Type"});
                message.getAS2Info().setSignType(AS2Message.SIGNATURE_NONE);
                message.setContentType(MimeUtility.unfold(messagePart.getContentType()));
                message.setRawData(memOut.toByteArray());
            }
        }
        if (dispositionState.contains("error")) {
            if (this.logger != null) {
                this.logger.log(Level.SEVERE, rb.getResourceString("mdn.created",
                        new Object[]{
                            mdnInfo.getRelatedMessageId(), dispositionState
                        }), mdnInfo);
            }
        } else {
            if (this.logger != null) {
                this.logger.log(Level.FINE, rb.getResourceString("mdn.created",
                        new Object[]{
                            mdnInfo.getRelatedMessageId(), dispositionState
                        }), mdnInfo);
            }
        }
        return (message);
    }

    /**
     * Its necessary to transmit additional notes
     */
    private MimeBodyPart createMDNNotesBody(String text, String contentTransferEncoding) throws MessagingException {
        MimeBodyPart body = new MimeBodyPart();
        body.setDataHandler(new DataHandler(new ByteArrayDataSource(text.getBytes(), "text/plain")));
        body.setHeader("Content-Type", "text/plain");
        body.setHeader("Content-Transfer-Encoding", contentTransferEncoding);
        return (body);
    }

    /**
     * Creates the MDN body and returns it
     *
     */
    private MimeBodyPart createMDNDispositionBody(AS2MessageInfo relatedMessageInfo, String dispositionState,
            String contentTransferEncoding) throws MessagingException {
        MimeBodyPart body = new MimeBodyPart();
        StringBuilder builder = new StringBuilder();
        builder.append("Reporting-UA: ").append(AS2ServerVersion.getProductName()).append("\r\n");
        builder.append("Original-Recipient: rfc822; ").append(relatedMessageInfo.getReceiverId()).append("\r\n");
        builder.append("Final-Recipient: rfc822; ").append(relatedMessageInfo.getReceiverId()).append("\r\n");
        builder.append("Original-Message-ID: <").append(relatedMessageInfo.getMessageId()).append(">\r\n");
        builder.append("Disposition: automatic-action/MDN-sent-automatically; ").append(dispositionState).append("\r\n");
        if (relatedMessageInfo.getReceivedContentMIC() != null) {
            builder.append("Received-Content-MIC: ").append(relatedMessageInfo.getReceivedContentMIC()).append("\r\n");
        }
        body.setDataHandler(new DataHandler(new ByteArrayDataSource(builder.toString().getBytes(),
                "message/disposition-notification")));
        body.setHeader("Content-Transfer-Encoding", contentTransferEncoding);
        return (body);
    }

    /**
     * Signs the passed mdn and returns it if this is requested by the inbound
     * AS2 message
     *
     * @param remotePartner might be null if the receiver is unidentified
     */
    private MimeMessage signMDN(MimeMessage mimeMessage, Partner localPartner, Partner remotePartner,
            AS2Message as2Message, AS2MessageInfo relatedMessageInfo) throws Exception {
        if (relatedMessageInfo.getDispositionNotificationOptions().signMDN()) {
            int preferredDigestDispositionNotification = relatedMessageInfo.getDispositionNotificationOptions().getPreferredSignatureAlgorithm();
            int relatedMessageDigest = relatedMessageInfo.getSignType();
            //The preferred sign digest from the disposition notification option does not contain the signature scheme. Means if the sender used
            //a special signature scheme this should be used for the MDN signature, too - even if it is impossible to signal this
            //by the dispositoin notification option.
            if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA1 && relatedMessageDigest == AS2Message.SIGNATURE_SHA1_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA1_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA224 && relatedMessageDigest == AS2Message.SIGNATURE_SHA224_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA224_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA256 && relatedMessageDigest == AS2Message.SIGNATURE_SHA256_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA256_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA384 && relatedMessageDigest == AS2Message.SIGNATURE_SHA384_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA384_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA512 && relatedMessageDigest == AS2Message.SIGNATURE_SHA512_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA512_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_224 && relatedMessageDigest == AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_256 && relatedMessageDigest == AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_384 && relatedMessageDigest == AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_512 && relatedMessageDigest == AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS) {
                preferredDigestDispositionNotification = AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS;
            }
            as2Message.getAS2Info().setSignType(preferredDigestDispositionNotification);
            String digestStr = null;
            if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_MD5) {
                digestStr = BCCryptoHelper.ALGORITHM_MD5;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA1) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA1;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA1_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA_1_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA224) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA224;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA224_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA_224_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA256) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA256;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA256_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA_256_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA384) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA384;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA384_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA_384_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA512) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA512;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA512_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA_512_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_224) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_224;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_256) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_256;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_384) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_384;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_512) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_512;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_224_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_256_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_384_RSASSA_PSS;
            } else if (preferredDigestDispositionNotification == AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS) {
                digestStr = BCCryptoHelper.ALGORITHM_SHA3_512_RSASSA_PSS;
            }
            if (digestStr == null) {
                as2Message.getAS2Info().setSignType(AS2Message.SIGNATURE_NONE);
                if (this.logger != null) {
                    this.logger.log(Level.INFO, rb.getResourceString("mdn.notsigned",
                            new Object[]{
                                as2Message.getAS2Info().getMessageId(),}), as2Message.getAS2Info());
                }
                return (mimeMessage);
            }
            String signFingerprintSHA1;
            if (remotePartner != null && remotePartner.isOverwriteLocalStationSecurity()
                    && remotePartner.getSignOverwriteLocalstationFingerprintSHA1() != null) {
                signFingerprintSHA1 = remotePartner.getSignOverwriteLocalstationFingerprintSHA1();
            } else {
                signFingerprintSHA1 = localPartner.getSignFingerprintSHA1();
            }
            if (signFingerprintSHA1 == null) {
                throw new Exception("AS2MDNCreation.signMDN: No private key defined to sign outbound MDN");
            }
            PrivateKey senderKey = this.certificateManager.getPrivateKeyByFingerprintSHA1(signFingerprintSHA1);
            String senderSignAlias = this.certificateManager.getAliasByFingerprint(signFingerprintSHA1);
            Certificate[] chain = this.certificateManager.getCertificateChain(senderSignAlias);
            BCCryptoHelper helper = new BCCryptoHelper();
            boolean useAlgorithmIdentifierProtectionAttribute = true;
            if (remotePartner != null) {
                useAlgorithmIdentifierProtectionAttribute = remotePartner.getUseAlgorithmIdentifierProtectionAttribute();
                if (!useAlgorithmIdentifierProtectionAttribute && this.logger != null) {
                    this.logger.log(Level.INFO, rb.getResourceString("signature.no.aipa",
                            new Object[]{
                                as2Message.getAS2Info().getMessageId(),}), as2Message.getAS2Info());
                }
            }
            MimeMessage signedMimeMessage = helper.signToMessage(mimeMessage, chain, senderKey, digestStr.toUpperCase(),
                    useAlgorithmIdentifierProtectionAttribute,
                    AS2Server.CRYPTO_PROVIDER.getProviderEncSign().getProvider().getName());
            if (this.logger != null) {
                this.logger.log(Level.INFO, rb.getResourceString("mdn.signed",
                        new Object[]{
                            digestStr.toUpperCase(),
                            senderSignAlias,
                            localPartner.getName()
                        }), as2Message.getAS2Info());
            }
            return (signedMimeMessage);
        } else {
            as2Message.getAS2Info().setSignType(AS2Message.SIGNATURE_NONE);
            if (this.logger != null) {
                this.logger.log(Level.INFO, rb.getResourceString("mdn.notsigned",
                        new Object[]{
                            as2Message.getAS2Info().getMessageId(),}), as2Message.getAS2Info());
            }
            return (mimeMessage);
        }
    }

    /**
     * @param logger the logger to set. If no logger is passed to this class
     * there will be no logging
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
