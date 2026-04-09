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
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.List;

/**
 * Response containing list of users
 *
 */
public class UserListResponse extends ClientServerResponse {

    private static final long serialVersionUID = 1L;
    private List<WebUIUser> users;

    public UserListResponse(UserListRequest request) {
        super(request);
    }

    public List<WebUIUser> getUsers() {
        return users;
    }

    public void setUsers(List<WebUIUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "User list response";
    }
}
