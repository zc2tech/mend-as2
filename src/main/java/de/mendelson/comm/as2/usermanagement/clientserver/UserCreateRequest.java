/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.usermanagement.clientserver;

import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.util.clientserver.messages.ClientServerMessage;

/**
 * Message to create a new user
 *
 */
public class UserCreateRequest extends ClientServerMessage{

    private static final long serialVersionUID = 1L;
    private WebUIUser user;
    private String plainPassword;
    private boolean generateAndEmailPassword;

    public UserCreateRequest() {
    }

    public WebUIUser getUser() {
        return user;
    }

    public void setUser(WebUIUser user) {
        this.user = user;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public boolean isGenerateAndEmailPassword() {
        return generateAndEmailPassword;
    }

    public void setGenerateAndEmailPassword(boolean generateAndEmailPassword) {
        this.generateAndEmailPassword = generateAndEmailPassword;
    }

    @Override
    public String toString() {
        return "Create user: " + (user != null ? user.getUsername() : "null");
    }
}
