/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.condition;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.CallEvent;
import com.wazeegroup.physhun.framework.AbstractTriggeredCondition;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsCallable extends AbstractTriggeredCondition {
    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(IsCallable.class);

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
    public IsCallable(WorkflowProcessorService workflowProcessorService) {
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
    public boolean evaluate(ProcessObject po, Object triggerEvent) {
        boolean result = false;

        if (po instanceof FsmProcessObject && triggerEvent instanceof CallEvent) {
            FsmProcessObject pgpo = (FsmProcessObject) po;
            CallEvent event = (CallEvent) triggerEvent;

            if (log.isDebugEnabled()) {
                log.debug("Checking if gambler with id: " + event.getGamblerId() + " can call...");
            }

            result = workflowProcessorService.isCallable(event.getGamblerId());

            if (log.isDebugEnabled()) {
                log.debug("Checking if gambler with id: " + event.getGamblerId() + " can call: " + result);
            }
        }

        return result;
    }
}
