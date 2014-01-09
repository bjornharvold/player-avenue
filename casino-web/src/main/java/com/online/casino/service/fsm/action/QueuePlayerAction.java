/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.action;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.wazeegroup.physhun.framework.AbstractAction;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueuePlayerAction extends AbstractAction {
    private final static Logger log = LoggerFactory.getLogger(QueuePlayerAction.class);
    private final WorkflowProcessorService workflowProcessorService;

    @Autowired
    public QueuePlayerAction(WorkflowProcessorService workflowProcessorService) {
        this.workflowProcessorService = workflowProcessorService;
    }

    @Override
    public void execute(ProcessObject po) {
        if (log.isDebugEnabled()) {
            log.debug("Nothing to do here");
        }
    }

    @Override
    public void execute(ProcessObject po, Object triggerEvent) {
        FsmProcessObject pgpo = (FsmProcessObject) po;
        QueuePlayerEvent event = (QueuePlayerEvent) triggerEvent;

        workflowProcessorService.doQueuePlayer(event);
    }
}
