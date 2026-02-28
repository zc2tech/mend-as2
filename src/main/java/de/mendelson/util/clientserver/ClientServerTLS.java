//$Header: /as4/de/mendelson/util/clientserver/ClientServerTLS.java 1     7/03/24 13:04 Heller $
package de.mendelson.util.clientserver;

import javax.net.ssl.SSLContext;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface that deals with the TLS connection of the client-server connection
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface ClientServerTLS {

    /**
     * Create a SSLContext using a special provider
     * @return
     * @throws Exception 
     */
    public SSLContext createSSLContext() throws Exception;
    
    /**
     * Returns the product name
     * @return 
     */
    public String getProductName();

}
