package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.postprocessingevent.ProcessingEvent;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.send.HttpConnectionParameter;
import de.mendelson.comm.as2.send.MessageHttpUploader;
import de.mendelson.comm.as2.send.NoConnectionException;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.EventBus;
import de.mendelson.comm.as2.tracker.PayloadAnalyzer;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2Util;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Receiver class that reads the enqueued send orders and starts the AS2 message
 * send process for each message
 *
 * @author S.Heller
 * @version $Revision: 56 $
 */
public class SendOrderReceiver {

    private final static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final MecResourceBundle rb;
    private final SendOrderQueueInterface queue; // Use interface instead of concrete class
    private final ClientServer clientserver;
    private final PreferencesAS2 preferences;
    private final MessageStoreHandler messageStoreHandler;
    private final MessageAccessDB messageAccess;
    private final IDBDriverManager dbDriverManager;
    private final PartnerAccessDB partnerAccess; // Need for reloading partners
    private final de.mendelson.util.security.cert.MultiUserCertificateManager multiUserCertificateManager;
    private SendOrderReceiverThread sendOrderReceiverThread = null;
    private final ScheduledExecutorService scheduledExecutor
            = Executors.newSingleThreadScheduledExecutor(
                    new NamedThreadFactory("sendorder-receiver"));

    public SendOrderReceiver(SendOrderQueueInterface queue, ClientServer clientserver,
                           IDBDriverManager dbDriverManager,
                           de.mendelson.util.security.cert.MultiUserCertificateManager multiUserCertificateManager) throws Exception {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSendOrderReceiver.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.dbDriverManager = dbDriverManager;
        this.queue = queue; // Use injected queue
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        this.messageAccess = new MessageAccessDB(dbDriverManager);
        this.messageStoreHandler = new MessageStoreHandler(dbDriverManager);
        this.clientserver = clientserver;
        this.multiUserCertificateManager = multiUserCertificateManager;
        preferences = new PreferencesAS2(dbDriverManager);
    }

    public void execute() {
        this.sendOrderReceiverThread = new SendOrderReceiverThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.sendOrderReceiverThread, 1, 5, TimeUnit.SECONDS);
    }

    public class SendOrderReceiverThread implements Runnable {

        private final ThreadPoolExecutor threadExecutor;
        private long lastConfigCheckTime = System.currentTimeMillis();
        private int maxOutboundConnections = 0;
        private final AtomicInteger activeConnections = new AtomicInteger(0);

        public SendOrderReceiverThread() {
            this.maxOutboundConnections = preferences.getInt(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS);
            if (maxOutboundConnections == 0) {
                logger.config(rb.getResourceString("as2.send.disabled"));
            }
            //Using a synchronous queue for the number of parallel connections. As the queue will block for 
            //each put a new thread will be created for every outbound connection
            SynchronousQueue<Runnable> syncQueue = new SynchronousQueue<Runnable>();
            //
            //If the number of threads is less than the corePoolSize, create a new Thread to run a new task.
            //If the number of threads is equal (or greater than) the corePoolSize, put the task into the queue.
            //If the queue is full, and the number of threads is less than the maxPoolSize, create a new thread to run tasks in.
            //If the queue is full, and the number of threads is greater than or equal to maxPoolSize, reject the task.
            //--as this uses a sync queue which will always block until taken a new thread is created for every execute!
            //Unused threads will be killed after 30s once they are idle
            this.threadExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE,
                    30, TimeUnit.SECONDS, syncQueue,
                    new NamedThreadFactory("sendorder-processing")) {
                /**
                 * Reduce the number of active connections after processing a
                 * send order
                 */
                @Override
                protected void afterExecute(Runnable runnable, Throwable exception) {
                    super.afterExecute(runnable, exception);
                    activeConnections.decrementAndGet();
                }
            };
        }

        @Override
        public void run() {
            //this try is necessary because this thread must never stop. If it stops no more messages
            //and MDN are send!
            //transactional connection to the database just to read the data from the poll queue - no auto commit
            final List<SendOrderItem> waitingOrders = new ArrayList<SendOrderItem>();
            //collect the waiting orders
            try {
                this.detectModification();
                //check if new outbound connection are currently possible. The Math.max value is taken because its possible that
                //the number of active connections is already reduced in the afterExecute method of the queue but the thread does still exist.
                int possibleNewConnections = this.maxOutboundConnections - Math.max(activeConnections.get(),
                        threadExecutor.getActiveCount());

                if (possibleNewConnections > 0) {
                    //Get max number of outbound send orders and pass them to the thread executor
                    waitingOrders.addAll(queue.dequeueAvailable(possibleNewConnections));
                } else {
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e);
            }
            //process the found orders
            try {
                for (SendOrderItem order : waitingOrders) {
                    final int activeConnectionNumber = activeConnections.incrementAndGet();
                    final SendOrderItem finalOrder = order;
                    final int finalMaxOutboundConnectionCount = maxOutboundConnections;
                    Runnable connectionRunner = new Runnable() {
                        @Override
                        public void run() {
                            processOrder(finalOrder, finalMaxOutboundConnectionCount, activeConnectionNumber);
                        }
                    };
                    threadExecutor.execute(connectionRunner);
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.instance().systemFailure(e);
            }

        }

        /**
         * Computes if the user performed a modification of the number of max
         * outbound parallel connections. The modification detection is just
         * executed all 10s
         *
         */
        private void detectModification() {
            //check for a configuration change - if the user changed the number of outbound connections
            //that has to be computed. This check will just happen from time to time
            if (System.currentTimeMillis() - this.lastConfigCheckTime > TimeUnit.SECONDS.toMillis(10)) {
                //check if the user has changed the outbound connection settings
                int maxOutboundConnectionsNew = preferences.getInt(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS);
                if (maxOutboundConnectionsNew != this.maxOutboundConnections) {
                    try {
                        this.maxOutboundConnections = maxOutboundConnectionsNew;
                        if (this.maxOutboundConnections == 0) {
                            logger.config(rb.getResourceString("as2.send.disabled"));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                if (activeConnections.get() > this.maxOutboundConnections) {
                    logger.config(rb.getResourceString("send.connectionsstillopen",
                            new Object[]{
                                String.valueOf(this.maxOutboundConnections),
                                String.valueOf(activeConnections)
                            }));
                }
                this.lastConfigCheckTime = System.currentTimeMillis();
            }
        }

        /**
         * Processes a single send order
         */
        private void processOrder(SendOrderItem item,
                int maxOutboundConnectionsCount,
                int activeConnectionsCount) {
            AS2Message message = null;
            Partner sender = null;
            Partner receiver = null;

            try {
                // Step 1: Build AS2Message if needed (IN_MEMORY strategy)
                // or use pre-built message (PERSISTENT strategy)
                if (item.hasPrebuiltMessage()) {
                    // PERSISTENT strategy: message already built
                    message = item.getMessage();

                    // Reload partners from DB (deprecated transient fields are null)
                    sender = partnerAccess.getPartner(item.getSenderDBId());
                    receiver = partnerAccess.getPartner(item.getReceiverDBId());

                } else {
                    // IN_MEMORY strategy: build message on-demand OR use cached message for retries
                    logger.info("═════════════════════════════════════════════════");
                    logger.info("[SENDORDER-DEBUG] Processing IN_MEMORY send order:");
                    logger.info("[SENDORDER-DEBUG]   Order ID: " + item.getOrderId());
                    logger.info("[SENDORDER-DEBUG]   Sender DB ID: " + item.getSenderDBId());
                    logger.info("[SENDORDER-DEBUG]   Receiver DB ID: " + item.getReceiverDBId());
                    logger.info("[SENDORDER-DEBUG]   User ID: " + item.getUserId());
                    logger.info("[SENDORDER-DEBUG]   Retry count: " + item.getRetryCount());
                    logger.info("[SENDORDER-DEBUG]   Files: " + (item.getFiles() != null ? item.getFiles().length : 0));
                    logger.info("═════════════════════════════════════════════════");

                    // Load partners
                    sender = partnerAccess.getPartner(item.getSenderDBId());
                    receiver = partnerAccess.getPartner(item.getReceiverDBId());

                    if (sender == null || receiver == null) {
                        throw new Exception("Could not load sender or receiver partner from database " +
                                          "(senderDBId=" + item.getSenderDBId() +
                                          ", receiverDBId=" + item.getReceiverDBId() + ")");
                    }

                    logger.info("[SENDORDER-DEBUG] Partners loaded:");
                    logger.info("[SENDORDER-DEBUG]   Sender: " + sender.getName() + " (AS2 ID: " + sender.getAS2Identification() + ")");
                    logger.info("[SENDORDER-DEBUG]   Receiver: " + receiver.getName() + " (AS2 ID: " + receiver.getAS2Identification() + ")");
                    logger.info("[SENDORDER-DEBUG]   Sender sign cert fingerprint: " + sender.getSignFingerprintSHA1());
                    logger.info("[SENDORDER-DEBUG]   Receiver crypt cert fingerprint: " + receiver.getCryptFingerprintSHA1());

                    // Check if this is a retry with cached message
                    if (item.getRetryCount() > 0 && item.getCachedMessage() != null) {
                        // RETRY: Reuse cached message (same message ID, same encrypted bytes)
                        message = item.getCachedMessage();
                        logger.info("[SENDORDER-DEBUG] ✓ Reusing cached AS2Message for retry (message ID: " +
                                   message.getAS2Info().getMessageId() + ")");
                    } else {
                        // FIRST ATTEMPT: Build new AS2Message
                        logger.info("[SENDORDER-DEBUG] Building new AS2Message (first attempt)");

                        de.mendelson.comm.as2.message.AS2MessageCreationAdapter messageCreationAdapter =
                            new de.mendelson.comm.as2.message.AS2MessageCreationAdapter(
                                SendOrderReceiver.this.multiUserCertificateManager,
                                logger
                            );
                        messageCreationAdapter.setServerResources(dbDriverManager);

                        message = messageCreationAdapter.createMessage(
                            sender, receiver,
                            item.getFiles(),
                            item.getOriginalFilenames(),
                            item.getUserdefinedId(),
                            item.getSubject(),
                            item.getPayloadContentTypes(),
                            item.getUserId()  // Use user ID from send order
                        );

                        // Extract payload format and document type from raw file BEFORE encryption
                        if (message != null && item.getFiles() != null && item.getFiles().length > 0) {
                            try {
                                AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                                // Read only first 4KB of the first raw payload file for analysis
                                java.nio.file.Path firstFile = item.getFiles()[0];
                                byte[] payloadData;
                                try (java.io.InputStream is = java.nio.file.Files.newInputStream(firstFile)) {
                                    payloadData = is.readNBytes(4096);
                                }
                                if (payloadData != null && payloadData.length > 0) {
                                    PayloadAnalyzer.PayloadAnalysis analysis = PayloadAnalyzer.analyze(payloadData);
                                    messageInfo.setPayloadFormat(analysis.getFormat());
                                    messageInfo.setPayloadDocType(analysis.getDocumentType());
                                }
                            } catch (Exception e) {
                                logger.warning("Failed to analyze payload for outgoing message: " + e.getMessage());
                                // Continue - this is not a critical failure
                            }
                        }

                        // Cache the message for future retries
                        item.setCachedMessage(message);
                        queue.updateCachedMessage(item.getOrderId(), message);

                        logger.info("[SENDORDER-DEBUG] ✓ AS2Message created and cached (message ID: " +
                                   message.getAS2Info().getMessageId() + ")");

                        // Clean up uploaded files after successful message creation
                        // Files are no longer needed - the encrypted message is now cached
                        if (item.getFiles() != null) {
                            for (java.nio.file.Path uploadedFile : item.getFiles()) {
                                try {
                                    // Only delete files from webui uploads (temp directory)
                                    if (uploadedFile != null &&
                                        uploadedFile.getFileName().toString().startsWith("webui_upload_")) {
                                        java.nio.file.Files.deleteIfExists(uploadedFile);
                                        logger.info("[SENDORDER-DEBUG] Cleaned up uploaded file: " + uploadedFile.getFileName());
                                    }
                                } catch (Exception cleanupEx) {
                                    logger.warning("[SENDORDER-DEBUG] Failed to cleanup uploaded file: " +
                                                  uploadedFile.getFileName() + " - " + cleanupEx.getMessage());
                                }
                            }
                        }
                    }
                }

                if (sender == null || receiver == null || message == null) {
                    throw new Exception("Failed to prepare message for sending");
                }

                boolean processingAllowed = true;
                //before performing the send there has to be checked if the send process is still valid. The orders
                //are queued, between scheduling and processing the orders the transmission time could expire
                //or the user could cancel it
                if (message.isMDN()) {
                    //if the MDN state is on failure then the related transmission is on failure state, too -
                    //checking this makes no sense here
                    AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                    AS2MessageInfo relatedMessageInfo = messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                    if (relatedMessageInfo == null) {
                        processingAllowed = false;
                    }
                } else {
                    AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                    if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_AS2) {
                        //update the message info from the database
                        messageInfo = messageAccess.getLastMessageEntry(messageInfo.getMessageId());
                        if (messageInfo == null || messageInfo.getState() == AS2Message.STATE_STOPPED) {
                            processingAllowed = false;
                        }
                    } else if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                        processingAllowed = true;
                    }
                }
                if (processingAllowed) {
                    //display some log information that the outbound connection is prepared
                    if (message.isMDN()) {
                        AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                        AS2MessageInfo relatedMessageInfo = messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                        String asyncMDNURL = relatedMessageInfo.getAsyncMDNURL();
                        logger.log(Level.INFO, rb.getResourceString("outbound.connection.prepare.mdn",
                                new Object[]{
                                    asyncMDNURL,
                                    String.valueOf(activeConnectionsCount),
                                    String.valueOf(maxOutboundConnectionsCount),}), mdnInfo);
                    } else {
                        //its a AS2 message that has been sent
                        AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                        if (receiver.getURL().toLowerCase().startsWith("https")) {
                            messageInfo.setUsesTLS(true);
                        }
                        messageAccess.initializeOrUpdateMessage(messageInfo);
                        logger.log(Level.INFO, rb.getResourceString("outbound.connection.prepare.message",
                                new Object[]{
                                    receiver.getURL(),
                                    String.valueOf(activeConnectionsCount),
                                    String.valueOf(maxOutboundConnectionsCount),}), messageInfo);
                    }
                    //ensure that the OAuth2 access token is valid before starting the data upload
                    if (message.isMDN()) {
                        if (receiver.usesOAuth2MDN() && receiver.getOAuth2MDN() != null) {
                            OAuth2Util.ensureValidAccessToken(dbDriverManager,
                                    SystemEventManagerImplAS2.instance(),
                                    receiver.getOAuth2MDN());
                        }
                    } else {
                        if (receiver.usesOAuth2Message() && receiver.getOAuth2Message() != null) {
                            OAuth2Util.ensureValidAccessToken(dbDriverManager,
                                    SystemEventManagerImplAS2.instance(),
                                    receiver.getOAuth2Message());
                        }
                    }
                    MessageHttpUploader messageUploader = new MessageHttpUploader();
                    if (!preferences.getBoolean(PreferencesAS2.CEM)) {
                        messageUploader.setEDIINTFeatures("multiple-attachments");
                    } else {
                        messageUploader.setEDIINTFeatures("multiple-attachments, CEM");
                    }
                    messageUploader.setLogger(logger);
                    messageUploader.setAbstractServer(clientserver);
                    messageUploader.setDBConnection(dbDriverManager);
                    messageUploader.setUserId(item.getUserId());
                    //configure the connection parameters
                    HttpConnectionParameter connectionParameter = new HttpConnectionParameter();
                    connectionParameter.setConnectionTimeoutMillis(preferences.getInt(PreferencesAS2.HTTP_SEND_TIMEOUT));
                    connectionParameter.setTrustAllRemoteServerCertificates(preferences.getBoolean(PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES));
                    connectionParameter.setStrictHostCheck(preferences.getBoolean(PreferencesAS2.TLS_STRICT_HOST_CHECK));
                    connectionParameter.setHttpProtocolVersion(receiver.getHttpProtocolVersion());
                    connectionParameter.setProxy(messageUploader.createProxyObjectFromPreferences());
                    connectionParameter.setUseExpectContinue(true);
                    messageUploader.upload(connectionParameter,
                            message, sender, receiver);
                    //set error or finish state, remember that this send order could be
                    //also an MDN if async MDN is requested
                    if (message.isMDN()) {
                        AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                        if (mdnInfo.getState() == AS2Message.STATE_FINISHED) {
                            AS2MessageInfo relatedMessageInfo = messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                            messageStoreHandler.movePayloadToInbox(relatedMessageInfo.getMessageType(), mdnInfo.getRelatedMessageId(),
                                    sender, receiver);
                            //execute a shell command after send SUCCESS
                            ProcessingEvent.enqueueEventIfRequired(dbDriverManager,
                                    relatedMessageInfo, null);
                        }
                        //set the transaction state to the MDN state
                        messageAccess.setMessageState(mdnInfo.getRelatedMessageId(), mdnInfo.getState());
                    } else {
                        //its a AS2 message that has been sent
                        AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                        messageAccess.setMessageSendDate(messageInfo);
                        messageAccess.updateFilenames(messageInfo);
                        if (!messageInfo.requestsSyncMDN()) {
                            long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(preferences.getInt(PreferencesAS2.ASYNC_MDN_TIMEOUT));
                            DateFormat format = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT,
                                    DateFormat.MEDIUM);
                            logger.log(Level.INFO, rb.getResourceString("async.mdn.wait",
                                    new Object[]{
                                        format.format(endTime)
                                    }), messageInfo);
                        }
                    }
                }
                //Either it is processed now or the entry in the queue was no longer valid - delete it in both cases
                queue.markCompleted(item.getOrderId());
                //send push messages to all clients that the number/state of transaction has been changed
                EventBus.getInstance().publish(new RefreshClientMessageOverviewList());
            } catch (NoConnectionException e) {
                int retryCount = item.incRetryCount();
                int maxRetryCount = preferences.getInt(PreferencesAS2.MAX_CONNECTION_RETRY_COUNT);
                //to many retries: cancel the transaction
                if (retryCount > maxRetryCount) {
                    if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
                        logger.log(Level.WARNING, e.getMessage(), message != null ? message.getAS2Info() : null);
                    }
                    logger.log(Level.SEVERE, rb.getResourceString("max.retry.reached",
                            String.valueOf(maxRetryCount)), message != null ? message.getAS2Info() : null);
                    queue.markCompleted(item.getOrderId());
                    this.processUploadError(item, message);
                } else {
                    if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
                        logger.log(Level.WARNING, e.getMessage(), message != null ? message.getAS2Info() : null);
                    }
                    logger.log(Level.WARNING, rb.getResourceString("retry",
                            new Object[]{
                                String.valueOf(preferences.getInt(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S)),
                                String.valueOf(retryCount),
                                String.valueOf(maxRetryCount)
                            }), message != null ? message.getAS2Info() : null);
                    this.sendOrderToRetry(item);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, e.getMessage(), message != null ? message.getAS2Info() : null);
                this.processUploadError(item, message);
            }
        }

        /**
         * Update the order in the queue - with a new nextexecution time
         */
        private void sendOrderToRetry(SendOrderItem item) {
            long nextExecutionTime = System.currentTimeMillis() +
                TimeUnit.SECONDS.toMillis(preferences.getInt(PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S));
            queue.requeueForRetry(item.getOrderId(), nextExecutionTime - System.currentTimeMillis());
        }

        /**
         * The upload process of the data failed. Set the message state, execute
         * the command, ..
         */
        private void processUploadError(SendOrderItem item, AS2Message message) {
            try {
                if (message == null) {
                    logger.log(Level.SEVERE, "processUploadError: message is null");
                    return;
                }

                // Reload partners for storing error message
                Partner sender = partnerAccess.getPartner(item.getSenderDBId());
                Partner receiver = partnerAccess.getPartner(item.getReceiverDBId());

                //stores
                messageStoreHandler.storeSentErrorMessage(message, sender, receiver);
                if (!message.isMDN()) {
                    //message upload failure
                    messageAccess.setMessageState(message.getAS2Info().getMessageId(),
                            AS2Message.STATE_STOPPED);
                    //its important to set the state in the message info, too. An event exec is not performed
                    //for pending messages
                    message.getAS2Info().setState(AS2Message.STATE_STOPPED);
                    messageAccess.updateFilenames((AS2MessageInfo) message.getAS2Info());
                    ProcessingEvent.enqueueEventIfRequired(dbDriverManager,
                            (AS2MessageInfo) message.getAS2Info(), null);
                    //write status file
                    messageStoreHandler.writeOutboundStatusFile((AS2MessageInfo) message.getAS2Info());
                } else {
                    //MDN send failure, e.g. wrong URL for async MDN in message
                    messageAccess.setMessageState(((AS2MDNInfo) message.getAS2Info()).getRelatedMessageId(),
                            AS2Message.STATE_STOPPED);
                }
                EventBus.getInstance().publish(new RefreshClientMessageOverviewList());
            } catch (Exception ee) {
                ee.printStackTrace();
                logger.log(Level.SEVERE, "SendOrderReceiver.processUploadError(): " + ee.getMessage(),
                        message != null ? message.getAS2Info() : null);
                if (message != null) {
                    messageAccess.setMessageState(message.getAS2Info().getMessageId(), AS2Message.STATE_STOPPED);
                }
            }
        }
    }

}
