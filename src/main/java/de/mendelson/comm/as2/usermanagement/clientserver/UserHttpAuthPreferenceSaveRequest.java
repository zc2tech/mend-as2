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

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Message to save HTTP authentication preferences for a user
 * Structure: partnerId -> type (message/mdn) -> field (username/password) -> value
 */
public class UserHttpAuthPreferenceSaveRequest extends ClientServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private Map<Integer, Map<String, Map<String, String>>> preferences;

    public UserHttpAuthPreferenceSaveRequest(int userId) {
        this.userId = userId;
        this.preferences = new HashMap<>();
    }

    public int getUserId() {
        return userId;
    }

    public Map<Integer, Map<String, Map<String, String>>> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<Integer, Map<String, Map<String, String>>> preferences) {
        this.preferences = preferences;
    }

    /**
     * Add a preference value
     */
    public void addPreference(int partnerId, String type, String field, String value) {
        preferences.computeIfAbsent(partnerId, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new HashMap<>())
                .put(field, value);
    }

    @Override
    public String toString() {
        return "Save HTTP auth preferences for user " + userId + " (" + preferences.size() + " partners)";
    }
}
