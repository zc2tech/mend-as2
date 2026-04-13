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
 * Resource bundle for tracker configuration dialog
 *
 * @author Julian Xu
 */
public class ResourceBundleDialogTrackerConfig extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS = {
        {"title", "Tracker Configuration"},
        {"label.tracker.url.info", "Your Tracker Endpoint URL:"},
        {"label.enabled", "Enable Tracker Endpoint:"},
        {"label.auth.required", "Require Authentication:"},
        {"label.maxsize", "Max Message Size (MB):"},
        {"label.ratelimit.title", "Rate Limiting Settings:"},
        {"label.ratelimit.failures", "Max Failed Attempts:"},
        {"label.ratelimit.window", "Time Window (hours):"},
        {"label.ratelimit.block", "Block Duration (minutes):"},
        {"button.ok", "OK"},
        {"button.cancel", "Cancel"},
        {"success.saved", "Tracker configuration saved successfully"},
        {"error.save", "Failed to save configuration"},
        {"error.maxsize", "Max size must be between 1 and 100 MB"},
        {"error.failures", "Max failures must be between 1 and 100"},
        {"error.window", "Time window must be between 1 and 24 hours"},
        {"error.block", "Block duration must be between 1 and 1440 minutes (24 hours)"},
        {"error.invalid.number", "Please enter valid numbers for all numeric fields"},
    };
}
