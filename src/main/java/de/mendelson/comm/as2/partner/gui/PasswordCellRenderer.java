package de.mendelson.comm.as2.partner.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Custom renderer for password column that shows asterisks or plain text
 * based on visibility state
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class PasswordCellRenderer extends JTextField implements TableCellRenderer {

    public PasswordCellRenderer() {
        super();
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // Get the password value
        String password = value != null ? value.toString() : "";

        // Check if password should be visible for this row
        TableModelInboundAuthBasic model = (TableModelInboundAuthBasic) table.getModel();
        Boolean isVisible = (Boolean) model.getValueAt(row, TableModelInboundAuthBasic.COL_SHOW_PASSWORD);

        if (isVisible != null && isVisible) {
            // Show plain text
            setText(password);
        } else {
            // Show asterisks
            setText(password.isEmpty() ? "" : "••••••••");
        }

        // Set colors based on selection
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // Set border for focus
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        return this;
    }

    private static final Border noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
}
