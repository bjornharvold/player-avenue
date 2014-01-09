/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.fsm.action;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.CallEvent;
import com.wazeegroup.physhun.framework.AbstractAction;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/12/18
 */
@Component
public class CallAction extends AbstractAction {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(CallAction.class);

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
    public CallAction(WorkflowProcessorService workflowProcessorService) {
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
        if ((po instanceof FsmProcessObject) && (triggerEvent instanceof CallEvent)) {
            FsmProcessObject pgpo = (FsmProcessObject) po;
            CallEvent event = (CallEvent) triggerEvent;

            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + event.getGamblerId() + " calls");
            }

            workflowProcessorService.doCall(event);

            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + event.getGamblerId() + " called successfully");
            }
        }
    }
}
