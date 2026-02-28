//$Header: /as2/de/mendelson/util/DateChooserUI.java 11    27/06/24 12:21 Heller $
package de.mendelson.util;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDayChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * UI setup for used date choosers in mendelson software Call
 * jDateChooser.setUI(new DateChooserUI());
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class DateChooserUI extends PanelUI {

    private final static int IMAGE_HEIGHT = 18;

    private static final MendelsonMultiResolutionImage IMAGE_CALENDAR
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/calendar.svg",
                    IMAGE_HEIGHT);

    public static ComponentUI createUI(JComponent c) {
        return new DateChooserUI();
    }

    @Override
    public void installUI(JComponent component) {
        if (!(component instanceof JDateChooser)) {
            throw new IllegalArgumentException("setUI: JDateChooser expected - please pass this UI to JDateChoosers only");
        }
        Color panelBackgroundColor = UIManager.getColor("Panel.background");
        if( panelBackgroundColor == null ){
            panelBackgroundColor = Color.LIGHT_GRAY;
        }
        JDateChooser datechooser = (JDateChooser) component;
        datechooser.setPreferredSize(new Dimension(110, datechooser.getPreferredSize().height));
        JButton selectionButton = datechooser.getCalendarButton();
        selectionButton.setIcon(new ImageIcon(IMAGE_CALENDAR.toMinResolution(IMAGE_HEIGHT)));
        JCalendar calendar = datechooser.getJCalendar();
        Color decorationBackgroundColor;
        if( ColorUtil.calculateLuminance(panelBackgroundColor) > 128 ){
            decorationBackgroundColor = panelBackgroundColor.darker();
        }else{
            decorationBackgroundColor = panelBackgroundColor.brighter();
        }
        calendar.setDecorationBackgroundColor(decorationBackgroundColor);
        calendar.setDecorationBackgroundVisible(true);
        if (UIManager.getColor("Objects.RedStatus") != null) {
            calendar.setSundayForeground(UIManager.getColor("Objects.RedStatus"));
        } else {
            Color sundayForegroundColor = calendar.getSundayForeground();
            sundayForegroundColor = ColorUtil.getBestContrastColorAroundForeground(
                    panelBackgroundColor, sundayForegroundColor);
            calendar.setSundayForeground(sundayForegroundColor);
        }
        if (UIManager.getColor("Objects.Blue") != null) {
            calendar.setWeekdayForeground(UIManager.getColor("Objects.Blue"));
        } else {
            Color weekdayForegroundColor = calendar.getWeekdayForeground();
            weekdayForegroundColor = ColorUtil.getBestContrastColorAroundForeground(
                    panelBackgroundColor, weekdayForegroundColor);
            calendar.setWeekdayForeground(weekdayForegroundColor);
        }
        calendar.setWeekOfYearVisible(false);
        calendar.setMinSelectableDate(
                Date.from(LocalDate.of( 2000 , Month.JANUARY , 1 ).atStartOfDay(
                        ZoneId.systemDefault()).toInstant()));        
        JDayChooser dayChooser = calendar.getDayChooser();
        for (Component subComponent : dayChooser.getComponents()) {
            if (subComponent instanceof JComponent) {
                JComponent subsubJComponent = (JComponent) subComponent;
                for (Component subsubComponent : subsubJComponent.getComponents()) {
                    if (subsubComponent instanceof JButton) {
                        JButton button = (JButton) subsubComponent;
                        Dimension dimension = new Dimension(30, 30);
                        button.setMaximumSize(dimension);
                        button.setPreferredSize(dimension);
                        button.setMinimumSize(dimension);
                    }
                }
            }
        }
    }

}
