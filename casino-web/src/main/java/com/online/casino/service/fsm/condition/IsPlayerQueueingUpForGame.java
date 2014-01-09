/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.condition;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.wazeegroup.physhun.framework.AbstractTriggeredCondition;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsPlayerQueueingUpForGame extends AbstractTriggeredCondition {
    private final static Logger log = LoggerFactory.getLogger(IsPlayerQueueingUpForGame.class);

    @Override
    public boolean evaluate(ProcessObject po, Object triggerEvent) {
        boolean result = false;

        if (triggerEvent instanceof QueuePlayerEvent && po instanceof FsmProcessObject) {
            if (log.isDebugEnabled()) {
                log.debug("Checking if player is queueing up for game...");
            }
            FsmProcessObject pgpo = (FsmProcessObject) po;
            QueuePlayerEvent event = (QueuePlayerEvent) triggerEvent;

            if (pgpo.getId().equals(event.getPokergameId())) {
                result = true;
            }
            if (log.isDebugEnabled()) {
                log.debug("Checking if player is queueing up for game: " + result);
            }
        }

        return result;
    }
}
