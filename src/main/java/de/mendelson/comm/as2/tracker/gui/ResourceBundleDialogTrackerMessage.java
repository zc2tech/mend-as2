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
 * Resource bundle for tracker message viewer dialog
 *
 * @author Julian Xu
 */
public class ResourceBundleDialogTrackerMessage extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS = {
        {"title", "Tracker Messages"},
        {"filter.title", "Filter Options"},
        {"label.startdate", "Start Date:"},
        {"label.enddate", "End Date:"},
        {"label.trackerid.filter", "Tracker ID:"},
        {"label.trackerid.filter.tooltip", "Search by partial tracker ID (leave empty to search by date)"},
        {"label.auth.title", "Auth Status:"},
        {"label.auth.none", "No Auth"},
        {"label.auth.success", "Success"},
        {"label.auth.failed", "Failed"},
        {"button.search", "Search"},
        {"button.refresh", "Refresh"},
        {"button.reset", "Reset Filter"},
        {"column.trackerid", "Tracker ID"},
        {"column.timestamp", "Timestamp"},
        {"column.remoteip", "Remote IP"},
        {"column.useragent", "User Agent"},
        {"column.size", "Size"},
        {"column.authstatus", "Auth Status"},
        {"column.authuser", "User"},
        {"details.title", "Message Details"},
        {"details.noselection", "Select a message to view details"},
        {"button.download", "Download Content"},
        {"button.download.tooltip", "Download full message content to file"},
        {"status.searching", "Searching tracker messages..."},
        {"status.found", "Found messages"},
        {"status.downloading", "Downloading message content..."},
        {"status.download.success", "File saved successfully"},
        {"error.dates.required", "Start and end dates are required"},
        {"error.dates.invalid", "Start date must be before end date"},
        {"error.search", "Search failed"},
        {"error.download", "Download failed"},
        {"error.noselection", "Please select a message first"},
    };
}
