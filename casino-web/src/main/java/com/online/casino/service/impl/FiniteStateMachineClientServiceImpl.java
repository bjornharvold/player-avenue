/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import com.online.casino.service.fsm.event.ReQueuePlayerEvent;
import com.wazeegroup.physhun.engine.ProcessContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/13/11
 * Time: 7:47 PM
 * Responsibility: Anyone who needs to kick off an event or process related to either the FSM container
 * or the FSM service should go through this service.
 */
@Service("finiteStateMachineClientService")
public class FiniteStateMachineClientServiceImpl implements FiniteStateMachineClientService {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(FiniteStateMachineClientServiceImpl.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final ProcessContainer processContainer;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param processContainer processContainer
     */
    @Autowired
    public FiniteStateMachineClientServiceImpl(ProcessContainer processContainer) {
        this.processContainer = processContainer;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchCallEvent(CallEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    /**
     * Method description
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchCheckEvent(CheckEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    /**
     * Method description
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchFoldEvent(FoldEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    /**
     * Method description
     *
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchLeaveEvent(LeaveEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    /**
     * Method description
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchQueuePlayerEvent(QueuePlayerEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchReQueuePlayerEvent(ReQueuePlayerEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }

    /**
     * Method description
     *
     * @param event event
     */
    @PreAuthorize("hasRole('RIGHT_PLAY_GAME')")
    @Override
    public void dispatchRaiseEvent(RaiseEvent event) {
        processContainer.sendTriggerEventToProcess(event.getPokergameId(), event);
    }
}
