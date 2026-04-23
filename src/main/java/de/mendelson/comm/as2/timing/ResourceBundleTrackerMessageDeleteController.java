package de.mendelson.comm.as2.timing;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * ResourceBundle to localize the tracker message delete controller
 *
 * @author Julian Xu
 */
public class ResourceBundleTrackerMessageDeleteController extends MecResourceBundle {

    private static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    private static final Object[][] CONTENTS = {
        {"autodelete", "Tracker message maintenance: Deleted {0} tracker message(s) older than {1} day(s)"},
        {"tracker.deleted.system", "Tracker messages deleted by system maintenance"},
        {"tracker.delete.setting.olderthan", "Delete tracker messages older than {0}"},
        {"tracker.delete.count", "Number of tracker messages deleted: {0}"},};

}
