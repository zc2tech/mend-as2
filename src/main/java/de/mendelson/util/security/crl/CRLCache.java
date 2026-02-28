//$Header: /as2/de/mendelson/util/security/crl/CRLCache.java 1     26/02/24 11:26 Heller $
package de.mendelson.util.security.crl;

import java.security.cert.X509CRL;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Verifies a CRL of a certificate
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class CRLCache {
    
    private final static ConcurrentHashMap<String, X509CRL> CRL_MAP 
            = new ConcurrentHashMap<String, X509CRL>();

    /**Returns the CRL of the passed URL if it is still valid or null if it is either no longer valid or does not
     * exist
     * @param url
     * @return null if the entry is not valid (date) or does not exist so far
     */
    public X509CRL getCRL( String url ){
        if( CRL_MAP.containsKey(url)){
            X509CRL crl = CRL_MAP.get(url);
            Instant now = Instant.now();
            if( crl.getNextUpdate().toInstant().isBefore(now)){
                CRL_MAP.remove(url);
                return( null );
            }
            return( CRL_MAP.get(url));
        }else{
            return( null );
        }
    }
    
    public void put( String url, X509CRL crl){
        CRL_MAP.put(url, crl);
    }

    
}
