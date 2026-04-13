package de.mendelson.comm.as2.message.clientserver;

import de.mendelson.comm.as2.message.MessageOverviewFilter;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
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
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class MessageOverviewRequest extends ClientServerMessage{

    private static final long serialVersionUID = 1L;
    private MessageOverviewFilter filter = null;
    private String messageId = null;
    private int userId = 0;  // User ID for filtering (0 = admin)
    private boolean hasUserManagePermission = false;  // If true, user sees all messages

    public MessageOverviewRequest(String messageId) {
        this.messageId = messageId;
    }

    public MessageOverviewRequest(MessageOverviewFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return ("Message overview request");
    }

    /**
     * @return the filter
     */
    public MessageOverviewFilter getFilter() {
        return filter;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set (0 = admin, >0 = specific user)
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return whether user has USER_MANAGE permission
     */
    public boolean hasUserManagePermission() {
        return hasUserManagePermission;
    }

    /**
     * @param hasUserManagePermission whether user has USER_MANAGE permission
     */
    public void setHasUserManagePermission(boolean hasUserManagePermission) {
        this.hasUserManagePermission = hasUserManagePermission;
    }

    /**
     * Prevent an overwrite of the readObject method for de-serialization
     */
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
        inStream.defaultReadObject();
    }

}
