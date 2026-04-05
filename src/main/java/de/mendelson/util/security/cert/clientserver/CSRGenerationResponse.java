package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class CSRGenerationResponse extends ClientServerResponse implements Serializable {

    /**
     * @return the crmfSignatureBase64
     */
    public String getCrmfSignatureBase64() {
        return crmfSignatureBase64;
    }

    /**
     * @param crmfSignatureBase64 the crmfSignatureBase64 to set
     */
    public void setCrmfSignatureBase64(String crmfSignatureBase64) {
        this.crmfSignatureBase64 = crmfSignatureBase64;
    }

    /**
     * @return the crmfEncryptionBase64
     */
    public String getCrmfEncryptionBase64() {
        return crmfEncryptionBase64;
    }

    /**
     * @param crmfEncryptionBase64 the crmfEncryptionBase64 to set
     */
    public void setCrmfEncryptionBase64(String crmfEncryptionBase64) {
        this.crmfEncryptionBase64 = crmfEncryptionBase64;
    }

    /**
     * @return the crmfTLSBase64
     */
    public String getCrmfTLSBase64() {
        return crmfTLSBase64;
    }

    /**
     * @param crmfTLSBase64 the crmfTLSBase64 to set
     */
    public void setCrmfTLSBase64(String crmfTLSBase64) {
        this.crmfTLSBase64 = crmfTLSBase64;
    }

    private static final long serialVersionUID = 1L;
    private String csrBase64 = null;
    private String crmfSignatureBase64 = null;
    private String crmfEncryptionBase64 = null;
    private String crmfTLSBase64 = null;
    
    public CSRGenerationResponse(CSRGenerationRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Generate CSR");
    }
    
    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
    
    /**
     * @return the csrPEM
     */
    public String getCSRBase64() {
        return this.csrBase64;
    }

    /**
     * @param csrBase64 the csr to set, in BASE64 encoding
     */
    public void setCSRBase64(String csrBase64) {
        this.csrBase64 = csrBase64;
    }
}
