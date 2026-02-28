//$Header: /as2/de/mendelson/util/NotificationBadgeButton.java 8     18/06/24 12:38 Heller $
package de.mendelson.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Button that contains a notification badge to count something
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class NotificationBadgeButton extends JButton {

    private BufferedImage bufferedImageNotificationBadge = null;
    private Color circleColor = Color.RED;
    private Color foregroundColor = Color.WHITE;

    private final static RenderingHints RENDERING_HINTS_BEST_QUALITY
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

    public NotificationBadgeButton() {
        super();
        this.setUI(new NotificationBadgeButtonUI());
        this.setOpaque(false);
        this.setForeground(this.foregroundColor);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    /**
     * Sets fore- and background color of the notification badge
     *
     * @param circleColor
     * @param foregroundColor
     */
    public void setNotificationBadgeColors(Color circleColor, Color foregroundColor) {
        this.circleColor = circleColor;
        this.foregroundColor = foregroundColor;
        this.setForeground(foregroundColor);
    }

    @Override
    public void setIcon(Icon icon) {
        this.setIcon(icon, false);
    }

    /**
     * Sets an icon for the wrench. 
     * 
     *
     * @param icon
     * @param adjustColor Indicates if the system should automatically adjust the contrast
     */
    public void setIcon(Icon icon, boolean adjustColor) {
        super.setIcon(icon);
        this.bufferedImageNotificationBadge = this.generateImageFromIcon((ImageIcon) icon, adjustColor);
        int gap = (int) (icon.getIconHeight() / 1.6f);
        this.setBorder(new EmptyBorder(0, 0, 0, gap));
    }

    private BufferedImage generateImageFromIcon(ImageIcon icon, boolean adjustColor) {
        if (this.getIcon() != null) {                        
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHints(RENDERING_HINTS_BEST_QUALITY);
            g2d.drawImage(icon.getImage(), 0, 0, null);
            g2d.dispose();
            if (adjustColor) {
                //find the highest possible contrast
                int luminanceBackground = ColorUtil.calculateLuminance( this.getBackground() );
                //darker or lighten 300%
                if( luminanceBackground < 128){                    
                    ImageUtil.adjustBrightness(image, 3.0f);
                }else{
                    ImageUtil.adjustBrightness(image, -3.0f);
                }
            }
            return (image);
        } else {
            return (null);
        }
    }

    private class NotificationBadgeButtonUI extends BasicButtonUI {

        @Override
        protected void paintIcon(Graphics g, JComponent component, Rectangle iconRectangle) {
            if (bufferedImageNotificationBadge != null && getText() != null && !getText().trim().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHints(RENDERING_HINTS_BEST_QUALITY);
                double maxBadgeCircleSize = Math.max(iconRectangle.getWidth() * 0.7f,
                        iconRectangle.getHeight() * 0.7f);
                double x = Math.min(iconRectangle.getX() + iconRectangle.getWidth() / 2f,
                        component.getWidth() - maxBadgeCircleSize);
                double y = Math.max(iconRectangle.getY() - maxBadgeCircleSize * 0.2f, 0);
                Area area = new Area(iconRectangle);
                area.subtract(new Area(new Ellipse2D.Double(x - 1, y, maxBadgeCircleSize + 1, maxBadgeCircleSize + 1)));
                g2d.drawImage(bufferedImageNotificationBadge, null, iconRectangle.x, iconRectangle.y);
                this.displayNotificationText(g2d, x, y, maxBadgeCircleSize, getText());
                g2d.dispose();
            } else {
                //no notification - just draw the icon
                super.paintIcon(g, component, iconRectangle);
            }
        }

        private void displayNotificationText(Graphics2D g2d, double x, double y, double size, String notificationText) {
            g2d.setFont(getFont().deriveFont((float) size * 0.8f));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            String displayText = notificationText;
            if (notificationText.length() > 2) {
                displayText = "99+";
            }
            Rectangle2D textRectangle = fontMetrics.getStringBounds(displayText, g2d);
            double gap = (displayText.length() - 1) * size * 0.05f;
            double width = Math.max(textRectangle.getWidth(), size);
            g2d.setColor(circleColor);
            g2d.translate(x, y);
            g2d.fill(new RoundRectangle2D.Double(0, 0, width + gap * 2, size, size, size));
            double textLocationX = ((width - textRectangle.getWidth()) / 2f);
            double textLocationY = ((size - textRectangle.getHeight()) / 2f);
            g2d.setColor(foregroundColor);
            g2d.drawString(displayText, (int) (textLocationX + gap), (int) (textLocationY + fontMetrics.getAscent()));
        }

        @Override
        protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
            //this is done in displayNotificationText
        }

    }

}
