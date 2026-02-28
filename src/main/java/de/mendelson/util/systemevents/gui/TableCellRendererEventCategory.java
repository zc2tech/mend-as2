package de.mendelson.util.systemevents.gui;

import de.mendelson.util.systemevents.SystemEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Table model to display the event categories
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class TableCellRendererEventCategory extends DefaultTableCellRenderer implements TableCellRenderer {

    public static final int ROW_HEIGHT = TableModelSystemEvents.ROW_HEIGHT;
    protected static final int IMAGE_HEIGHT = ROW_HEIGHT - 3;

    /**
     * Creates a default table cell renderer.
     */
    public TableCellRendererEventCategory() {
        super();
        this.setOpaque(true);
    }

    /**
     *
     * Returns the default table cell renderer.
     *
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at
     * <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Color defaultForgroundColor;
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            defaultForgroundColor = table.getSelectionForeground();
            this.setForeground(defaultForgroundColor);
        } else {
            this.setBackground(table.getBackground());
            defaultForgroundColor = table.getForeground();
            this.setForeground(defaultForgroundColor);
        }
        this.setEnabled(table.isEnabled());
        this.setFont(table.getFont());
        if (value == null) {
            this.setText("");
        } else if (value instanceof SystemEvent) {
            SystemEvent event = (SystemEvent) value;
            this.setIcon(event.getCategoryIconMultiResolution(IMAGE_HEIGHT));
            this.setText(event.categoryToTextLocalized());
        }
        return (this);
    }

    /*
     * The following methods are overridden as a performance measure to
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and
     * drawbacks of overriding methods like these.
     */
    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground())
                && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
        if (propertyName.equals("text")) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}
