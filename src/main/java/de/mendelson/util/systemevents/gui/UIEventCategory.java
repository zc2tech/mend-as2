//$Header: /oftp2/de/mendelson/util/systemevents/gui/UIEventCategory.java 11    1/03/24 15:01 Heller $
package de.mendelson.util.systemevents.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.systemevents.ResourceBundleSystemEvent;
import de.mendelson.util.systemevents.SystemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Selectable event category in the UI
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class UIEventCategory implements Comparable<UIEventCategory> {

    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_FALLBACK
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/origin_system.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_CERTIFICATE
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/certificate.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_DATABASE
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/db.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_NOTIFICATION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/notification.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_CONFIGURATION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_configuration.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_TRANSACTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/messagedetails.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_SERVER
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_server.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_CONNECTIVITY
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/testconnection.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_PROCESSING
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_processing.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_LICENSE
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_license.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_FILEOPERATION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_fileoperation.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_CLIENT
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_client.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_QUOTA
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_quota.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_OTHER
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/category_other.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_XML_INTERFACE
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/xml_root.svg", 16, 64);
    public static final MendelsonMultiResolutionImage IMAGE_CATEGORY_REST_INTERFACE
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/rest.svg", 16, 64);
    
    private final int category;
    private final static MecResourceBundle rbSystemEvent;

    static {
        //Load resourcebundle
        try {
            rbSystemEvent = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEvent.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public UIEventCategory(int category) {
        this.category = category;
    }

    public static List<UIEventCategory> getAllSorted() {
        List<UIEventCategory> categoryList = new ArrayList<UIEventCategory>();
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_SERVER_COMPONENTS));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CONFIGURATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CONNECTIVITY));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CERTIFICATE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_DATABASE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_NOTIFICATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_PROCESSING));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_QUOTA));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_TRANSACTION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_FILE_OPERATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_OTHER));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_LICENSE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CLIENT_OPERATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_XML_INTERFACE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_REST_INTERFACE));
        Collections.sort(categoryList);
        return (categoryList);
    }

    @Override
    public String toString() {
        return (rbSystemEvent.getResourceString("category." + this.category));
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof UIEventCategory) {
            UIEventCategory entry = (UIEventCategory) anObject;
            return (entry.category == this.category);
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.category;
        return hash;
    }

    @Override
    public int compareTo(UIEventCategory otherCategory) {
        return (this.toString().compareTo(otherCategory.toString()));
    }

    public int getCategoryValue() {
        return (this.category);
    }

    public MendelsonMultiResolutionImage getImage() {
        return (getImageByCategory(this.category));
    }

    public static MendelsonMultiResolutionImage getImageByCategory(int category) {
        if (category == SystemEvent.CATEGORY_CERTIFICATE) {
            return (IMAGE_CATEGORY_CERTIFICATE);
        } else if (category == SystemEvent.CATEGORY_DATABASE) {
            return (IMAGE_CATEGORY_DATABASE);
        } else if (category == SystemEvent.CATEGORY_NOTIFICATION) {
            return (IMAGE_CATEGORY_NOTIFICATION);
        } else if (category == SystemEvent.CATEGORY_CONFIGURATION) {
            return (IMAGE_CATEGORY_CONFIGURATION);
        }else if (category == SystemEvent.CATEGORY_TRANSACTION) {
            return (IMAGE_CATEGORY_TRANSACTION);
        }else if (category == SystemEvent.CATEGORY_SERVER_COMPONENTS) {
            return (IMAGE_CATEGORY_SERVER);
        }else if (category == SystemEvent.CATEGORY_CONNECTIVITY) {
            return (IMAGE_CATEGORY_CONNECTIVITY);
        }else if (category == SystemEvent.CATEGORY_PROCESSING) {
            return (IMAGE_CATEGORY_PROCESSING);
        }else if (category == SystemEvent.CATEGORY_LICENSE) {
            return (IMAGE_CATEGORY_LICENSE);
        }else if (category == SystemEvent.CATEGORY_FILE_OPERATION) {
            return (IMAGE_CATEGORY_FILEOPERATION);
        }else if (category == SystemEvent.CATEGORY_CLIENT_OPERATION) {
            return (IMAGE_CATEGORY_CLIENT);
        }else if (category == SystemEvent.CATEGORY_QUOTA) {
            return (IMAGE_CATEGORY_QUOTA);
        }else if (category == SystemEvent.CATEGORY_OTHER) {
            return (IMAGE_CATEGORY_OTHER);
        }else if (category == SystemEvent.CATEGORY_XML_INTERFACE) {
            return (IMAGE_CATEGORY_XML_INTERFACE);
        }else if (category == SystemEvent.CATEGORY_REST_INTERFACE) {
            return (IMAGE_CATEGORY_REST_INTERFACE);
        }
        return (IMAGE_CATEGORY_FALLBACK);
    }

}
