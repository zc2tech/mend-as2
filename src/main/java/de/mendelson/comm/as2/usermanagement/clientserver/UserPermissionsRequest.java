package de.mendelson.comm.as2.usermanagement.clientserver;

import de.mendelson.comm.as2.clientserver.message.ClientServerMessage;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Request to retrieve current user's permissions
 * Uses the username from the session, no parameters needed
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class UserPermissionsRequest extends ClientServerMessage {

    public static final long serialVersionUID = 1L;

    public UserPermissionsRequest() {
    }

    @Override
    public String toString() {
        return "User permissions request";
    }
}
