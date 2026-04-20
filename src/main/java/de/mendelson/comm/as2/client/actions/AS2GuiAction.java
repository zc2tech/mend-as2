/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

package de.mendelson.comm.as2.client.actions;

import de.mendelson.comm.as2.client.AS2Gui;

/**
 * Base class for all AS2Gui actions.
 * Provides access to the parent GUI and implements the Command pattern.
 *
 * @author Julian Xu
 */
public abstract class AS2GuiAction {

    protected final AS2Gui gui;

    protected AS2GuiAction(AS2Gui gui) {
        this.gui = gui;
    }

    /**
     * Execute the action
     */
    public abstract void execute();

    /**
     * Get the action name for logging/debugging
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
