package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
public class SinglePartnerModificationResponse extends ClientServerResponse {

    private static final long serialVersionUID = 1L;

    public SinglePartnerModificationResponse(SinglePartnerModificationRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Modify partner");
    }
       
}
