package de.mendelson.comm.as2.api.message;

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
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class CommandResponse extends ClientServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String serversideResponseFilename = null;
    
    public CommandResponse(CommandRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Command response");
    }

    /**
     * @return the serversideResponseFilename
     */
    public String getServersideResponseFilename() {
        return serversideResponseFilename;
    }

    /**
     * @param serversideResponseFilename the serversideResponseFilename to set
     */
    public void setServersideResponseFilename(String serversideResponseFilename) {
        this.serversideResponseFilename = serversideResponseFilename;
    }
}
