/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.action;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.CheckEvent;
import com.wazeegroup.physhun.framework.AbstractAction;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAction extends AbstractAction {
    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(CheckAction.class);

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
    public CheckAction(WorkflowProcessorService workflowProcessorService) {
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
        if (log.isDebugEnabled()) {
            log.debug("Nothing to do here");
        }
    }

    /**
     * Method description
     *
     * @param po           po
     * @param triggerEvent triggerEvent
     */
    @Override
    public void execute(ProcessObject po, Object triggerEvent) {
        if ((po instanceof FsmProcessObject) && (triggerEvent instanceof CheckEvent)) {
            FsmProcessObject pgpo = (FsmProcessObject) po;
            CheckEvent event = (CheckEvent) triggerEvent;

            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + event.getGamblerId() + " checks");
            }

            workflowProcessorService.doCheck(event);

            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + event.getGamblerId() + " checked successfully");
            }
        }
    }
}
