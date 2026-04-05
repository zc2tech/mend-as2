package de.mendelson.comm.as2;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.ResourceBundleAS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBServer;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Thread that is executed if the VM will shut down (that means the server is
 * shut down)
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class AS2ShutdownThread extends Thread {

    private final IDBServer dbServer;
    private MecResourceBundle rb = null;

    public AS2ShutdownThread(IDBServer dbServer) {
        this.dbServer = dbServer;
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Server.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * This will start the thread, it is called if the JVM shutdown is detected
     */
    @Override
    public void run() {
        //indicate that the system is in shutdown process for all other processes
        AS2Server.inShutdownProcess = true;
        try {
            //if this is an external database server this will simply do nothing
            this.dbServer.shutdown();
        } catch (Throwable e) {
            //nop
        }
        SystemEventManagerImplAS2.instance().newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_MAIN_SERVER_SHUTDOWN,
                this.rb.getResourceString("server.shutdown", AS2ServerVersion.getProductName()),
                "");
        System.out.println(this.rb.getResourceString("server.shutdown", AS2ServerVersion.getProductName()));
        //delete lock file
        AS2Server.deleteLockFile();
    }
}
