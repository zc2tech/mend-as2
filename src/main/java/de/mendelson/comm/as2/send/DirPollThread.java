package de.mendelson.comm.as2.send;

import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.EventBus;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.IOFileFilterRegexpMatch;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
 * Thread that polls a directory
 *
 * @author S.Heller
 * @version $Revision: 45 $
 */
public class DirPollThread implements Runnable {

    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDirPollManager.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    /**
     * Polls all 10s by default
     */
    private final long pollInterval;
    private boolean stopRequested = false;
    private final Partner receiver;
    private final Partner sender;
    private final IDBDriverManager dbDriverManager;
    private final static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final ClientServer clientserver;
    private final CertificateManager certificateManagerEncSign;
    private ScheduledFuture<?> future = null;
    private final ExecutorService sendingTheadExecutor
            = Executors.newFixedThreadPool(3,
                    new NamedThreadFactory("dirpoll-send"));
    /**
     * GUI preferences
     */
    private final PreferencesAS2 preferences;    

    public DirPollThread(IDBDriverManager dbDriverManager, ClientServer clientserver,
            CertificateManager certificateManagerEncSign, Partner sender, Partner receiver) {
        Objects.requireNonNull(sender, "DirPollThread: sender must not be null");
        Objects.requireNonNull(receiver, "DirPollThread: receiver must not be null");
        this.dbDriverManager = dbDriverManager;
        this.clientserver = clientserver;
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.preferences = new PreferencesAS2(dbDriverManager);
        //if something happens outside of this thread to the partners this should not result in weird problems
        this.receiver = (Partner) receiver.clone();
        this.sender = (Partner) sender.clone();
        //set the poll interval to a min value of 1s - even if the user requested 0. But in this case the CPU activity will go up to 100%
        this.pollInterval = Math.max(TimeUnit.SECONDS.toMillis(receiver.getPollInterval()), TimeUnit.SECONDS.toMillis(1));
    }

    public long getPollIntervalInMS() {
        return (this.pollInterval);
    }

    /**
     * Returns a line that describes this thread for the log
     */
    public String getLogLine() {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(this.sender.getName())
                .append(" -> ")
                .append(this.receiver.getName())
                .append("] ")
                .append(this.getMonitoredDirectory().toAbsolutePath().toString())
                .append(" (")
                .append(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.pollInterval)))
                .append("s)");
        return (builder.toString());
    }

    /**
     * Checks if the passed data is still the data that is stored in this thread
     */
    public boolean hasBeenModified(Partner newSender, Partner newReceiver) {
        //check for name changes
        //partner renamed, this results in a new poll directory
        if (this.receiver != null && this.sender != null && newSender != null && newReceiver != null) {
            if (!Partner.hasSameContent(this.sender, newSender, this.certificateManagerEncSign)) {
                logger.info(rb.getResourceString("poll.modified",
                        new Object[]{this.sender.getName(), this.receiver.getName()}));
                return (true);
            }
            if (!Partner.hasSameContent(this.receiver, newReceiver, this.certificateManagerEncSign)) {
                logger.info(rb.getResourceString("poll.modified",
                        new Object[]{this.sender.getName(), this.receiver.getName()}));
                return (true);
            }
        }
        return (false);
    }

    /**
     * Asks the thread to stop
     */
    public void requestStop() {
        this.stopRequested = true;
        logger.info(rb.getResourceString("poll.stopped",
                new Object[]{
                    this.sender.getName(),
                    this.receiver.getName()
                }));
        //remove this thread from it's executor service
        if (this.future == null) {
            logger.warning(rb.getResourceString("poll.stopped.notscheduled",
                    new Object[]{
                        this.sender.getName(),
                        this.receiver.getName()
                    }));
        } else {
            //remove this thread from the scheduled executor
            this.future.cancel(false);
        }
        this.sendingTheadExecutor.shutdown();
    }

    /**
     * Sets the necessary information to this thread to be canceled
     */
    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    /**
     * Builds up the directory that is monitored by this process
     */
    private Path getMonitoredDirectory() {
        return (Paths.get(this.preferences.get(PreferencesAS2.DIR_MSG),
                AS2Tools.convertToValidFilename(this.receiver.getName()),
                "outbox",
                AS2Tools.convertToValidFilename(this.sender.getName())));
    }

    /**
     * Initialize the poll thread. Its required to call this always before
     * scheduling/rescheduling the thread
     */
    public void initializeThread() {
        String pollIgnoreList = this.receiver.getPollIgnoreListAsString();
        if (pollIgnoreList == null) {
            pollIgnoreList = "--";
        }
        logger.info(rb.getResourceString("poll.started",
                new Object[]{
                    this.sender.getName(),
                    this.receiver.getName(),
                    pollIgnoreList,
                    this.receiver.getPollInterval()
                }
        ));
    }

    /**
     * Runs this thread
     */
    @Override
    public void run() {
        try {
            if (!stopRequested) {
                Path outboxDir = this.getMonitoredDirectory();
                boolean logPollProcess = this.preferences.getBoolean(PreferencesAS2.LOG_POLL_PROCESS);
                if (Files.notExists(outboxDir)) {
                    try {
                        Files.createDirectories(outboxDir);
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.instance().newEventExceptionInDirectoryCreation(e,
                                outboxDir.toAbsolutePath().toString());
                    }
                }
                IOFileFilterRegexpMatch fileFilter = new IOFileFilterRegexpMatch();
                if (this.receiver.getPollIgnoreList() != null) {
                    for (String ignoreEntry : this.receiver.getPollIgnoreList()) {
                        fileFilter.addNonMatchingPattern(ignoreEntry);
                    }
                }
                //if logging is requested log that the poll process will start
                if (logPollProcess) {
                    logger.log(Level.FINER, rb.getResourceString("poll.log.polling",
                            new Object[]{
                                this.sender.getName(), this.receiver.getName(),
                                outboxDir.toAbsolutePath().toString()
                            }));
                }
                try {
                    List<Path> files = this.listFilesNIO(outboxDir, fileFilter);
                    Collections.sort(files, new ComparatorFiledateOldestFirst());
                    List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
                    int fileCounter = 0;
                    for (Path file : files) {
                        //take a defined max number of files per poll process only
                        if (fileCounter == this.receiver.getMaxPollFiles()) {
                            break;
                        }
                        //ignore directories
                        if (Files.isDirectory(file)) {
                            continue;
                        }
                        if (!Files.isReadable(file)) {
                            logger.warning(rb.getResourceString("warning.noread", file.toString()));
                            continue;
                        }
                        if (!Files.isWritable(file)) {
                            logger.warning(rb.getResourceString("warning.ro", file.toString()));
                            continue;
                        }
                        //it is not sure that this triggers as the behavior depends on the OS file locking mechanism
                        if (!this.renameIsPossible(file.toFile())) {
                            logger.warning(rb.getResourceString("warning.notcomplete", file.toString()));
                            continue;
                        }
                        final Path finalFile = file;
                        Callable<Boolean> singleTask = new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                processFile(finalFile);
                                return (Boolean.TRUE);
                            }
                        };
                        tasks.add(singleTask);
                        fileCounter++;
                    }
                    //wait for all threads to be finished
                    try {
                        sendingTheadExecutor.invokeAll(tasks);
                    } catch (InterruptedException e) {
                        //nop
                    }
                } catch (Exception e) {
                    //nop
                }
                //if logging is requested log that the poll process will wait for next poll
                if (logPollProcess) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, (int) TimeUnit.MILLISECONDS.toSeconds(this.pollInterval));
                    DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
                    logger.log(Level.FINER, rb.getResourceString("poll.log.wait",
                            new Object[]{
                                this.sender.getName(), this.receiver.getName(),
                                String.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.pollInterval)),
                                format.format(calendar.getTime())
                            })
                    );
                }
            }
        } catch (Throwable e) {
            //do never bail out with an exception - else the schedule of this thread is lost
            SystemEventManagerImplAS2.instance().systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
        }
    }

    /**
     * Non blocking file directory polling
     */
    private List<Path> listFilesNIO(Path dir, DirectoryStream.Filter<Path> fileFilter) throws Exception {
        List<Path> result = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, fileFilter)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * Checks if the passed file could be renamed. If this is not possible, the
     * file is still used as stream target and should not be touched (works
     * actually only on windows but does not lead to problems for other OS)
     *
     * @param file
     * @return
     */
    private boolean renameIsPossible(File file) {
        File newFile = new File(file.getAbsolutePath() + "x");
        boolean renamePossible = file.renameTo(newFile);
        boolean renameBackPossible = newFile.renameTo(file);
        return (renamePossible && renameBackPossible);
    }

    /**
     * Processes a single, found file
     */
    private void processFile(Path file) {
        try {
            logger.fine(rb.getResourceString("processing.file",
                    new Object[]{
                        file.getFileName().toString(),
                        this.sender.getName(),
                        this.receiver.getName()
                    }));
            SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
            AS2Message message = orderSender.send(this.certificateManagerEncSign, this.sender, this.receiver, file, null,
                    this.receiver.getSubject(), null);
            EventBus.getInstance().publish(new RefreshClientMessageOverviewList());
            try {
                Files.delete(file);
                if (message != null) {
                    logger.log(Level.INFO,
                            rb.getResourceString("messagefile.deleted",
                                    new Object[]{
                                        file.getFileName().toString()}),
                            message.getAS2Info());
                }
            } catch (IOException e) {
                SystemEvent event = new SystemEvent(
                        SystemEvent.SEVERITY_WARNING,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_FILE_DELETE);
                event.setSubject(event.typeToTextLocalized());
                event.setBody(
                        rb.getResourceString("processing.file",
                                new Object[]{
                                    file.getFileName().toString(),
                                    this.sender.getName(),
                                    this.receiver.getName()
                                }) + "\n\n[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                SystemEventManagerImplAS2.instance().newEvent(event);
            }
        } catch (Throwable e) {
            String message = rb.getResourceString("processing.file.error",
                    new Object[]{
                        file.getFileName().toString(),
                        this.sender,
                        this.receiver,
                        e.getMessage()});
            logger.severe(message);
            Exception exception = new Exception(message, e);
            SystemEventManagerImplAS2.instance().systemFailure(exception, SystemEvent.TYPE_PROCESSING_ANY);
        }
    }
}
