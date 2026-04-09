package de.mendelson.util.balloontip;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Help panel that is bound to a tooltip and could be used in the user interface
 * to explain details direct in the UI
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class JPanelUIHelp extends JPanel {

    private final static MendelsonMultiResolutionImage IMAGE_HELP
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/balloontip/help.svg", 8, 48);

    private final int GAP_X = 10;
    private int maxTooltipWidth = 200;

    private Color balloontipBackground = Color.LIGHT_GRAY;
    private Color balloontipForeground = Color.BLACK;
    private Color balloontipBorder = Color.DARK_GRAY;
    private boolean customColorsUsed = false;
    private BalloonToolTip balloonTip = null;
    private String originalTooltipText = null;
    private int triangleAlignment = BalloonToolTip.TRIANGLE_ALIGNMENT_TOP;

    private final static Map<String, Path> imageFileCache = new ConcurrentHashMap<String, Path>();
    
    public final static int TRIANGLE_ALIGNMENT_CENTER = BalloonToolTip.TRIANGLE_ALIGNMENT_CENTER;
    public final static int TRIANGLE_ALIGNMENT_TOP = BalloonToolTip.TRIANGLE_ALIGNMENT_TOP;
    public final static int TRIANGLE_ALIGNMENT_BOTTOM = BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM;
    

    /**
     * Creates new form JPanelUIHelp
     */
    public JPanelUIHelp() {
        initComponents();
        this.setMultiresolutionIcons();
        //this is a just bad code - but there seems no way to modify a single tooltip timing,
        //no idea to solve this without this hack
        final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
        final int defaultDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
        final int defaultReshowDelay = ToolTipManager.sharedInstance().getReshowDelay();
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                ToolTipManager.sharedInstance().setInitialDelay(200);
                ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
                ToolTipManager.sharedInstance().setReshowDelay(0);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
                ToolTipManager.sharedInstance().setDismissDelay(defaultDismissDelay);
                ToolTipManager.sharedInstance().setReshowDelay(defaultReshowDelay);
            }
        });
    }

    private void setMultiresolutionIcons() {
        int imageSize = Math.min(this.getPreferredSize().height, this.getPreferredSize().width);
        ImageIcon icon = new ImageIcon(IMAGE_HELP.toMinResolution((int) (imageSize * 0.7f)));
        this.jLabelImage.setIcon(icon);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.jLabelImage.setEnabled(enabled);
    }

    /**
     * Modifies the tooltip text if it is in HTML format: adds a width
     */
    private String addWidthParameterToTooltipText(String tooltipText, int width) {
        StringBuilder tipText = new StringBuilder();
        if (tooltipText != null
                && tooltipText.toUpperCase().startsWith("<HTML>")
                && tooltipText.toUpperCase().endsWith("</HTML>")) {
            tipText.append("<HTML>")
                    .append("<p style=\"width:")
                    .append(String.valueOf(width))
                    .append("px;\">")
                    .append(tooltipText.substring(6, tooltipText.length() - 7))
                    .append("</p></HTML>");
            return (tipText.toString());
        } else {
            return (tooltipText);
        }
    }

    /**
     * Deals with image references in tool tip texts. Uses a really bad parsing
     * approach but it should be enough because the parsed HTML is always under
     * control of the application itself. The image tag has to be exactly in
     * this format:
     * <img src="/de/mendelson/../image.svg" height="nn" width="nn"/>
     *
     */
    private String handleSVGImageInTooltipText(String tooltipText) throws Exception {
        if (tooltipText == null
                || !tooltipText.toUpperCase().startsWith("<HTML>")
                || !tooltipText.toUpperCase().endsWith("</HTML>")) {
            return (tooltipText);
        }
        int checkIndex = 0;
        while (tooltipText.toUpperCase().indexOf("<IMG ", checkIndex) >= 0) {
            int imgStart = tooltipText.toUpperCase().indexOf("<IMG ", checkIndex);
            int imgEnd = tooltipText.indexOf(">", imgStart);
            if (imgEnd != -1) {
                int sourceStart = tooltipText.toUpperCase().indexOf("SRC=\"", imgStart);
                if (sourceStart > 0 && sourceStart + 5 < tooltipText.length()) {
                    sourceStart += 5;
                    int sourceEnd = tooltipText.indexOf("\"", sourceStart + 5);
                    String source = tooltipText.substring(sourceStart, sourceEnd);
                    int width = -1;
                    int height = -1;
                    int heightStart = tooltipText.toUpperCase().indexOf("HEIGHT=\"", imgStart);
                    if (heightStart != -1 && heightStart + 8 < tooltipText.length()) {
                        int heightEnd = tooltipText.indexOf("\"", heightStart + 8);
                        String heightStr = tooltipText.substring(heightStart + 8, heightEnd);
                        try {
                            height = Integer.parseInt(heightStr);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    int widthStart = tooltipText.toUpperCase().indexOf("WIDTH=\"", imgStart);
                    if (widthStart != -1 && widthStart + 7 < tooltipText.length()) {
                        int widthEnd = tooltipText.indexOf("\"", widthStart + 7);
                        String widthStr = tooltipText.substring(widthStart + 7, widthEnd);
                        try {
                            width = Integer.parseInt(widthStr);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    Path imageFile = this.getSVGImagePathFromCache(source, height, width);
                    tooltipText = tooltipText.substring(0, imgStart)
                            + "<img src=\""
                            + imageFile.toUri()
                            + "\">"
                            + tooltipText.substring(imgEnd + 1);
                    checkIndex = imgEnd + 1;
                }
            }
        }
        return (tooltipText);
    }

    /**
     * Checks if the resource is already stored as file and stores it if it does
     * not exist
     */
    private Path getSVGImagePathFromCache(String resource, int height, int width) throws Exception {
        String imageStr = resource + "," + String.valueOf(height) + "," + String.valueOf(width);
        if (!imageFileCache.containsKey(imageStr)) {
            MendelsonMultiResolutionImage tempImage = null;
            if (height > 0) {
                tempImage
                        = MendelsonMultiResolutionImage.fromSVG(resource, height,
                                MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT);
            } else if (width > 0) {
                tempImage
                        = MendelsonMultiResolutionImage.fromSVG(resource, width,
                                MendelsonMultiResolutionImage.SVGScalingOption.KEEP_WIDTH);
            } else {
                //fallback to default size
                tempImage = MendelsonMultiResolutionImage.fromSVG(resource, 20);
            }
            ImageIcon imageIcon = new ImageIcon(tempImage.toMinResolution(height));
            BufferedImage writeableImage = new BufferedImage(
                    imageIcon.getIconWidth(),
                    imageIcon.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = writeableImage.createGraphics();
            // paint the Icon to the BufferedImage.
            imageIcon.paintIcon(null, g, 0, 0);
            g.dispose();
            Path imagePath = this.saveImage(writeableImage, 0.8f);
            imageFileCache.put(imageStr, imagePath);
        }
        return (imageFileCache.get(imageStr));
    }

    private Path saveImage(BufferedImage image, float quality) throws IOException {
        Path jpegFiletoSave = Files.createTempFile("tooltip", ".jpg");
        // save jpeg image with specific quality. "1f" corresponds to 100% , "0.7f" corresponds to 70%
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        try {
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(quality);
            jpgWriter.setOutput(ImageIO.createImageOutputStream(jpegFiletoSave.toFile()));
            IIOImage outputImage = new IIOImage(image, null, null);
            jpgWriter.write(null, outputImage, jpgWriteParam);
        } finally {
            jpgWriter.dispose();
        }
        return (jpegFiletoSave);
    }

    @Override
    public void setToolTipText(String tooltipText) {
        this.originalTooltipText = tooltipText;
        String modifiedTooltipText = this.addWidthParameterToTooltipText(tooltipText, this.maxTooltipWidth);
        try {
            modifiedTooltipText = this.handleSVGImageInTooltipText(modifiedTooltipText);
        } catch (Exception e) {
            e.printStackTrace();
            //ignore the image problem - just dont display it
        }
        super.setToolTipText(modifiedTooltipText);
    }

    /**
     * Sets up the tooltip text for the component
     */
    public void setToolTip(MecResourceBundle rb, String resourceKey) {
        this.setToolTipText(rb.getResourceString(resourceKey));
    }

    /**
     * Sets the max tooltip width in pixel
     */
    public void setTooltipWidth(int maxTooltipWidth) {
        this.maxTooltipWidth = maxTooltipWidth;
        this.setToolTipText(this.originalTooltipText);
    }

    /**
     * Returns the current set max width of the tooltip
     */
    public int getTooltipWidth() {
        return (this.maxTooltipWidth);
    }

    /**
     * Sets the colors of the all the components used in here
     */
    public void setColors(
            Color balloontipBackground,
            Color balloontipForeground,
            Color balloontipBorder) {
        this.customColorsUsed = true;
        this.balloontipBackground = balloontipBackground;
        this.balloontipForeground = balloontipForeground;
        this.balloontipBorder = balloontipBorder;
        this.setMultiresolutionIcons();
        if (this.balloonTip != null) {
            this.balloonTip.setColors(
                    this.balloontipBackground,
                    this.balloontipForeground,
                    this.balloontipBorder);
        }
    }

    /**
     * Rescale the icon - means the icon size could be set by the preferred size
     * of the widget
     */
    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);
        this.setMultiresolutionIcons();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        int x = this.getWidth() + GAP_X;
        int y = 0;
        if (this.balloonTip == null) {
            BalloonToolTip tempBalloonTip = new BalloonToolTip();
            tempBalloonTip.setTriangleAlignment(this.getTriangleAlignment());
            tempBalloonTip.setTipText(this.addWidthParameterToTooltipText(
                    this.originalTooltipText, this.maxTooltipWidth));
            int offsetY = 0;
            if (this.getTriangleAlignment() == TRIANGLE_ALIGNMENT_TOP) {
                offsetY = (int) (tempBalloonTip.getPreferredSize().height / 4);
            } else if (this.getTriangleAlignment() == TRIANGLE_ALIGNMENT_BOTTOM) {
                offsetY = -(int) (tempBalloonTip.getPreferredSize().height / 4);
            }
            y = this.jLabelImage.getHeight() / 2 - tempBalloonTip.getPreferredSize().height / 2 + offsetY;
        } else {
            int offsetY = 0;
            if (this.getTriangleAlignment() == TRIANGLE_ALIGNMENT_TOP) {
                offsetY = (int) (this.balloonTip.getPreferredSize().height / 4);
            } else if (this.getTriangleAlignment() == TRIANGLE_ALIGNMENT_BOTTOM) {
                offsetY = -(int) (this.balloonTip.getPreferredSize().height / 4);
            }
            y = this.jLabelImage.getHeight() / 2 - this.balloonTip.getPreferredSize().height / 2 + offsetY;
        }
        return new Point(x, y);
    }

    @Override
    public JToolTip createToolTip() {
        BalloonToolTip tooltip = new BalloonToolTip();
        tooltip.setTriangleAlignment(this.getTriangleAlignment());
        tooltip.setComponent(this);
        if (this.customColorsUsed) {
            tooltip.setColors(
                    this.balloontipBackground,
                    this.balloontipForeground,
                    this.balloontipBorder);
        }
        this.balloonTip = tooltip;
        return tooltip;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelImage = new javax.swing.JLabel();
        jPanelSpacer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/balloontip/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(jLabelImage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(jPanelSpacer, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @return the triangleAlignment.
     * One of
     * BalloonToolTipTRIANGLE_ALIGNMENT_CENTER, BalloonToolTipTRIANGLE_ALIGNMENT_TOP,
     * BalloonToolTipTRIANGLE_ALIGNMENT_BOTTOM
     */
    public int getTriangleAlignment() {
        return triangleAlignment;
    }

    /**
     * @param triangleAlignment the triangleAlignment to set. 
     * One of
     * BalloonToolTipTRIANGLE_ALIGNMENT_CENTER, BalloonToolTipTRIANGLE_ALIGNMENT_TOP,
     * BalloonToolTipTRIANGLE_ALIGNMENT_BOTTOM
     */
    public void setTriangleAlignment(int triangleAlignment) {
        this.triangleAlignment = triangleAlignment;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JPanel jPanelSpacer;
    // End of variables declaration//GEN-END:variables

}
