/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by Bjorn Harvold
 * Date: 12/20/10
 * Time: 4:07 AM
 * Responsibility:
 */
//@Named
//@Singleton
//@Service("monitor")
public class Monitor {
    private final static Logger log = LoggerFactory.getLogger(Monitor.class);

    @PostConstruct
    public void init() {
        if (log.isInfoEnabled()) {
            log.info("Monitor Service Initialized");
        }
    }

    @Listener("/meta/subscribe")
    public void monitorSubscribe(ServerSession session, ServerMessage message) {
        if (log.isInfoEnabled()) {
            log.info("Monitored Subscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }
    }

    @Listener("/meta/unsubscribe")
    public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
        if (log.isInfoEnabled()) {
            log.info("Monitored Unsubscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }
    }

    @Listener("/meta/*")
    public void monitorMeta(ServerSession session, ServerMessage message) {
        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        }
    }
}
