package de.mendelson.util.clientserver.messages;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Login request message for SwingUI authentication
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class LoginRequest extends ClientServerMessage  {

    public static final long serialVersionUID = 1L;

    private String username;
    private char[] password;
    private String clientId;

    public LoginRequest(String username, char[] password, String clientId) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "LoginRequest for user: " + username;
    }
}
