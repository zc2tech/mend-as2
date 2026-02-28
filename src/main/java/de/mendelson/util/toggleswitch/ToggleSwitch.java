package de.mendelson.util.toggleswitch;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Switch button implementation
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ToggleSwitch extends JPanel implements ItemSelectable {

    private static final AtomicInteger UNIQUE_ID = new AtomicInteger(0);
    
    private Color switchOnColor = Color.decode("#0067c0");
    private Color switchOffColor = Color.decode("#888a8a");
    private Color switchBackgroundColor = Color.WHITE;
    private int borderSize = 1;
    private static final int SPACE = 2;
    private static final int SPACE_MOUSE_OVER = 3;

    private int textPosition = SwingConstants.RIGHT;
    private String text = "";
    private static final int ROUND_SQARE = 5;
    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 0;
    public static final int SHAPE_SQUARE = 0;
    public static final int SHAPE_ROUND = 1;
    private int onOffState = STATE_OFF;
    private int shape = SHAPE_ROUND;
    private boolean inMouseOver;
    private boolean inMousePressed;
    private boolean displayStatusText = false;
    private final List<ActionListener> actionListenerList
            = Collections.synchronizedList(new ArrayList<ActionListener>());
    private final List<ItemListener> itemListenerList
            = Collections.synchronizedList(new ArrayList<ItemListener>());

    private static final RenderingHints RENDERING_HINTS_BEST_QUALITY
            = new RenderingHints(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

    static {
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        RENDERING_HINTS_BEST_QUALITY.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE));
    }
    private static final MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleToggleSwitch.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    public ToggleSwitch() {
        initComponents();
        setOpaque(false);
        this.initMouseEvents();
        this.setColorDefaultsFromUIManager();
        this.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    /**
     * Sets the colour defaults from the UI manager to this component
     */
    public ToggleSwitch setColorDefaultsFromUIManager() {
        final String KEY_SWITCH_COLOR = "TextField.selectionBackground";
        final String KEY_SWITCH_OFF_COLOR = "Button.borderColor";
        final String KEY_SWITCH_BACKGROUND = "TextField.background";
        //switch color
        if (UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_COLOR) != null) {
            this.setColorSwitchON(UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_COLOR));
        }
        //border and off color
        if (UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_OFF_COLOR) != null) {
            this.setColorSwitchOFF(UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_OFF_COLOR));
        }
        //background color
        if (UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_BACKGROUND) != null) {
            this.setColorSwitchBackground(UIManager.getLookAndFeelDefaults().getColor(KEY_SWITCH_BACKGROUND));
        }
        return (this);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (this.jLabelRight != null) {
            this.jLabelRight.setFont(font);
        }
        if (this.jLabelLeft != null) {
            this.jLabelLeft.setFont(font);
        }
    }

    /**
     * If this is set the switch will always display its state localized in the
     * text. All other text settings are ignored
     */
    public void setDisplayStatusText(boolean displayStatusText) {
        this.displayStatusText = displayStatusText;
        this.handleStatusLabelSize();
        this.handleStatusTextDisplay();
    }

    /**
     * Depending on the font the label for the status text has to have always
     * the longest possible width
     */
    private void handleStatusLabelSize() {
        String onStr = rb.getResourceString("on");
        String offStr = rb.getResourceString("off");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //left and right gap
                int gaps = 2;
                int onStrWidth = computeStringWidth(jLabelLeft.getFont(), onStr) + gaps;
                int offStrWidth = computeStringWidth(jLabelLeft.getFont(), offStr) + gaps;
                int width = Math.max(onStrWidth, offStrWidth);
                jLabelLeft.setPreferredSize(new Dimension(width, jLabelLeft.getPreferredSize().height));
                jLabelLeft.setMinimumSize(new Dimension(width, jLabelLeft.getMinimumSize().height));
                jLabelLeft.setMaximumSize(new Dimension(width, jLabelLeft.getMaximumSize().height));
                jLabelRight.setPreferredSize(new Dimension(width, jLabelRight.getPreferredSize().height));
                jLabelRight.setMinimumSize(new Dimension(width, jLabelRight.getMinimumSize().height));
                jLabelRight.setMaximumSize(new Dimension(width, jLabelRight.getMaximumSize().height));
                jLabelLeft.invalidate();
                jLabelLeft.validate();
                jLabelRight.invalidate();
                jLabelRight.validate();
            }
        });
    }

    /**
     * Compute the width of the content up to the actual cursor position not
     * been found on the OS
     */
    private int computeStringWidth(Font font, String text) {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affinetransform, true, true);
        int width = (int) (font.getStringBounds(text, fontRenderContext).getWidth());
        return (width);
    }

    private void handleStatusTextDisplay() {
        String onStr = rb.getResourceString("on");
        String offStr = rb.getResourceString("off");
        if (this.displayStatusText) {
            if (this.isSelected()) {
                this.jLabelLeft.setText(onStr);
                this.jLabelRight.setText(onStr);
            } else {
                this.jLabelLeft.setText(offStr);
                this.jLabelRight.setText(offStr);
            }
        } else {
            this.jLabelLeft.setText(this.text);
            this.jLabelRight.setText(this.text);
        }
    }

    /**
     * Enables/disables the switch. If the component is disabled its background
     * will be just painted int the background of the component
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.jLabelLeft.setEnabled(enabled);
        this.jLabelRight.setEnabled(enabled);
    }

    /**
     * Sets the horizontal position of the text Parameters: textPosition - one
     * of the following values: SwingConstants.RIGHT SwingConstants.LEFT
     */
    public void setHorizontalTextPosition(int textPosition) {
        if (textPosition != SwingConstants.RIGHT && textPosition != SwingConstants.LEFT) {
            throw new IllegalArgumentException(
                    "SwitchButton.setHorizontalTextPosition must be one of SwingConstants.RIGHT, "
                    + "SwingConstants.LEFT");
        }
        this.textPosition = textPosition;
        if (textPosition == SwingConstants.LEFT) {
            this.jLabelRight.setVisible(false);
            this.jLabelLeft.setVisible(true);
            this.jPanelSpaceRight.setVisible(true);
        } else {
            this.jLabelRight.setVisible(true);
            this.jLabelLeft.setVisible(false);
            this.jPanelSpaceRight.setVisible(false);
        }
        this.repaint();
    }

    /**
     * Gets the horizontal position of the text Parameters: textPosition - one
     * of the following values: SwingConstants.RIGHT SwingConstants.LEFT
     */
    public int getHorizontalTextPosition() {
        return (this.textPosition);
    }

    /**
     * Returns an array of all the ActionListeners added to this Component with
     * addActionListener().
     */
    public ActionListener[] getActionListeners() {
        synchronized (this.actionListenerList) {
            ActionListener[] listenerArray = new ActionListener[this.actionListenerList.size()];
            for (int i = 0; i < this.actionListenerList.size(); i++) {
                listenerArray[i] = this.actionListenerList.get(i);
            }
            return (listenerArray);
        }
    }

    /**
     * Returns an array of all the ItemListeners added to this Component with
     * addItemListener().
     */
    public ItemListener[] getItemListeners() {
        synchronized (this.itemListenerList) {
            ItemListener[] listenerArray = new ItemListener[this.itemListenerList.size()];
            for (int i = 0; i < this.itemListenerList.size(); i++) {
                listenerArray[i] = this.itemListenerList.get(i);
            }
            return (listenerArray);
        }
    }

    public void setText(String text) {
        this.text = text;
        this.jLabelRight.setText(text);
        this.jLabelLeft.setText(text);
    }

    public String getText() {
        return (this.text);
    }

    public Color getColorSwitchOFF() {
        return switchOffColor;
    }

    public void setColorSwitchOFF(Color switchOffColor) {
        this.switchOffColor = switchOffColor;
        this.repaint();
    }

    public Color getColorSwitchON() {
        return switchOnColor;
    }

    public void setColorSwitchBackground(Color switchBackgroundColor) {
        this.switchBackgroundColor = switchBackgroundColor;
        this.repaint();
    }

    public Color getColorSwitchBackground() {
        return this.switchBackgroundColor;
    }

    public void setColorSwitchON(Color switchColor) {
        this.switchOnColor = switchColor;
        this.repaint();
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        this.repaint();
    }

    public int getShape() {
        return this.shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
        this.repaint();
    }

    public boolean isSelected() {
        return (this.onOffState == STATE_ON);
    }

    public void setSelected(boolean selected) {
        if (selected != this.isSelected()) {
            if (this.onOffState == STATE_OFF) {
                this.onOffState = STATE_ON;
            } else {
                this.onOffState = STATE_OFF;
            }
            this.handleStatusTextDisplay();
            this.fireItemChangedPerformed();
            this.repaint();
        }
    }

    @Override
    public void addItemListener(ItemListener listener) {
        synchronized (this.itemListenerList) {
            if (!this.itemListenerList.contains(listener)) {
                this.itemListenerList.add(listener);
            }
        }
    }

    @Override
    public void removeItemListener(ItemListener listener) {
        synchronized (this.itemListenerList) {
            this.itemListenerList.remove(listener);
        }
    }

    /**
     * Adds a action listener to this component. It will be informed by a user
     * related change event - means a mouse click
     *
     * @param listener The action listener that will be informed
     */
    public void addActionListener(ActionListener listener) {
        synchronized (this.actionListenerList) {
            if (!this.actionListenerList.contains(listener)) {
                this.actionListenerList.add(listener);
            }
        }
    }

    public void removeActionListener(ActionListener listener) {
        synchronized (this.actionListenerList) {
            this.actionListenerList.remove(listener);
        }
    }

    private void initMouseEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                inMouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                inMouseOver = false;
                inMousePressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                inMousePressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e) && inMouseOver) {
                    setSelected(!isSelected());
                    fireActionPerformed();
                }
                inMousePressed = false;
            }
        };
        this.addMouseListener(mouseAdapter);
    }

    /**
     * Informs the action listeners about an action event
     */
    private void fireActionPerformed() {
        int id = UNIQUE_ID.incrementAndGet();
        String command = this.onOffState == STATE_OFF ? "switched_off" : "switched_on";
        ActionEvent event = new ActionEvent(this, id, command);
        synchronized (this.actionListenerList) {
            for (ActionListener listener : this.actionListenerList) {
                listener.actionPerformed(event);
            }
        }
    }

    private void fireItemChangedPerformed() {
        int eventType = this.onOffState == STATE_OFF ? ItemEvent.DESELECTED : ItemEvent.SELECTED;
        ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.onOffState, eventType);
        synchronized (this.itemListenerList) {
            for (ItemListener listener : this.itemListenerList) {
                listener.itemStateChanged(event);
            }
        }
    }

    /**
     * Programmatically perform a "click". This does the same thing as if the
     * user had pressed and released the button.
     */
    public void doClick() {
        this.setSelected(!this.isSelected());
    }

    @Override
    public Object[] getSelectedObjects() {
        if (this.isSelected() == false) {
            return null;
        }
        Object[] selectedObjects = new Object[1];
        selectedObjects[0] = getText();
        return selectedObjects;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHints(RENDERING_HINTS_BEST_QUALITY);
        double width = this.jPanelSwitch.getPreferredSize().getWidth();
        double height = this.jPanelSwitch.getPreferredSize().getHeight();
        this.paintBorder(g2d, width, height);
        this.paintSwitch(g2d, width, height);
        g2d.dispose();
    }

    private void paintBorder(Graphics2D g2d, double width, double height) {
        double offsetX = this.jPanelSwitch.getX();
        double offsetY = this.jPanelSwitch.getY();
        double radius = (this.shape == SHAPE_ROUND) ? height : ROUND_SQARE;
        Area switchBorderArea = new Area(
                new RoundRectangle2D.Double(offsetX, offsetY, width, height, radius, radius));
        radius = (this.shape == SHAPE_ROUND) ? height - (this.borderSize << 1) : ROUND_SQARE;
        if (this.onOffState == STATE_ON) {
            if (this.isEnabled()) {
                if (this.inMouseOver) {
                    g2d.setColor(ColorUtil.lightenColor(this.switchOnColor, 0.1f));
                } else {
                    g2d.setColor(this.switchOnColor);
                }
            } else {
                g2d.setColor(this.switchOffColor);
            }
        } else {
            Area switchBackgroundArea = new Area(
                    new RoundRectangle2D.Double(offsetX + borderSize,
                            offsetY + this.borderSize,
                            width - (borderSize << 1),
                            height - (borderSize << 1), radius, radius));
            switchBorderArea.subtract(switchBackgroundArea);
            if (this.isEnabled()) {
                g2d.setColor(this.switchBackgroundColor);
                g2d.fill(switchBackgroundArea);
                g2d.setColor(this.switchOffColor);
            } else {
                g2d.setColor(this.switchOffColor);
            }
        }
        g2d.fill(switchBorderArea);
    }

    private void paintSwitch(Graphics2D g2, double width, double height) {
        double offsetX = this.jPanelSwitch.getX();
        double offsetY = this.jPanelSwitch.getY();
        double spaceSize;
        double additionalWidthForPressedMouse = 0d;
        double leftShiftForPressedMouse = 0d;
        if (this.inMouseOver && this.isEnabled()) {
            spaceSize = this.borderSize + this.SPACE;
            if (this.inMousePressed) {
                additionalWidthForPressedMouse = 2;
                if (this.isSelected()) {
                    leftShiftForPressedMouse = 2;
                }
            }
        } else {
            spaceSize = this.borderSize + this.SPACE_MOUSE_OVER;
        }
        double radius;
        if (this.shape == SHAPE_ROUND) {
            radius = height - spaceSize * 2d;
        } else {
            radius = ROUND_SQARE;
        }
        double x;
        double y = spaceSize;
        if (this.onOffState == STATE_ON) {
            x = spaceSize + (width / 2d);
        } else {
            x = spaceSize;
        }
        Area area = new Area(new RoundRectangle2D.Double(
                x + offsetX - leftShiftForPressedMouse,
                y + offsetY,
                height - spaceSize * 2 + additionalWidthForPressedMouse,
                height - spaceSize * 2,
                radius, radius));
        if (this.isEnabled()) {
            if (this.onOffState == STATE_ON) {
                g2.setColor(this.switchBackgroundColor);
            } else {
                g2.setColor(this.switchOffColor);
            }
        } else {
            g2.setColor(this.switchOffColor);
        }
        g2.fill(area);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelSwitch = new javax.swing.JPanel();
        labelOn = new javax.swing.JLabel();
        labelOff = new javax.swing.JLabel();
        jLabelRight = new javax.swing.JLabel();
        jLabelLeft = new javax.swing.JLabel();
        jPanelSpaceRight = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanelSwitch.setMaximumSize(new java.awt.Dimension(40, 20));
        jPanelSwitch.setMinimumSize(new java.awt.Dimension(40, 20));
        jPanelSwitch.setOpaque(false);
        jPanelSwitch.setPreferredSize(new java.awt.Dimension(40, 20));

        labelOn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOn.setMaximumSize(new java.awt.Dimension(1000, 1000));
        labelOn.setPreferredSize(new java.awt.Dimension(5, 5));

        labelOff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOff.setMaximumSize(new java.awt.Dimension(1000, 1000));
        labelOff.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSwitchLayout = new javax.swing.GroupLayout(jPanelSwitch);
        jPanelSwitch.setLayout(jPanelSwitchLayout);
        jPanelSwitchLayout.setHorizontalGroup(
            jPanelSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanelSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelSwitchLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(labelOn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(7, 7, 7)
                    .addComponent(labelOff, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanelSwitchLayout.setVerticalGroup(
            jPanelSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanelSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelSwitchLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanelSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labelOn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelOff, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        add(jPanelSwitch, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jLabelRight, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(jLabelLeft, gridBagConstraints);

        javax.swing.GroupLayout jPanelSpaceRightLayout = new javax.swing.GroupLayout(jPanelSpaceRight);
        jPanelSpaceRight.setLayout(jPanelSpaceRightLayout);
        jPanelSpaceRightLayout.setHorizontalGroup(
            jPanelSpaceRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSpaceRightLayout.setVerticalGroup(
            jPanelSpaceRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jPanelSpaceRight, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (labelOn != null) {
            labelOn.setForeground(fg);
            labelOff.setForeground(fg);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelLeft;
    private javax.swing.JLabel jLabelRight;
    private javax.swing.JPanel jPanelSpaceRight;
    private javax.swing.JPanel jPanelSwitch;
    private javax.swing.JLabel labelOff;
    private javax.swing.JLabel labelOn;
    // End of variables declaration//GEN-END:variables
}
