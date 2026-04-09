package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
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
 * @version $Revision: 2 $
 */
public class CertificateExportResponse extends ClientServerResponse {

    private static final long serialVersionUID = 1L;
    private byte[] exportData = null;
    
    public CertificateExportResponse(CertificateExportRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Export certificate");
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }

    /**
     * @return the exportData
     */
    public byte[] getExportData() {
        return exportData;
    }

    /**
     * @param exportData the exportData to set
     */
    public void setExportData(byte[] exportData) {
        this.exportData = exportData;
    }
    
    
}
