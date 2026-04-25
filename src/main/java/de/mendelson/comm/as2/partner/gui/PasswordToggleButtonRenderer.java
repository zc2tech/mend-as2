package de.mendelson.comm.as2.partner.gui;

import de.mendelson.util.MendelsonMultiResolutionImage;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Custom button renderer/editor for show/hide password toggle
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class PasswordToggleButtonRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final MendelsonMultiResolutionImage IMAGE_EYE_MASKED
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/passwordfield/password_toggle.svg", 16, 48);
    private static final MendelsonMultiResolutionImage IMAGE_EYE_UNMASKED
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/passwordfield/password_toggle_nomask.svg", 16, 48);

    private final JButton renderButton;
    private final JButton editorButton;
    private Boolean currentValue;

    private final ImageIcon iconEyeMasked;
    private final ImageIcon iconEyeUnmasked;

    public PasswordToggleButtonRenderer() {
        // Generate icons at 16px size
        iconEyeMasked = new ImageIcon(IMAGE_EYE_MASKED.toMinResolution(16));
        iconEyeUnmasked = new ImageIcon(IMAGE_EYE_UNMASKED.toMinResolution(16));

        renderButton = new JButton();
        renderButton.setOpaque(true);
        renderButton.setMargin(new Insets(0, 4, 0, 4));
        renderButton.setFocusPainted(false);
        renderButton.setBorderPainted(false);
        renderButton.setContentAreaFilled(false);

        editorButton = new JButton();
        editorButton.setOpaque(true);
        editorButton.setMargin(new Insets(0, 4, 0, 4));
        editorButton.setFocusPainted(false);
        editorButton.setBorderPainted(false);
        editorButton.setContentAreaFilled(false);

        editorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the value
                currentValue = !currentValue;
                // Stop editing and update the model
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Boolean isVisible = (Boolean) value;
        if (isVisible != null && isVisible) {
            renderButton.setIcon(iconEyeUnmasked);
            renderButton.setToolTipText("Hide password");
        } else {
            renderButton.setIcon(iconEyeMasked);
            renderButton.setToolTipText("Show password");
        }

        if (isSelected) {
            renderButton.setBackground(table.getSelectionBackground());
        } else {
            renderButton.setBackground(table.getBackground());
        }

        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        this.currentValue = (Boolean) value;

        if (currentValue != null && currentValue) {
            editorButton.setIcon(iconEyeUnmasked);
            editorButton.setToolTipText("Hide password");
        } else {
            editorButton.setIcon(iconEyeMasked);
            editorButton.setToolTipText("Show password");
        }

        editorButton.setBackground(table.getSelectionBackground());

        return editorButton;
    }

    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }
}
