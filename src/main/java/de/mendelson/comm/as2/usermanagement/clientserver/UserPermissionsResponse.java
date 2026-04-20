package de.mendelson.comm.as2.usermanagement.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.util.HashSet;
import java.util.Set;

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
 * Response containing user's permissions
 * Returns a set of permission names (e.g., "PARTNER_READ", "MESSAGE_WRITE")
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class UserPermissionsResponse extends ClientServerResponse {

    public static final long serialVersionUID = 1L;
    private Set<String> permissions = new HashSet<>();

    public UserPermissionsResponse(ClientServerMessage request) {
        super(request);
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        if (permissions == null) {
            this.permissions = new HashSet<>();
        } else {
            this.permissions = permissions;
        }
    }

    @Override
    public String toString() {
        return "User permissions response: " + permissions.size() + " permissions";
    }
}
