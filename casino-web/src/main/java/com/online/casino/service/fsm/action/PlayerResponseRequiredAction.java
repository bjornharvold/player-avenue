/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.fsm.action;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
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
public class PlayerResponseRequiredAction extends AbstractAction {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(PlayerResponseRequiredAction.class);

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
    public PlayerResponseRequiredAction(WorkflowProcessorService workflowProcessorService) {
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
        if (po instanceof FsmProcessObject) {
            FsmProcessObject pgpo = (FsmProcessObject) po;

            if (log.isDebugEnabled()) {
                log.debug("Player response required of gambler for hand id: " + pgpo.getHandId());
            }

            workflowProcessorService.doPlayerResponseRequired(pgpo.getHandId());

            if (log.isDebugEnabled()) {
                log.debug("Player response required action dispatched for hand id: " + pgpo.getHandId());
            }
        }
    }
}
