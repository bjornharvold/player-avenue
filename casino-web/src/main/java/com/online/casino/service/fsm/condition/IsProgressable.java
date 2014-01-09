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
public class IsProgressable extends AbstractCondition {
    private final static Logger log = LoggerFactory.getLogger(IsProgressable.class);
    private final WorkflowProcessorService workflowProcessorService;

    @Autowired
    public IsProgressable(WorkflowProcessorService workflowProcessorService) {
        this.workflowProcessorService = workflowProcessorService;
    }

    @Override
    public boolean evaluate(ProcessObject po) {
        boolean result = false;

        if (po instanceof FsmProcessObject) {
            FsmProcessObject pgpo = (FsmProcessObject) po;

            if (log.isDebugEnabled()) {
                log.debug("Checking if we can progress hand with id: " + pgpo.getHandId());
            }

            result = workflowProcessorService.isProgressable(pgpo.getHandId());

            if (log.isDebugEnabled()) {
                log.debug("Checking if we can progress hand: " + result);
            }
        }

        return result;
    }
}
