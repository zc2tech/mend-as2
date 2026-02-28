//$Header: /as2/de/mendelson/comm/as2/database/migration/clientserver/HSQLDBMigrationRequest.java 1     7/11/23 8:15 Heller $
package de.mendelson.comm.as2.database.migration.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class HSQLDBMigrationRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private boolean migratePreferences = false;
    private boolean migrateKeystores = false;
    
    public HSQLDBMigrationRequest() {
    }

    public void setMigration( boolean migratePreferences, boolean migrateKeystores){
        this.setMigrateKeystores(migrateKeystores);
        this.setMigratePreferences(migratePreferences);
    }
    
    @Override
    public String toString() {
        return ("HSQLDB preferences migration request");
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }

    /**
     * @return the migratePreferences
     */
    public boolean isMigratePreferences() {
        return migratePreferences;
    }

    /**
     * @param migratePreferences the migratePreferences to set
     */
    public void setMigratePreferences(boolean migratePreferences) {
        this.migratePreferences = migratePreferences;
    }

    /**
     * @return the migrateKeystores
     */
    public boolean isMigrateKeystores() {
        return migrateKeystores;
    }

    /**
     * @param migrateKeystores the migrateKeystores to set
     */
    public void setMigrateKeystores(boolean migrateKeystores) {
        this.migrateKeystores = migrateKeystores;
    }
    
}
