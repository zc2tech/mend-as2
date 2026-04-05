package de.mendelson.util.clientserver;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is thrown if a login failed because of an authentication problem
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class LoginFailedException extends Exception{

    private final int loginState;
    
    public LoginFailedException(String message, int loginState){
        super( message );
        this.loginState = loginState;
    }

    /**
     * @return the loginState
     */
    public int getLoginState() {
        return loginState;
    }
    
}
