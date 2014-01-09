/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.condition;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.wazeegroup.physhun.framework.AbstractCondition;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsEndGame extends AbstractCondition {
    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(IsPostSmallBlind.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    private final WorkflowProcessorService workflowProcessorService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param workflowProcessorService workflowProcessorService
     */
    @Autowired
    public IsEndGame(WorkflowProcessorService workflowProcessorService) {
        this.workflowProcessorService = workflowProcessorService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param po po
     * @return Return value
     */
    @Override
    public boolean evaluate(ProcessObject po) {
        boolean result = false;

        if (po instanceof FsmProcessObject) {
            if (log.isDebugEnabled()) {
                log.debug("Checking if hand has reached end game...");
            }

            FsmProcessObject pgpo = (FsmProcessObject) po;

            result = workflowProcessorService.isEndGame(pgpo.getHandId());

            if (log.isDebugEnabled()) {
                log.debug("Hand with id: " + pgpo.getHandId() + " end game: " + result);
            }
        }

        return result;
    }
}
