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

import de.mendelson.comm.as2.tracker.TrackerMessageInfo;
import de.mendelson.util.MecResourceBundle;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Table model for tracker messages
 *
 * @author Julian Xu
 */
public class TableModelTrackerMessage extends AbstractTableModel {

    public static final int ROW_HEIGHT = 24;

    private MecResourceBundle rb;
    private List<TrackerMessageInfo> data = new ArrayList<>();
    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    public TableModelTrackerMessage() {
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogTrackerMessage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Resource bundle not found: " + e.getClassName());
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return rb.getResourceString("column.trackerid");
            case 1:
                return rb.getResourceString("column.timestamp");
            case 2:
                return rb.getResourceString("column.remoteip");
            case 3:
                return rb.getResourceString("column.useragent");
            case 4:
                return rb.getResourceString("column.size");
            case 5:
                return rb.getResourceString("column.authstatus");
            case 6:
                return rb.getResourceString("column.authuser");
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
            case 2:
            case 3:
            case 5:
            case 6:
                return String.class;
            case 1:
                return String.class; // Formatted date string
            case 4:
                return Integer.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row >= data.size()) {
            return null;
        }

        TrackerMessageInfo info = data.get(row);

        switch (column) {
            case 0:
                return info.getTrackerId();
            case 1:
                return info.getInitDate() != null ? dateFormat.format(info.getInitDate()) : "";
            case 2:
                return info.getRemoteAddr();
            case 3:
                String ua = info.getUserAgent();
                return ua != null && ua.length() > 50 ? ua.substring(0, 47) + "..." : ua;
            case 4:
                return formatSize(info.getContentSize());
            case 5:
                return info.getAuthStatusText();
            case 6:
                return info.getAuthUser() != null ? info.getAuthUser() : "";
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Pass new data to the table
     */
    public void passNewData(List<TrackerMessageInfo> newData) {
        this.data.clear();
        if (newData != null) {
            this.data.addAll(newData);
        }
        fireTableDataChanged();
    }

    /**
     * Get tracker message info at row
     */
    public TrackerMessageInfo getRow(int row) {
        if (row >= 0 && row < data.size()) {
            return data.get(row);
        }
        return null;
    }

    /**
     * Format size in bytes to human-readable format
     */
    private String formatSize(int bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}
