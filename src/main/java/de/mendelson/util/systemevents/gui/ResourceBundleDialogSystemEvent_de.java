//$Header: /oftp2/de/mendelson/util/systemevents/gui/ResourceBundleDialogSystemEvent_de.java 15    9/12/24 15:51 Heller $
package de.mendelson.util.systemevents.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class ResourceBundleDialogSystemEvent_de extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"title", "Ansicht der Systemereignisse"},
        {"label.user", "Besitzer"},
        {"label.host", "Host"},
        {"label.id", "Ereignisnummer"},
        {"label.date", "Datum"},        
        {"label.type", "Typ"},
        {"label.category", "Kategorie"},
        {"header.timestamp", "Zeitstempel"},
        {"header.category", "Kategorie"},
        {"header.type", "Typ"},
        {"user.server.process", "Serverprozess" },
        {"label.startdate", "Start" },
        {"label.enddate", "Ende" },
        {"no.data", "Es gibt kein Systemereignis, das mit der aktuellen Datums-/Typenauswahl übereinstimmt." }, 
        {"label.freetext", "Suchtext" },
        {"label.freetext.hint", "Ereignisnummer oder Textsuche" },
        {"category.all", "-- Alle --" },
        {"label.close", "Schliessen" },
        {"label.search", "Ereignissuche" },
        {"label.resetfilter", "Zurücksetzen" },
    };
}
