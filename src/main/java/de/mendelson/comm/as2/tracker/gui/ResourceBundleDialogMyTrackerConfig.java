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
package de.mendelson.comm.as2.tracker.gui;

import de.mendelson.util.MecResourceBundle;

/**
 * Resource bundle for My Tracker Configuration dialog
 *
 * @author Julian Xu
 */
public class ResourceBundleDialogMyTrackerConfig extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS = {
        {"title", "My Tracker Configuration"},
        {"label.endpoint", "Your Tracker Endpoint URL:"},
        {"tab.basicauth", "Basic Authentication"},
        {"tab.certauth", "Certificate Authentication"},
        {"label.basicauth.enable", "Enable Basic Authentication:"},
        {"label.certauth.enable", "Enable Certificate Authentication:"},
        {"column.enabled", "Enabled"},
        {"column.username", "Username"},
        {"column.password", "Password"},
        {"column.certificate", "Certificate"},
        {"button.ok", "OK"},
        {"button.cancel", "Cancel"},
        {"button.add", "Add"},
        {"button.delete", "Delete"},
        {"success.saved", "Tracker authentication configuration saved successfully"},
    };
}
