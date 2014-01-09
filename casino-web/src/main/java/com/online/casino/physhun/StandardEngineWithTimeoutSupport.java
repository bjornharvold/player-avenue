/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.physhun;

import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.service.fsm.event.TimeoutEvent;
import com.wazeegroup.physhun.engine.StandardEngine;
import com.wazeegroup.physhun.engine.StateEngine;
import com.wazeegroup.physhun.framework.ProcessObject;
import com.wazeegroup.physhun.framework.State;
import com.wazeegroup.physhun.framework.StateModel;
import com.wazeegroup.physhun.framework.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bjorn Harvold
 * Date: 5/20/11
 * Time: 3:34 PM
 * Responsibility:
 */
public class StandardEngineWithTimeoutSupport extends StandardEngine implements StateEngine {
    private final static Logger log = LoggerFactory.getLogger(StandardEngineWithTimeoutSupport.class);
    private Map<String, Timer> timeoutTimers = new ConcurrentHashMap<String, Timer>();
    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void processTriggerEvent(StateModel stateModel, ProcessObject processObject, Object triggerEvent) {
        // first thing we want to do is we need to cancel any currently running timers for the processObject
        // so they don't interfere with
        cancelPotentialTimeoutTransitionForState(processObject.getID());

        super.processTriggerEvent(stateModel, processObject, triggerEvent);
    }

    @Override
    protected void process(StateModel stateModel, ProcessObject processObject) {
        // first thing we want to do is we need to cancel any currently running timers for the processObject
        // so they don't interfere with
        cancelPotentialTimeoutTransitionForState(processObject.getID());

        super.process(stateModel, processObject);

        // ok so now we transitioned to a new state
        // if this new state has a transition on it of type ConcreteTimeoutTransition
        // we need to start the timer for that transition
        initializePotentialTimeoutTransitionForState(stateModel, processObject);
    }

    /**
     * @param stateModel    stateModel
     * @param processObject processObject
     */
    private void initializePotentialTimeoutTransitionForState(final StateModel stateModel, final ProcessObject processObject) {
        // find the current state we're in
        State currentState = stateModel.getState(processObject.getActiveStates()[0]);

        // find all transitions for state
        Transition[] applicableTransitions = stateModel.getTransitionsFromState(currentState);

        // filter out ConcreteTimeoutTransitions and retrieve the first one
        final ConcreteTimeoutTransition identifiedTransition = identifyValidTimeoutTransition(applicableTransitions, processObject);

        // kick off a timer based on the timeout on the transition
        if (identifiedTransition != null) {
            initiateTimeout(processObject.getID(), identifiedTransition.getTimeoutInSeconds(), new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (processObject instanceof FsmProcessObject) {
                            FsmProcessObject fsm = (FsmProcessObject) processObject;
                            // bind entity manager transaction to session
                            EntityManager em = entityManagerFactory.createEntityManager();
                            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));

                            // get the latest process object
                            ProcessObject po = getPersistenceSupport().getPersistedObject(fsm.getId());

                            // the po might have been removed
                            if (po != null) {
                                // dispatch trigger event
                                processTriggerEvent(stateModel, po, new TimeoutEvent(fsm.getId(), fsm.getHandId()));
                            }
                        }
                    } finally {
                        // unbind transaction
                        EntityManagerHolder emHolder = (EntityManagerHolder)
                                TransactionSynchronizationManager.unbindResource(entityManagerFactory);
                        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
                    }
                }
            });
        }
    }

    private void initiateTimeout(String processId, Integer timeoutInSeconds, TimerTask task) {
        Timer timeoutTimer = new Timer(processId, false);
        timeoutTimer.schedule(task, timeoutInSeconds * 1000);

        if (log.isDebugEnabled()) {
            log.debug("Initiating timer for processId: " + processId);
        }

        timeoutTimers.put(processId, timeoutTimer);
    }

    /**
     * Call this after someone dispatches an event that moves the process away from the current state
     *
     * @param processId processId
     */
    private void cancelPotentialTimeoutTransitionForState(String processId) {
        if (timeoutTimers.containsKey(processId)) {
            if (log.isDebugEnabled()) {
                log.debug("Cancelling timer for processId: " + processId);
            }
            timeoutTimers.get(processId).cancel();
            timeoutTimers.remove(processId);
        }
    }

    /**
     * It will look for and return the first ConcreteTimeoutTransition it finds for that state.
     *
     * @param applicableTransitions applicableTransitions
     * @param processObject         processObject
     * @return ConcreteTimeoutTransition
     */
    private ConcreteTimeoutTransition identifyValidTimeoutTransition(Transition[] applicableTransitions, ProcessObject processObject) {
        ConcreteTimeoutTransition identifiedTransition = null;

        for (int i = 0; i < applicableTransitions.length && identifiedTransition == null; i++) {
            Transition applicableTransition = applicableTransitions[i];

            if (applicableTransition instanceof ConcreteTimeoutTransition) {
                _log.debug("Current state has a ConcreteTimeoutTransition");
                identifiedTransition = (ConcreteTimeoutTransition) applicableTransition;
            }
        }

        return identifiedTransition;
    }
}
