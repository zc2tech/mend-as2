/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.client;

import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.MendelsonMultiResolutionImage.SVGScalingOption;
import javax.swing.ImageIcon;

/**
 * Centralized icon management for AS2 GUI.
 * All application icons are loaded and accessed through this class.
 *
 * @author Julian Xu
 */
public class IconManager {

    // Image size constants
    public static final int IMAGE_SIZE_POPUP = 20;
    public static final int IMAGE_SIZE_MENU_ITEM = 20;
    public static final int IMAGE_SIZE_TOOLBAR = 24;
    public static final int IMAGE_SIZE_DIALOG = 32;
    public static final int IMAGE_SIZE_TREENODE = 18;
    public static final int IMAGE_SIZE_LIST = 18;
    public static final int IMAGE_SIZE_TABLE = 18;

    // Multi-resolution images
    private static MendelsonMultiResolutionImage IMAGE_DELETE;
    private static MendelsonMultiResolutionImage IMAGE_FILTER;
    private static MendelsonMultiResolutionImage IMAGE_FILTER_ACTIVE;
    private static MendelsonMultiResolutionImage IMAGE_MESSAGE_DETAILS;
    private static MendelsonMultiResolutionImage IMAGE_CERTIFICATE;
    private static MendelsonMultiResolutionImage IMAGE_MANUAL_SEND;
    private static MendelsonMultiResolutionImage IMAGE_PARTNER;
    private static MendelsonMultiResolutionImage IMAGE_USER_MANAGEMENT;
    private static MendelsonMultiResolutionImage IMAGE_STOP;
    private static MendelsonMultiResolutionImage IMAGE_COLUMN;
    private static MendelsonMultiResolutionImage IMAGE_LOG_SEARCH;
    private static MendelsonMultiResolutionImage IMAGE_PORTS;
    private static MendelsonMultiResolutionImage IMAGE_EXIT;
    private static MendelsonMultiResolutionImage IMAGE_PREFERENCES;
    private static MendelsonMultiResolutionImage IMAGE_PRODUCT_LOGO_WITH_TEXT;
    private static MendelsonMultiResolutionImage IMAGE_PRODUCT_LOGO;
    private static MendelsonMultiResolutionImage IMAGE_PENDING;
    private static MendelsonMultiResolutionImage IMAGE_STOPPED;
    private static MendelsonMultiResolutionImage IMAGE_FINISHED;
    private static MendelsonMultiResolutionImage IMAGE_HIDE;
    private static MendelsonMultiResolutionImage IMAGE_SYSINFO;
    private static MendelsonMultiResolutionImage IMAGE_HOURGLASS;
    private static MendelsonMultiResolutionImage IMAGE_TRACKER_CONFIG;
    private static MendelsonMultiResolutionImage IMAGE_MY_TRACKER_CONFIG;
    private static MendelsonMultiResolutionImage IMAGE_TRACKER_MESSAGES;

    private static boolean initialized = false;

    /**
     * Private constructor to prevent instantiation
     */
    private IconManager() {
    }

    /**
     * Initialize all icons. This method is idempotent - calling it multiple times is safe.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        IMAGE_DELETE = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/delete.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_FILTER = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/filter.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_FILTER_ACTIVE = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/filter_active.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_MESSAGE_DETAILS = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/messagedetails.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_CERTIFICATE = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/util/security/cert/certificate.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_MANUAL_SEND = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/send.svg", IMAGE_SIZE_MENU_ITEM, IMAGE_SIZE_TOOLBAR * 2);
        IMAGE_PARTNER = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/partner/gui/singlepartner.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_USER_MANAGEMENT = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/usermanagement/gui/usermanagement.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_STOP = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/stop.svg", IMAGE_SIZE_TOOLBAR);
        IMAGE_COLUMN = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/util/tables/hideablecolumns/column.svg", IMAGE_SIZE_TOOLBAR);
        IMAGE_LOG_SEARCH = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/util/clientserver/log/search/gui/magnifying_glass.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_PORTS = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/util/httpconfig/gui/ports.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_EXIT = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/exit.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_PREFERENCES = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/preferences/preferences.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_PRODUCT_LOGO_WITH_TEXT = MendelsonMultiResolutionImage.fromSVG(
                getLogoPath("logo_open_source_with_text.svg"), 100);
        IMAGE_PRODUCT_LOGO = MendelsonMultiResolutionImage.fromSVG(
                getLogoPath("logo_open_source.svg"), 16, 128);
        IMAGE_PENDING = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/message/loggui/state_pending.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_STOPPED = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/message/loggui/state_stopped.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_FINISHED = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/message/loggui/state_finished.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_HIDE = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/hide.svg", IMAGE_SIZE_MENU_ITEM, IMAGE_SIZE_MENU_ITEM * 2,
                SVGScalingOption.KEEP_HEIGHT);
        IMAGE_SYSINFO = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/util/systemevents/gui/sysinfo.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_HOURGLASS = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/client/hourglass.svg", IMAGE_SIZE_MENU_ITEM, IMAGE_SIZE_TOOLBAR * 2);
        IMAGE_TRACKER_CONFIG = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/tracker/gui/tracker_config.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_MY_TRACKER_CONFIG = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/tracker/gui/my_tracker_config.svg", IMAGE_SIZE_MENU_ITEM);
        IMAGE_TRACKER_MESSAGES = MendelsonMultiResolutionImage.fromSVG(
                "/de/mendelson/comm/as2/tracker/gui/tracker_messages.svg", IMAGE_SIZE_MENU_ITEM);

        initialized = true;
    }

    /**
     * Helper method to determine logo path based on test mode
     * Test mode is detected by checking if the system property is set
     */
    private static String getLogoPath(String defaultLogoName) {
        boolean isTestMode = Boolean.parseBoolean(System.getProperty("mend.as2.testmode", "false"));
        if (isTestMode) {
            // Replace .svg with _test.svg
            return "/de/mendelson/comm/as2/client/" + defaultLogoName.replace(".svg", "_test.svg");
        } else {
            return "/de/mendelson/comm/as2/client/" + defaultLogoName;
        }
    }

    // Icon getters - return ImageIcon sized for specific contexts

    public static ImageIcon getDeleteIcon(int size) {
        return new ImageIcon(IMAGE_DELETE.toMinResolution(size));
    }

    public static ImageIcon getFilterIcon(int size) {
        return new ImageIcon(IMAGE_FILTER.toMinResolution(size));
    }

    public static ImageIcon getFilterActiveIcon(int size) {
        return new ImageIcon(IMAGE_FILTER_ACTIVE.toMinResolution(size));
    }

    public static ImageIcon getMessageDetailsIcon(int size) {
        return new ImageIcon(IMAGE_MESSAGE_DETAILS.toMinResolution(size));
    }

    public static ImageIcon getCertificateIcon(int size) {
        return new ImageIcon(IMAGE_CERTIFICATE.toMinResolution(size));
    }

    public static ImageIcon getManualSendIcon(int size) {
        return new ImageIcon(IMAGE_MANUAL_SEND.toMinResolution(size));
    }

    public static ImageIcon getPartnerIcon(int size) {
        return new ImageIcon(IMAGE_PARTNER.toMinResolution(size));
    }

    public static ImageIcon getUserManagementIcon(int size) {
        return new ImageIcon(IMAGE_USER_MANAGEMENT.toMinResolution(size));
    }

    public static ImageIcon getStopIcon(int size) {
        return new ImageIcon(IMAGE_STOP.toMinResolution(size));
    }

    public static ImageIcon getColumnIcon(int size) {
        return new ImageIcon(IMAGE_COLUMN.toMinResolution(size));
    }

    public static ImageIcon getLogSearchIcon(int size) {
        return new ImageIcon(IMAGE_LOG_SEARCH.toMinResolution(size));
    }

    public static ImageIcon getPortsIcon(int size) {
        return new ImageIcon(IMAGE_PORTS.toMinResolution(size));
    }

    public static ImageIcon getExitIcon(int size) {
        return new ImageIcon(IMAGE_EXIT.toMinResolution(size));
    }

    public static ImageIcon getPreferencesIcon(int size) {
        return new ImageIcon(IMAGE_PREFERENCES.toMinResolution(size));
    }

    public static ImageIcon getProductLogoWithTextIcon(int size) {
        return new ImageIcon(IMAGE_PRODUCT_LOGO_WITH_TEXT.toMinResolution(size));
    }

    public static ImageIcon getProductLogoIcon(int size) {
        return new ImageIcon(IMAGE_PRODUCT_LOGO.toMinResolution(size));
    }

    public static ImageIcon getPendingIcon(int size) {
        return new ImageIcon(IMAGE_PENDING.toMinResolution(size));
    }

    public static ImageIcon getStoppedIcon(int size) {
        return new ImageIcon(IMAGE_STOPPED.toMinResolution(size));
    }

    public static ImageIcon getFinishedIcon(int size) {
        return new ImageIcon(IMAGE_FINISHED.toMinResolution(size));
    }

    public static ImageIcon getHideIcon(int size) {
        return new ImageIcon(IMAGE_HIDE.toMinResolution(size));
    }

    public static ImageIcon getSysinfoIcon(int size) {
        return new ImageIcon(IMAGE_SYSINFO.toMinResolution(size));
    }

    public static ImageIcon getHourglassIcon(int size) {
        return new ImageIcon(IMAGE_HOURGLASS.toMinResolution(size));
    }

    public static ImageIcon getTrackerConfigIcon(int size) {
        return new ImageIcon(IMAGE_TRACKER_CONFIG.toMinResolution(size));
    }

    public static ImageIcon getMyTrackerConfigIcon(int size) {
        return new ImageIcon(IMAGE_MY_TRACKER_CONFIG.toMinResolution(size));
    }

    public static ImageIcon getTrackerMessagesIcon(int size) {
        return new ImageIcon(IMAGE_TRACKER_MESSAGES.toMinResolution(size));
    }

    // Convenience methods for common sizes

    public static ImageIcon getDeleteIconToolbar() {
        return getDeleteIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getDeleteIconPopup() {
        return getDeleteIcon(IMAGE_SIZE_POPUP);
    }

    public static ImageIcon getFilterIconToolbar() {
        return getFilterIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getMessageDetailsIconToolbar() {
        return getMessageDetailsIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getMessageDetailsIconPopup() {
        return getMessageDetailsIcon(IMAGE_SIZE_POPUP);
    }

    public static ImageIcon getCertificateIconToolbar() {
        return getCertificateIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getCertificateIconMenuItem() {
        return getCertificateIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getManualSendIconMenuItem() {
        return getManualSendIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getManualSendIconPopup() {
        return getManualSendIcon(IMAGE_SIZE_POPUP);
    }

    public static ImageIcon getPartnerIconToolbar() {
        return getPartnerIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getPartnerIconMenuItem() {
        return getPartnerIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getUserManagementIconToolbar() {
        return getUserManagementIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getUserManagementIconMenuItem() {
        return getUserManagementIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getLogSearchIconMenuItem() {
        return getLogSearchIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getStopIconToolbar() {
        return getStopIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getColumnIconToolbar() {
        return getColumnIcon(IMAGE_SIZE_TOOLBAR);
    }

    public static ImageIcon getPortsIconMenuItem() {
        return getPortsIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getExitIconMenuItem() {
        return getExitIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getPreferencesIconMenuItem() {
        return getPreferencesIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getProductLogoIconMenuItem() {
        return getProductLogoIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getPendingIconMenuItem() {
        return getPendingIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getStoppedIconMenuItem() {
        return getStoppedIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getFinishedIconMenuItem() {
        return getFinishedIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getHideIconMenuItem() {
        return getHideIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getSysinfoIconMenuItem() {
        return getSysinfoIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getTrackerConfigIconMenuItem() {
        return getTrackerConfigIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getMyTrackerConfigIconMenuItem() {
        return getMyTrackerConfigIcon(IMAGE_SIZE_MENU_ITEM);
    }

    public static ImageIcon getTrackerMessagesIconMenuItem() {
        return getTrackerMessagesIcon(IMAGE_SIZE_MENU_ITEM);
    }

    // Multi-resolution image getters (for cases where ImageIcon is not needed)

    public static MendelsonMultiResolutionImage getProductLogoWithText() {
        return IMAGE_PRODUCT_LOGO_WITH_TEXT;
    }

    public static MendelsonMultiResolutionImage getProductLogo() {
        return IMAGE_PRODUCT_LOGO;
    }

    public static MendelsonMultiResolutionImage getPreferences() {
        return IMAGE_PREFERENCES;
    }

    public static MendelsonMultiResolutionImage getHourglass() {
        return IMAGE_HOURGLASS;
    }
}
