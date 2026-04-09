package de.mendelson.util.mailautoconfig.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.mailautoconfig.MailServiceConfiguration;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 * @version $Revision: 3 $
 */
public class MailAutoConfigDetectResponse extends ClientServerResponse {

    private static final long serialVersionUID = 1L;
    private final List<MailServiceConfiguration> configurationList = new ArrayList<MailServiceConfiguration>();
      
    public MailAutoConfigDetectResponse(MailAutoConfigDetectRequest request) {
        super(request);
    }
    
    public List<MailServiceConfiguration> getConfiguration() {
        return (this.configurationList);
    }

    public void setMailServiceConfiguration(List<MailServiceConfiguration> configurationList) {
        this.configurationList.addAll(configurationList);
    }

    @Override
    public String toString() {
        return ("Return detected mail server configuration");
    }

    

}
