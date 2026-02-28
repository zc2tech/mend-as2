//$Header: /as2/de/mendelson/Copyright.java 25    11/02/25 13:39 Heller $
package de.mendelson;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Show information about the copyright message for all products of
 * mendelson-e-commerce GmbH
 * @author S.Heller
 * @version $Revision: 25 $
 */
public class Copyright{

    private Copyright(){        
    }
    
    /**Gets the copyright message for all products*/
    public static String getCopyrightMessage(){
        return( "(c) 2000-2025 mendelson-e-commerce GmbH Berlin, Germany" );
    }
    
}
