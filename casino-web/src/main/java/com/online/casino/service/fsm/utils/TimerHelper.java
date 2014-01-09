/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.fsm.utils;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 1/19/11
 * Time: 10:07 PM
 * Responsibility: In-memory timer to track how long a gambler takes to execute an action. This will be used by
 * timing out a gambler.
 */
public class TimerHelper {

    /** Field description */
    private static Map<String, Long> gameTime = new HashMap<String, Long>();

    //~--- methods ------------------------------------------------------------

    /**
     * Returns (in seconds) the time since the timer started.
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Time (in seconds) since the timer started
     */
    public static Long checkTime(String pokergameId) {
        Long result = 0L;

        if (gameTime.containsKey(pokergameId)) {
            result = (System.currentTimeMillis() - gameTime.get(pokergameId)) / 1000L;
        }

        return result;
    }

    /**
     * Starts a timer for a specific game. It records the start time in a map
     *
     *
     * @param pokergameId pokergameId
     */
    public static void startTime(String pokergameId) {
        gameTime.put(pokergameId, System.currentTimeMillis());
    }

    /**
     * Deletes the entry from the timer
     *
     *
     * @param pokergameId pokergameId
     */
    public void stopTime(String pokergameId) {
        gameTime.remove(pokergameId);
    }
}
