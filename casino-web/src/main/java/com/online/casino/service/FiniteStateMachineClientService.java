/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service;

import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import com.online.casino.service.fsm.event.ReQueuePlayerEvent;


/**
 * Created by Bjorn Harvold
 * Date: 3/13/11
 * Time: 7:52 PM
 * Responsibility:
 */
public interface FiniteStateMachineClientService {
    void dispatchQueuePlayerEvent(QueuePlayerEvent event);

    void dispatchReQueuePlayerEvent(ReQueuePlayerEvent event);

    void dispatchCallEvent(CallEvent event);

    void dispatchCheckEvent(CheckEvent event);

    void dispatchFoldEvent(FoldEvent event);

    void dispatchRaiseEvent(RaiseEvent event);

    void dispatchLeaveEvent(LeaveEvent event);
}
