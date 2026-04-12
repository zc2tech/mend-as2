package de.mendelson.util.clientserver.messages;

import de.mendelson.util.clientserver.user.User;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Login response message for SwingUI authentication
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class LoginResponse extends ClientServerResponse  {

    public static final long serialVersionUID = 1L;

    private boolean success;
    private String errorMessage;
    private User user;
    private boolean mustChangePassword;

    public LoginResponse(LoginRequest request) {
        super(request);
    }

    /**
     * @return true if authentication succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success true if authentication succeeded
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the error message if authentication failed
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param message the error message
     */
    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    /**
     * @return the authenticated user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the authenticated user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return true if user must change password on first login
     */
    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    /**
     * @param must true if user must change password
     */
    public void setMustChangePassword(boolean must) {
        this.mustChangePassword = must;
    }
}
