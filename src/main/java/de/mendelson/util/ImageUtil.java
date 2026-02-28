//$Header: /as2/de/mendelson/util/ImageUtil.java 18    11/02/25 13:39 Heller $
package de.mendelson.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.util.List;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class that contains routines for the image processing
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class ImageUtil {

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

    /**
     * Its just a utility class..
     */
    private ImageUtil() {
    }

    /**
     * Replaces a single color in the passed image and returns the new one.
     * Color is RGBA - but this method will ignore transparency during replace.
     *
     * @param background original image to set new rgb values in
     * @param colorOld The old color to replace
     * @param colorNew Replacing color
     */
    public static ImageIcon replaceColor(ImageIcon background, Color colorOld, Color colorNew) {
        int oldColorRGB = colorOld.getRGB() & 0x00FFFFFF;
        int newColorRGB = colorNew.getRGB();
        BufferedImage image = new BufferedImage(
                background.getIconWidth(),
                background.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(background.getImage(), 0, 0, null);
        for (int x = 0; x < background.getIconWidth(); x++) {
            for (int y = 0; y < background.getIconHeight(); y++) {
                if ((image.getRGB(x, y) & 0x00FFFFFF) == oldColorRGB) {
                    image.setRGB(x, y, newColorRGB);
                }
            }
        }
        g.dispose();
        return (new ImageIcon(image));
    }

    /**
     * Replaces a single color in the passed image and returns the new one
     *
     * @param background original image to set new rgb values in
     * @param hexColorOld RGB hex str for the old color to replace
     * @param hexColorNew RGB hex str for the new, replacing color
     */
    public static ImageIcon replaceColor(ImageIcon background, String hexColorOld, String hexColorNew) {
        if (hexColorOld.startsWith("#")) {
            hexColorOld = hexColorOld.substring(1);
        }
        if (hexColorNew.startsWith("#")) {
            hexColorNew = hexColorNew.substring(1);
        }
        if (!hexColorOld.startsWith("0x")) {
            hexColorOld = "0x" + hexColorOld;
        }
        if (!hexColorNew.startsWith("0x")) {
            hexColorNew = "0x" + hexColorNew;
        }
        Color oldColor = Color.decode(hexColorOld);
        Color newColor = Color.decode(hexColorNew);
        return (replaceColor(background, oldColor, newColor));
    }

    /**
     * Forms a web known hex string from a given color The output format is
     * "#RRGGBB"
     */
    public static String toHex(Color color) {
        StringBuilder builder = new StringBuilder();
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());
        if (red.length() < 2) {
            builder.append("0");
        }
        builder.append(red);
        if (green.length() < 2) {
            builder.append("0");
        }
        builder.append(green);
        if (blue.length() < 2) {
            builder.append("0");
        }
        builder.append(blue);
        return ("#" + builder.toString());
    }

    /**
     * Mixes two images, the foreground image is painted onto the background
     * image
     *
     * @param background Background image, is painted first
     * @param foreground Foreground image, is painted second
     */
    public static ImageIcon mixImages(ImageIcon background, ImageIcon foreground) {
        final int backgroundIconWidth = background.getIconWidth();
        final int backgroundIconHeight = background.getIconHeight();
        BufferedImage image = new BufferedImage(
                backgroundIconWidth,
                backgroundIconHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.drawImage(background.getImage(), 0, 0, null);
        int foregroundOffsetX = backgroundIconWidth - foreground.getIconWidth();
        if (foregroundOffsetX < 0) {
            foregroundOffsetX = 0;
        }
        int foregroundOffsetY = backgroundIconWidth - foreground.getIconHeight();
        if (foregroundOffsetY < 0) {
            foregroundOffsetY = 0;
        }
        g2d.drawImage(foreground.getImage(), foregroundOffsetX, foregroundOffsetY, null);
        return (new ImageIcon(image));
    }

    /**
     * Turns the passed icon into a transparent image, this is used to mark a
     * hidden element
     */
    public static ImageIcon transparentImage(ImageIcon icon) {
        BufferedImage image = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        image = setOpacity(image, 0.5f);
        return (new ImageIcon(image));
    }

    /**
     * Turns the passed icon into a grayed out and returns it
     */
    public static ImageIcon grayImage(ImageIcon icon) {
        return new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
    }

    /**
     * Scales a passed image icon to a new size and returns this
     */
    public static ImageIcon scaleWidthKeepingProportions(ImageIcon icon, int newWidth) {
        BufferedImage sourceImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = sourceImage.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        BufferedImage targetImage = scaleWidthKeepingProportions(sourceImage, newWidth);
        return (new ImageIcon(targetImage));
    }

    /**
     * Scales a passed image icon to a new size and returns this
     */
    public static ImageIcon scaleHeightKeepingProportions(ImageIcon icon, int newHeight) {
        BufferedImage sourceImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = sourceImage.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        BufferedImage targetImage = scaleHeightKeepingProportions(sourceImage, newHeight);
        return (new ImageIcon(targetImage));
    }

    /**
     * Scales a passed buffered image to a new size and returns this - keeping
     * the proportions
     *
     * @return
     */
    public static BufferedImage scale(BufferedImage sourceImage, int newWidth, int newHeight) {
        BufferedImage targetImage = new BufferedImage(newWidth, newHeight, sourceImage.getType());
        Graphics2D g = targetImage.createGraphics();
        RenderingHints renderingHints = g.getRenderingHints();
        renderingHints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        g.drawImage(sourceImage, 0, 0, newWidth, newHeight, 0, 0, sourceImage.getWidth(),
                sourceImage.getHeight(), null);
        g.dispose();
        return (targetImage);
    }

    /**
     * Scales a passed buffered image to a new width and returns this
     *
     * @return
     */
    public static BufferedImage scaleWidthKeepingProportions(BufferedImage sourceImage, int newWidth) {
        double scaleFactor = (double) newWidth / (double) sourceImage.getWidth();
        return (scale(sourceImage, (int) (sourceImage.getWidth() * scaleFactor),
                (int) (sourceImage.getHeight() * scaleFactor)));
    }

    /**
     * Scales a passed buffered image to a new width and returns this
     *
     * @return
     */
    public static BufferedImage scaleHeightKeepingProportions(BufferedImage sourceImage, int newHeight) {
        double scaleFactor = (double) newHeight / (double) sourceImage.getHeight();
        return (scale(sourceImage, (int) (sourceImage.getWidth() * scaleFactor), (int) (sourceImage.getHeight() * scaleFactor)));
    }

    public static BufferedImage drawCenteredImageInImage(BufferedImage backgroundImage, BufferedImage frontImage) {
        Graphics2D graphics = backgroundImage.createGraphics();
        int frontX = (backgroundImage.getWidth() / 2) - (frontImage.getWidth() / 2);
        int frontY = (backgroundImage.getHeight() / 2) - (frontImage.getHeight() / 2);
        graphics.drawImage(
                frontImage,
                frontX, frontY, null);
        graphics.dispose();
        return (backgroundImage);
    }

    public static BufferedImage blur(BufferedImage image) {
        Kernel kernel = new Kernel(3, 3,
                new float[]{
                    1f / 9f, 1f / 9f, 1f / 9f,
                    1f / 9f, 1f / 9f, 1f / 9f,
                    1f / 9f, 1f / 9f, 1f / 9f});
        BufferedImageOp op = new ConvolveOp(kernel);
        image = op.filter(image, null);
        return (image);
    }

    public static BufferedImage setOpacity(BufferedImage source, float opacity) {
        BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D graphics = newImage.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        return (newImage);
    }

    /**
     * Generates a disabled image from a passed MultiResolutionImage.
     *
     * @return
     */
    public static MendelsonMultiResolutionImage generateDisabledImage(MendelsonMultiResolutionImage sourceImageMultiResolution) {
        List<Image> sourceImageList = sourceImageMultiResolution.getResolutionVariants();
        Image[] disabledImageArray = new Image[sourceImageList.size()];
        for (int i = 0; i < sourceImageList.size(); i++) {
            disabledImageArray[i] = GrayFilter.createDisabledImage(sourceImageList.get(i));
        }
        MendelsonMultiResolutionImage disabledImage = new MendelsonMultiResolutionImage(disabledImageArray);
        return (disabledImage);
    }

    public static ImageIcon iconToImageIcon(Icon icon) {
        if (icon instanceof ImageIcon) {
            return (ImageIcon) icon;
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge
                    = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return (new ImageIcon(image));
        }
    }

    /**
     *
     * @param image The image to adjust
     * @param brightness A brightness value of 0.9 will darken the image by 10%,
     * a value of 1.1 will lighten the image by 10%. A value of 1 will keep the
     * brightness.
     */
    public static void adjustBrightness(BufferedImage image, float brightness) {
        RescaleOp rescaleOp = new RescaleOp(brightness, 0, null);
        rescaleOp.filter(image, image);
    }

    /**
     *
     * @param image The image to adjust
     * @param brightness A brightness value of 0.9 will darken the image by 10%,
     * a value of 1.1 will lighten the image by 10%. A value of 1 will keep the
     * brightness.
     */
    public static ImageIcon adjustBrightness(ImageIcon icon, float brightness) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_BEST_QUALITY);
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();
        adjustBrightness(image, brightness);
        return (new ImageIcon(image));
    }

}
