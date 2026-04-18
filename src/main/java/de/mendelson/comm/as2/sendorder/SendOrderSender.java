package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageCreation;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;

import java.nio.file.Path;
import java.util.MissingResourceException;
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
 * Sender class that enqueues send orders
 *
 * @author S.Heller
 * @version $Revision: 30 $
 */
public class SendOrderSender {

    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSendOrderSender.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private final SendOrderQueueInterface queue; // Use interface instead of concrete class
    private final IDBDriverManager dbDriverManager;

    public SendOrderSender(SendOrderQueueInterface queue, IDBDriverManager dbDriverManager) {
        this.queue = queue;
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * @deprecated Use sendWithResult() instead. This method returns null on success for IN_MEMORY strategy.
     * @return AS2Message for PERSISTENT strategy, null for IN_MEMORY strategy or error
     */
    @Deprecated
    public AS2Message send(CertificateManager certificateManager, Partner sender,
            Partner receiver, Path[] files, String[] originalFilenames, String userdefinedId,
            String subject, String[] payloadContentTypes) {
        SendResult result = this.sendWithResult(certificateManager, sender, receiver, files,
                originalFilenames, userdefinedId, subject, payloadContentTypes, -1);
        return result.getMessage(); // Returns null for IN_MEMORY (confusing legacy behavior)
    }

    /**
     * @deprecated Use sendWithResult() instead. This method returns null on success for IN_MEMORY strategy.
     * @return AS2Message for PERSISTENT strategy, null for IN_MEMORY strategy or error
     */
    @Deprecated
    public AS2Message send(CertificateManager certificateManager, Partner sender,
            Partner receiver, Path[] files, String[] originalFilenames, String userdefinedId,
            String subject, String[] payloadContentTypes, int userId) {
        SendResult result = this.sendWithResult(certificateManager, sender, receiver, files,
                originalFilenames, userdefinedId, subject, payloadContentTypes, userId);
        return result.getMessage(); // Returns null for IN_MEMORY (confusing legacy behavior)
    }

    /**
     * Creates and sends an AS2 message with userId for HTTP auth preferences.
     * Returns a SendResult object that clearly indicates success/failure.
     *
     * @return SendResult containing success status, message (if available), and error details
     */
    public SendResult sendWithResult(CertificateManager certificateManager, Partner sender,
            Partner receiver, Path[] files, String[] originalFilenames, String userdefinedId,
            String subject, String[] payloadContentTypes, int userId) {
        try {
            long startProcessTime = System.currentTimeMillis();

            // Check if we're using PERSISTENT or IN_MEMORY strategy
            boolean isPersistentStrategy = (queue instanceof PersistentSendOrderQueue);

            if (isPersistentStrategy) {
                // PERSISTENT strategy: pre-build message
                System.out.println("DEBUG [SendOrderSender]: PERSISTENT strategy - building message");
                AS2MessageCreation messageCreation = new AS2MessageCreation(certificateManager, certificateManager);
                messageCreation.setLogger(this.logger);
                messageCreation.setServerResources(this.dbDriverManager);
                AS2Message message = messageCreation.createMessage(sender, receiver,
                            files, originalFilenames, userdefinedId, subject, payloadContentTypes);
                StringBuilder filenames = new StringBuilder();
                for (Path file : files) {
                    if (filenames.length() > 0) {
                        filenames.append(", ");
                    }
                    filenames.append(file.getFileName().toString());
                }
                this.logger.log(Level.INFO,
                        rb.getResourceString("message.packed",
                                new Object[]{
                                    filenames.toString(),
                                    receiver.getName(),
                                    AS2Tools.getDataSizeDisplay(message.getRawDataSize()),
                                    AS2Tools.getTimeDisplay(System.currentTimeMillis() - startProcessTime),
                                    (userdefinedId == null ? "--" : userdefinedId)
                                }),
                        message.getAS2Info());
                System.out.println("DEBUG [SendOrderSender]: Creating SendOrder for PERSISTENT");
                SendOrder sendOrder = new SendOrder()
                        .setReceiverDBId(receiver.getDBId())
                        .setSenderDBId(sender.getDBId())
                        .setMessage(message)
                        .setUserdefinedId(userdefinedId)
                        .setUserId(userId);
                ((PersistentSendOrderQueue) queue).enqueueWithMessage(sendOrder);
                return SendResult.successWithMessage(message);
            } else {
                // IN_MEMORY strategy: just enqueue metadata, message will be built on-demand
                System.out.println("DEBUG [SendOrderSender]: IN_MEMORY strategy - enqueueing metadata only");
                SendOrderRequest request = new SendOrderRequest()
                        .setSenderDBId(sender.getDBId())
                        .setReceiverDBId(receiver.getDBId())
                        .setFiles(files)
                        .setOriginalFilenames(originalFilenames)
                        .setUserdefinedId(userdefinedId)
                        .setSubject(subject)
                        .setPayloadContentTypes(payloadContentTypes)
                        .setUserId(userId);
                int orderId = queue.enqueue(request);
                this.logger.log(Level.INFO,
                        "Enqueued send order " + orderId + " for " + sender.getName() + " -> " + receiver.getName());
                return SendResult.successQueued(orderId);
            }
        } catch (Throwable e) {
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            logger.severe(rb.getResourceString("sendoder.sendfailed",
                    new Object[]{
                        e.getClass().getSimpleName(),
                        e.getMessage()
                    }
            ));
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
            return SendResult.failure(errorMsg);
        }
    }

    /**
     * Process a file object
     * http://tools.ietf.org/html/draft-meadors-multiple-attachments-ediint-08
     * 2.1: Multiple attachments in EDI-INT MUST NOT be used for batch
     * processing of EDI or other documents which are not inter-related. For
     * example numerous EDI purchase orders for different products must not be
     * sent in a multipart/related envelope but instead be transmitted in
     * separate, individual EDI-INT messages.
     *
     * That is the reason why the mendelson AS2 sends the AS2 messages using
     * single attachments, even if the software is capable of sending data using
     * the optional profile MA - multiple attachments.
     *
     *
     * between
     * https://tools.ietf.org/html/draft-meadors-multiple-attachments-ediint-10
     * and
     * https://tools.ietf.org/html/draft-meadors-multiple-attachments-ediint-11
     * the "MUST" changed to "SHOULD" - which means that batch EDI data
     * processing is possible since then
     *
     * @param payloadContentTypes May be null - then the payload content type
     * defined in the receiver is taken
     * @return NULL in the case of an error
     */
    public AS2Message send(CertificateManager certificateManager, Partner sender,
            Partner receiver, Path file, String userdefinedId, String subject, String[] payloadContentTypes) {
        return (this.send(certificateManager, sender, receiver, new Path[]{file}, null, userdefinedId,
                subject, payloadContentTypes));
    }

    /**
     * Enqueues an existing send order
     */
    public void resend(SendOrder order, long nextExecutionTime) {
        if (queue instanceof PersistentSendOrderQueue) {
            ((PersistentSendOrderQueue) queue).rescheduleOrder(order, nextExecutionTime);
        } else {
            // For IN_MEMORY strategy, this shouldn't be called since SendOrderReceiver
            // now calls queue.requeueForRetry() directly
            logger.warning("resend() called on IN_MEMORY strategy - should use requeueForRetry instead");
        }
    }

    /**
     * Enqueues an existing send order (PERSISTENT strategy specific)
     * Public for use by AS2ServerProcessing and other components
     */
    public void enqueuePersistentOrder(SendOrder order) {
        if (queue instanceof PersistentSendOrderQueue) {
            ((PersistentSendOrderQueue) queue).enqueueWithMessage(order);
        } else {
            logger.warning("enqueuePersistentOrder() called on IN_MEMORY strategy - not supported");
        }
    }

    /**
     * Enqueues a send order (DEPRECATED - use interface methods directly)
     */
    public void send(SendOrder order) {
        if (queue instanceof PersistentSendOrderQueue) {
            ((PersistentSendOrderQueue) queue).enqueueWithMessage(order);
        } else {
            logger.warning("send(SendOrder) called on IN_MEMORY strategy - not supported");
        }
    }
}
