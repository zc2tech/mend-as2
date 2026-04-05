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

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Response containing HTTP authentication preferences for a user
 * Structure: partnerId -> type (message/mdn) -> field (username/password) -> value
 */
public class UserHttpAuthPreferenceResponse extends ClientServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private Map<Integer, Map<String, Map<String, String>>> preferences;

    public UserHttpAuthPreferenceResponse(UserHttpAuthPreferenceRequest request) {
        super(request);
        this.userId = request.getUserId();
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

    /**
     * Get a specific preference value
     */
    public String getPreference(int partnerId, String type, String field) {
        return preferences.getOrDefault(partnerId, new HashMap<>())
                .getOrDefault(type, new HashMap<>())
                .get(field);
    }

    @Override
    public String toString() {
        return "HTTP auth preferences response for user " + userId + " (" + preferences.size() + " partners)";
    }
}
