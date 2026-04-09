package de.mendelson.comm.as2.database.migration.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
 * @version $Revision: 2 $
 */
public class HSQLDBMigrationResponse extends ClientServerResponse {

    private static final long serialVersionUID = 1L;
    
    private int keystoresSuccessfullyImportedCount = 0;
    private int preferencesSuccessfullyImportedCount = 0;
    
    public HSQLDBMigrationResponse(HSQLDBMigrationRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("HSQLDB preferences migration response");
    }

    /**
     * @return the keystoresSuccessfullyImported
     */
    public int getKeystoresSuccessfullyImported() {
        return this.keystoresSuccessfullyImportedCount;
    }

    /**
     * @param keystoresSuccessfullyImported the keystoresSuccessfullyImported to set
     */
    public void setKeystoresSuccessfullyImported(int importCount) {
        this.keystoresSuccessfullyImportedCount = importCount;
    }

    /**
     * @return the preferencesSuccessfullyImported
     */
    public int getPreferencesSuccessfullyImported() {
        return this.preferencesSuccessfullyImportedCount;
    }

    /**
     * @param preferencesSuccessfullyImported the preferencesSuccessfullyImported to set
     */
    public void setPreferencesSuccessfullyImported(int importCount) {
        this.preferencesSuccessfullyImportedCount = importCount;
    }

}
