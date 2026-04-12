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

package de.mendelson.comm.as2.usermanagement;

/**
 * Enum for the two system roles: ADMIN and USER
 *
 * ADMIN: Full system access, can manage users, switch users, access all data
 * USER: Regular user with access to their own partners, certificates, and messages
 */
public enum RoleType {
    /**
     * Administrator role - full system access
     */
    ADMIN(1, "ADMIN", "Administrator with full system access"),

    /**
     * Regular user role - access to own data only
     */
    USER(2, "USER", "Regular user with access to own data");

    private final int id;
    private final String name;
    private final String description;

    RoleType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get RoleType by role ID
     */
    public static RoleType fromId(int id) {
        for (RoleType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return USER; // Default to USER if not found
    }

    /**
     * Get RoleType by role name
     */
    public static RoleType fromName(String name) {
        for (RoleType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return USER; // Default to USER if not found
    }

    /**
     * Check if this role is ADMIN
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role is USER
     */
    public boolean isUser() {
        return this == USER;
    }

    @Override
    public String toString() {
        return name;
    }
}
