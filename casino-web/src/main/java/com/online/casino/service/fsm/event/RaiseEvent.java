/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.fsm.event;

//~--- JDK imports ------------------------------------------------------------

import java.math.BigDecimal;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/12/18
 * @author         Bjorn Harvold
 */
public final class RaiseEvent extends AbstractGamblerEvent {

    /** Field description */
    private final BigDecimal amount;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param applicationUserId applicationUserId
     * @param gamblerId gamblerId
     * @param amount amount
     */
    public RaiseEvent(String pokergameId, String handId, String applicationUserId, String gamblerId, BigDecimal amount) {
        super(pokergameId, handId, applicationUserId, gamblerId);
        this.amount = amount;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getAmount() {
        return amount;
    }
}
