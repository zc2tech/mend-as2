//$Header: /mec_oftp2/de/mendelson/util/ha/HAAccessDB.java 4     13/03/25 13:24 Heller $
package de.mendelson.util.ha;

import de.mendelson.util.systemevents.SystemEventManager;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the HA for the mendelson OFTP2
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class HAAccessDB {

    public HAAccessDB(SystemEventManager a) {
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        String revision = "$Revision: 4 $";
        return (revision.substring(revision.indexOf(":") + 1,
                revision.lastIndexOf("$")).trim());
    }

    public void deleteOlderThan(Object a, long c) {        
    }

    public List<ServerInstanceHA> getServerInstanceHA(Object a, long b) {
        List<ServerInstanceHA> list = new ArrayList<ServerInstanceHA>();
        return (list);
    }

    public List<ServerInstanceHA> getServerInstanceHAAsTransaction(Object a,
            Object b, long c) {
        List<ServerInstanceHA> instanceList = new ArrayList<ServerInstanceHA>();
        return (instanceList);
    }

    public void updateAsTransaction(Object a, Object b, Object c) {
    }

}
