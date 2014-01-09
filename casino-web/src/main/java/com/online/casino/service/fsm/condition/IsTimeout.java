/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.condition;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.TimeoutEvent;
import com.wazeegroup.physhun.framework.AbstractCondition;
import com.wazeegroup.physhun.framework.AbstractTriggeredCondition;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class IsTimeout extends AbstractTriggeredCondition {
    private final static Logger log = LoggerFactory.getLogger(IsTimeout.class);
    private final WorkflowProcessorService workflowProcessorService;

    @Value("${player.response.timeout.in.seconds:15}")
    private Integer playerResponseTimeoutInSeconds;

    /**
     * Field description
     */
    private Map<String, Boolean> timeoutTimers = new HashMap<String, Boolean>();

    @Autowired
    public IsTimeout(WorkflowProcessorService workflowProcessorService) {
        this.workflowProcessorService = workflowProcessorService;
    }

    @Override
    public boolean evaluate(ProcessObject po) {
        log.info("Nothing to do here");

        return false;
    }

    @Override
    public boolean evaluate(ProcessObject po, Object triggerEvent) {
        boolean result = false;

        if (po instanceof FsmProcessObject && triggerEvent instanceof TimeoutEvent) {
            FsmProcessObject pgpo = (FsmProcessObject) po;

            if (log.isDebugEnabled()) {
                log.debug("Checking if it is timeout for a gambler playing hand ID: " + pgpo.getHandId());
            }

            // this should always be true because the process that triggered this,
            // triggered it on the account of the timeout
            result = true;

            if (log.isDebugEnabled()) {
                log.debug("Timeout for gambler playing hand ID: " + pgpo.getHandId() + " is " + result);
            }
        }

        return result;
    }
}
