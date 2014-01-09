/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameEvent;

//~--- interfaces -------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 4/9/11
 * Time: 12:55 AM
 * Responsibility:
 */
public interface GameBroadcast {

    /**
     * Method description
     *
     *
     * @return Return value
     */
    GameEvent getEvent();

    /**
     * Method description
     *
     *
     * @return Return value
     */
    String getPokergameId();
}
