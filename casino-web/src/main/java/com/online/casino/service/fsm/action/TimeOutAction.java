/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.action;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.TimeoutEvent;
import com.wazeegroup.physhun.framework.AbstractAction;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimeOutAction extends AbstractAction {
    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(TimeOutAction.class);

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
    public TimeOutAction(WorkflowProcessorService workflowProcessorService) {
        this.workflowProcessorService = workflowProcessorService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param po po
     */
    @Override
    public void execute(ProcessObject po) {
        log.info("Nothing to do here");
    }

    @Override
    public void execute(ProcessObject po, Object triggerEvent) {
        if (po instanceof FsmProcessObject && triggerEvent instanceof TimeoutEvent) {
            FsmProcessObject pgpo = (FsmProcessObject) po;

            if (log.isDebugEnabled()) {
                log.debug("Timing out current gambler playing hand id: " + pgpo.getHandId());
            }

            workflowProcessorService.doTimeout(pgpo.getId(), pgpo.getHandId());

            if (log.isDebugEnabled()) {
                log.debug("Timed out current gambler playing hand id: " + pgpo.getHandId());
            }
        }
    }
}
