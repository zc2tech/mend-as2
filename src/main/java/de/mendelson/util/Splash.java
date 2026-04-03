//$Header: /converteride/de/mendelson/util/Splash.java 62    28/02/25 15:38 Heller $
package de.mendelson.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Splash window to been shown while one of the mendelson products load
 *
 * @author S.Heller
 * @version $Revision: 62 $
 */
public class Splash extends JWindow implements SwingConstants {

    /**
     * Image to display
     */
    private final BufferedImage splashImage;
    /**
     * PrintStream to pass to out components to let them write stuff into
     */
    private SplashPrintWriter out = null;
    /**
     * Indicates if this splash should have a progress bar, this is done if this
     * is != null
     */
    private Progress progress = null;
    /**
     * List of display string to display static in the Splash
     */
    private final List<DisplayString> displayStringList = Collections.synchronizedList(new ArrayList<DisplayString>());
    /**
     * Indicates if textual output should be anti aliased in the splash
     */
    private boolean textAntialiasing = true;

    /**
     * Creates a new splash with the given width and length
     *
     * @param imageResource ResourcePath to the image
     * @param imageHeight Scaling height of the splash - only valid if this is a
     * SVG, else the image size is taken
     */
    public Splash(String imageResource, int imageHeight) {
        this.splashImage = this.loadImage(imageResource, imageHeight);
        float width = (float) this.splashImage.getWidth(this);
        float height = (float) this.splashImage.getHeight(this);
        this.getContentPane().setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - (int) width) / 2,
                (screenSize.height - (int) height) / 2, (int) width, (int) height);
    }

    /**
     * Allows to load an image resource
     *
     * @deprecated (This will load a bitmap as Splash - but this should not be
     * used becasue of the missing scalability)
     */
    @Deprecated(since = "2020")
    public Splash(String imageResource) {
        this(imageResource, 330);
    }

    /**
     * Adds a static display string to the splash, this is always painted using
     * the passed parameters
     *
     * @param font Font to use to display the text in the splash
     * @param x X position of the output
     * @param y y position of the output
     * @param text Text to display
     * @param color Color to use for the text display
     */
    public void addDisplayString(Font font, int x, int y, String text, Color color, AffineTransform fontTransform) {
        DisplayString displayString = new DisplayString(font, x, y, text, color, fontTransform);
        synchronized (this.displayStringList) {
            this.displayStringList.add(displayString);
        }
    }

    /**
     * Adds a static display string to the splash, this is always painted using
     * the passed parameters
     *
     * @param font Font to use to display the text in the splash
     * @param x X position of the output
     * @param y y position of the output
     * @param text Text to display
     * @param color Color to use for the text display
     */
    public void addDisplayString(Font font, int x, int y, String text, Color color) {
        this.addDisplayString(font, x, y, text, color, null);
    }

    /**
     * Defines where to write the output to, with which properties
     *
     * @param font Font to use
     * @param x X Position of the output
     * @param y Y Position of the output
     * @param fontColor Font color to use
     */
    public PrintWriter createPrintWriter(Font font, int x, int y, Color fontColor) {
        StringBuilder builder = new StringBuilder();
        StringBuilderOutputStream outStream = new StringBuilderOutputStream(this, builder);
        this.out = new SplashPrintWriter(outStream, builder, font, x, y, fontColor);
        return (this.out);
    }

    /**
     * Passes a progress container to this splash. By passing a progress this is
     * shown!
     *
     * @param x XPos of the bar
     * @param y yPos of the bar
     * @param height Bars height
     * @param width Bars width
     * @param borderColor set this to null to not draw a border
     * @param foregroundColor foreground color of the bar
     * @param backgroundColor set this to null to have a transparent background
     * @param showPercent indicates to show the progress in procent
     */
    public Splash.Progress createProgress(
            int x, int y, int height, int width,
            Color foregroundColor,
            Color backgroundColor,
            Color borderColor, boolean showPercent) {
        this.progress = new Splash.Progress((JWindow) this,
                x, y,
                height, width,
                foregroundColor, backgroundColor, borderColor, showPercent);
        return (this.progress);
    }

    /**
     * Enables or disables the antialiasing of text output on the Splash
     */
    public void setTextAntiAliasing(boolean textAntialiasing) {
        this.textAntialiasing = textAntialiasing;
    }

    /**
     * Sets the rendering hints for any rendering of this splash
     */
    private void setRenderingHints(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (this.textAntialiasing) {
            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }

    /**
     * Draw/update this component
     */
    @Override
    public void paint(Graphics g) {
        if (this.splashImage == null) {
            return;
        }
        //draw into the memory image off-screen
        BufferedImage offScreenImage = new BufferedImage(
                this.splashImage.getWidth(this), this.splashImage.getHeight(this),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D offScreenGraphics = (Graphics2D) offScreenImage.getGraphics();
        this.setRenderingHints(offScreenGraphics);
        //assume a white background
        offScreenGraphics.setColor(Color.WHITE);
        offScreenGraphics.fillRect(0, 0, offScreenImage.getWidth(this), offScreenImage.getHeight(this));
        //copy the image into memory, off-screen
        offScreenGraphics.drawImage(this.splashImage, 0, 0, this);
        //add the static texts to the image, off-screen
        FontRenderContext renderContext = offScreenGraphics.getFontRenderContext();
        synchronized (this.displayStringList) {
            for (int i = 0; i < this.displayStringList.size(); i++) {
                DisplayString displayString = (DisplayString) this.displayStringList.get(i);
                TextLayout layout = new TextLayout(displayString.getText(),
                        displayString.getFont(), renderContext);
                AffineTransform transformPosition = new AffineTransform();
                transformPosition.setToTranslation(
                        displayString.getX(), displayString.getY());
                //only concat a transform if the display string has one
                if (displayString.getFontTransform() != null) {
                    transformPosition.concatenate(displayString.getFontTransform());
                }
                Shape shape = layout.getOutline(transformPosition);
                offScreenGraphics.setColor(displayString.getColor());
                offScreenGraphics.fill(shape);
            }
        }
        //draw printstream output
        if (this.out != null) {
            offScreenGraphics.setFont(this.out.getFont());
            offScreenGraphics.setColor(this.out.getFontColor());
            offScreenGraphics.drawString(this.out.getText(),
                    this.out.getX(), this.out.getY());
        }
        //draw progress bar
        if (this.progress != null) {
            if (this.progress.getBorder() != null) {
                offScreenGraphics.setColor(this.progress.getBorder());
                offScreenGraphics.drawRect(this.progress.getX() - 1, this.progress.getY() - 1,
                        this.progress.getWidth() + 1, this.progress.getHeight() + 1);
            }
            if (this.progress.getBackground() != null) {
                if (this.progress.getUseGradientPaint()) {
                    //draw background of the progress bar with 3d effects
                    GradientPaint paint = new GradientPaint(
                            this.progress.getX(),
                            this.progress.getY() - this.progress.getHeight(),
                            this.progress.getBackground().darker(),
                            this.progress.getX(),
                            this.progress.getY() + this.progress.getHeight() / 2,
                            this.progress.getBackground());
                    offScreenGraphics.setPaint(paint);
                } else {
                    offScreenGraphics.setPaint(progress.getBackground());
                }
                offScreenGraphics.fillRect(this.progress.getX(), this.progress.getY(),
                        this.progress.getWidth(), this.progress.getHeight() / 2);
                if (this.progress.getUseGradientPaint()) {
                    //draw background of the progress bar with 3d effects
                    GradientPaint paint = new GradientPaint(
                            this.progress.getX(),
                            this.progress.getY() + this.progress.getHeight() / 2,
                            this.progress.getBackground(),
                            this.progress.getX(),
                            this.progress.getY() + 2 * this.progress.getHeight(),
                            this.progress.getBackground().brighter());
                    offScreenGraphics.setPaint(paint);
                } else {
                    offScreenGraphics.setPaint(progress.getBackground());
                }
                offScreenGraphics.fillRect(this.progress.getX(),
                        this.progress.getY() + this.progress.getHeight() / 2,
                        this.progress.getWidth(), this.progress.getHeight() / 2);
            }
            if (this.progress.getUseGradientPaint()) {
                //draw bar itself, with 3D effect
                GradientPaint paint = new GradientPaint(
                        this.progress.getX(), this.progress.getY(),
                        this.progress.getForeground(),
                        this.progress.getX(),
                        this.progress.getY() + this.progress.getHeight(),
                        Color.white);
                offScreenGraphics.setPaint(paint);
            } else {
                offScreenGraphics.setPaint(progress.getForeground());
            }
            offScreenGraphics.fillRect(this.progress.getX(), this.progress.getY(),
                    this.progress.getProgress(), this.progress.getHeight() / 2);
            if (this.progress.getUseGradientPaint()) {
                //draw bar itself, with 3D effect
                GradientPaint paint = new GradientPaint(
                        this.progress.getX(), this.progress.getY(),
                        Color.white,
                        this.progress.getX(),
                        this.progress.getY() + this.progress.getHeight(),
                        this.progress.getForeground());
                offScreenGraphics.setPaint(paint);
            } else {
                offScreenGraphics.setPaint(progress.getForeground());
            }
            offScreenGraphics.fillRect(this.progress.getX(),
                    this.progress.getY() + this.progress.getHeight() / 2,
                    this.progress.getProgress(), this.progress.getHeight() / 2);
            if (this.progress.showPercent()) {
                TextLayout layout = new TextLayout(this.progress.getPercent(),
                        new Font(Font.DIALOG, Font.PLAIN, (int) (this.progress.getHeight() / 2)),
                        renderContext);
                AffineTransform transformPosition = new AffineTransform();
                transformPosition.setToTranslation(
                        this.progress.getX() + this.progress.getWidth() / 2,
                        (int) (this.progress.getY() + this.progress.getHeight() - this.progress.getHeight() * 0.25));
                Shape shape = layout.getOutline(transformPosition);
                offScreenGraphics.setColor(progress.getForeground());
                offScreenGraphics.fill(shape);
                offScreenGraphics.clip(new Rectangle(this.progress.getX(), this.progress.getY(),
                        this.progress.getProgress(), this.progress.getHeight()));
                offScreenGraphics.setColor(progress.getBackground());
                offScreenGraphics.fill(shape);
            }
        }
        offScreenGraphics.dispose();
        Graphics2D g2d = (Graphics2D) g;
        this.setRenderingHints(g2d);
        //bring the image to the screen
        g2d.setPaintMode();
        g2d.drawImage(offScreenImage, 0, 0, offScreenImage.getWidth(this),
                offScreenImage.getHeight(this), this);
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        // Return the buffered image
        return bufferedImage;
    }

    /**
     * Loads the image and tracks it
     *
     * @param resource image resource to load the image
     */
    private BufferedImage loadImage(String resource, int imageHeight) {
        if (resource.endsWith(".svg")) {
            MendelsonMultiResolutionImage image
                    = MendelsonMultiResolutionImage.fromSVG(resource, imageHeight,
                            MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT);
            return (this.toBufferedImage(new ImageIcon(image.toMinResolution(imageHeight)).getImage()));
        } else {
            BufferedImage image = this.loadImageAsBitmap(resource);
            return (image);
        }
    }

    private BufferedImage loadImageAsBitmap(String resource) {
        BufferedImage bufferedImage = null;
        try {
            //get an input stream from the resource
            try (InputStream inStream = Splash.class.getResourceAsStream(resource)) {
                bufferedImage = ImageIO.read(inStream);
            }
        } catch (Throwable e) {
            System.err.println("Fatal: Unable to load splash image resource " + resource + ".");
            System.exit(-1);
        }
        return (bufferedImage);
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            this.toFront();
        }
    }

    /**
     * removes the splash window
     */
    public void destroy() {
        this.setVisible(false);
        if (this.out != null) {
            try {
                this.out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * PrintStream that will stores also information about position of the
     * output and refreshed the passed component whenever a \n occurs
     */
    public static class SplashPrintWriter extends PrintWriter {

        /**
         * Font to use for the text printstream output in splash
         */
        private final Font font;
        /**
         * Font color to use in splash
         */
        private final Color fontColor;
        /**
         * Position to use to write output to
         */
        private final int outputX;
        private final int outputY;
        private final StringBuilder builder;

        /**
         * @param @param outStream stream to write entries to a string buffer
         * @param font Font to use
         * @param x X Position of the output
         * @param y Y Position of the output
         * @param fontColor Font color to use
         */
        public SplashPrintWriter(OutputStream outStream,
                StringBuilder builder,
                Font font, int x, int y, Color fontColor) {
            super(outStream, true, StandardCharsets.UTF_8);
            this.builder = builder;
            this.font = font;
            this.outputX = x;
            this.outputY = y;
            this.fontColor = fontColor;
        }

        public Font getFont() {
            return (this.font);
        }

        public int getX() {
            return (this.outputX);
        }

        public int getY() {
            return (this.outputY);
        }

        public Color getFontColor() {
            return (this.fontColor);
        }

        public String getText() {
            return (this.builder.toString());
        }
    }

    /**
     * Output Stream that writes it output to a stringbuffer, the buffer is only
     * refreshed if a \n occurs
     *
     */
    public static class StringBuilderOutputStream extends OutputStream {

        private final StringBuilder builder;
        private final Component component;
        /**
         * TempBuffer is necessary because an update of the component could be
         * forced outside this class, the LAST valid value is always in the
         * passed buffer pointer. Whenever a \n accurs, the pass buffer will get
         * a valid value and store it until the next \n appears
         */
        private final StringBuilder tempBuilder = new StringBuilder();

        /**
         * @param component Component to update on an end of a line
         * @param builder Buffer to write output to
         */
        public StringBuilderOutputStream(Component component, StringBuilder builder) {
            this.builder = builder;
            this.component = component;
        }

        @Override
        public void write(byte[] b, int off, int len) {
            String logStr = new String(b, off, len, StandardCharsets.UTF_8);
            for (char logChar : logStr.toCharArray()) {
                if (logChar == '\n') {
                    this.builder.delete(0, this.builder.length());
                    this.builder.append(tempBuilder.toString());
                    if (this.component.getGraphics() != null) {
                        this.component.update(this.component.getGraphics());
                    }
                    tempBuilder.delete(0, this.builder.length());
                } else {
                    this.tempBuilder.append(logChar);
                }
            }
        }

        @Override
        public void write(int b) {
            //works only for a single byte
            this.builder.append((char) (b & 0xFF));
        }

    }

    /**
     * Class that stores progress information
     */
    public static class Progress {

        /**
         * Position and size of the progress bar
         */
        private final int x;
        private final int y;
        private final int height;
        private final int width;
        /**
         * Progress bar border
         */
        private final Color backgroundColor;
        /**
         * Progress bar color itself
         */
        private final Color foregroundColor;
        /**
         * Border color for the progress bar
         */
        private final Color borderColor;
        /**
         * Progress state
         */
        private int maxProgress = 0;
        private int actualProgress = 0;
        private final boolean showPercent;
        /**
         * Format to format the percent output
         */
        private final DecimalFormat format = new DecimalFormat("0.0");
        /**
         * Indicates if the bar should be rendered with a 3D effect
         */
        private boolean useGradientPaint = true;
        private final JWindow parent;

        /**
         * Initialize the progress bar
         *
         * @param x XPos of the bar
         * @param y yPos of the bar
         * @param height Bars height
         * @param width Bars width
         * @param backgroundColor color, set this to null to have a transparent
         * background
         * @param foregroundColor bar color
         * @param borderColor progress bar border color set this to null to not
         * have a border
         * @param showPercent indicates to show the progress in procent
         */
        public Progress(JWindow parent, int x, int y, int height, int width,
                Color foregroundColor, Color backgroundColor, Color borderColor, boolean showPercent) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.showPercent = showPercent;
            this.parent = parent;
        }

        /**
         * Number of steps untill 100%
         */
        public void setMax(int max) {
            this.maxProgress = max;
        }

        public int getX() {
            return (this.x);
        }

        public int getY() {
            return (this.y);
        }

        public int getWidth() {
            return (this.width);
        }

        public int getHeight() {
            return (this.height);
        }

        /**
         * indicates if the progress bar should be painted using a 3D effect
         */
        public void setUseGradientPaint(boolean useGradientPaint) {
            this.useGradientPaint = useGradientPaint;
        }

        /**
         * indicates if the progress bar should be painted using a 3D effect
         */
        public boolean getUseGradientPaint() {
            return (this.useGradientPaint);
        }

        /**
         * Increates the progress
         */
        public void inc() {
            this.actualProgress++;
            //force a graphic refresh
            this.parent.update(this.parent.getGraphics());
        }

        /**
         * gets the progress in pixel, depends on the width!
         */
        public int getProgress() {
            return ((int) (((float) this.width / (float) maxProgress) * (float) this.actualProgress));
        }

        /**
         * Background color
         */
        public Color getBackground() {
            return (this.backgroundColor);
        }

        /**
         * Foreground color
         */
        public Color getForeground() {
            return (this.foregroundColor);
        }

        /**
         * Border color
         */
        public Color getBorder() {
            return (this.borderColor);
        }

        public boolean showPercent() {
            return (this.showPercent);
        }

        /**
         * Returns the percent string to show
         */
        public String getPercent() {
            return (format.format(this.getPercentValue()) + '%');
        }

        /**
         * Returns the computed progress percent
         */
        public float getPercentValue() {
            float percent = 0.0f;
            if (this.maxProgress > 0) {
                percent = (float) this.actualProgress * 100f / (float) this.maxProgress;
            }
            return (percent);
        }
    }

    /**
     * Class that stores output strings to display in the splash screen. This
     * text will appear on each update of the component!
     */
    public static class DisplayString {

        /**
         * Font to use for the output
         */
        private final Font font;
        /**
         * Position where to output the string
         */
        private final float x;
        private final float y;
        private final String text;
        private final Color color;
        private final AffineTransform fontTransform;

        /**
         * @param font Font to use to display the text in the splash
         * @param x X position of the output
         * @param y y position of the output
         * @param text Text to display
         * @param color Color to use for the text display
         */
        public DisplayString(Font font, float x, float y, String text, Color color,
                AffineTransform fontTransform) {
            this.font = font;
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            this.fontTransform = fontTransform;
        }

        public float getX() {
            return (this.x);
        }

        public float getY() {
            return (this.y);
        }

        public Font getFont() {
            return (this.font);
        }

        public String getText() {
            return (this.text);
        }

        public Color getColor() {
            return (this.color);
        }

        public AffineTransform getFontTransform() {
            return (this.fontTransform);
        }
    }
}
